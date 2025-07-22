package com.github.viewer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.viewer.model.Branch;
import com.github.viewer.model.Repository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = GithubViewerIntegrationTest.WireMockInit.class)
class GithubViewerIntegrationTest {

	private static final WireMockServer WM = new WireMockServer(0);

	@BeforeAll
	static void start() { WM.start(); }

	@AfterAll
	static void stop() { WM.stop(); }

	static class WireMockInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext ctx) {
			TestPropertyValues.of(
					"gitHub.url=http://localhost:" + WM.port()
			).applyTo(ctx.getEnvironment());
		}
	}

	@Autowired
	private TestRestTemplate rest;

	@Test
	void shouldReturnsNonForkReposWithBranches() {
		WM.stubFor(get(urlEqualTo("/users/patryk/repos"))
				.willReturn(okJson("""
                [
                  {"name":"demo","fork":false,"owner":{"login":"patryk"}},
                  {"name":"forked","fork":true,"owner":{"login":"patryk"}}
                ]
                """)));

		WM.stubFor(get(urlEqualTo("/repos/patryk/demo/branches"))
				.willReturn(okJson("""
                [
                  {"name":"main","commit":{"sha":"111"}},
                  {"name":"dev","commit":{"sha":"222"}}
                ]
                """)));
		WM.stubFor(get(urlEqualTo("/repos/patryk/forked/branches"))
				.willReturn(okJson("""
                [
                  {"name":"prep","commit":{"sha":"333"}},
                  {"name":"prod","commit":{"sha":"444"}}
                ]
                """)));

		var response = rest.getForEntity(
				"/api/v1/github/users/patryk/repositories",
				Repository[].class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var body = response.getBody();
		assertThat(body).isNotNull().hasSize(2);

		Repository demo = body[0];
		assertThat(demo.repositoryName()).isEqualTo("demo");
		assertThat(demo.ownerLogin()).isEqualTo("patryk");
		assertThat(demo.branches()).hasSize(2);
		assertThat(demo.branches())
				.extracting(Branch::branchName, Branch::lastCommitSha)
				.containsExactly(
						tuple("main", "111"),
						tuple("dev",  "222")
				);

		Repository forked = body[1];
		assertThat(forked.repositoryName()).isEqualTo("forked");
		assertThat(forked.ownerLogin()).isEqualTo("patryk");
		assertThat(forked.branches()).hasSize(2);
		assertThat(forked.branches())
				.extracting(Branch::branchName, Branch::lastCommitSha)
				.containsExactly(
						tuple("prep", "333"),
						tuple("prod", "444")
				);
	}
}