package com.github.viewer.client;

import com.github.viewer.dto.BranchResponseDTO;
import com.github.viewer.dto.RepositoryResponseDTO;
import com.github.viewer.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.viewer.util.CollectionUtil.safeStream;

@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final RestTemplate restTemplate;

    @Value("${github.repositories.url}")
    private String repositoriesUrl;

    @Value("${github.branches.url}")
    private String branchesUrl;

    public List<RepositoryResponseDTO> listNonForkRepos(String user) {
        try {
            var repositories = getRepositories(user);

            return safeStream(Objects.isNull(repositories)
                    ? Collections.emptyList()
                    : Arrays.asList(repositories))
                    .filter(Objects::nonNull)
                    .toList();

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException(user);
            }
            throw ex;
        }
    }

    public List<BranchResponseDTO> listBranches(String owner, String repo) {
        var branches = getBranches(owner, repo);
        return safeStream(Objects.isNull(branches)
                ? Collections.emptyList()
                : Arrays.asList(branches))
                .toList();
    }

    private BranchResponseDTO[] getBranches(String owner, String repo) {
        return restTemplate.getForObject(
                branchesUrl,
                BranchResponseDTO[].class,
                owner, repo
        );
    }

    private RepositoryResponseDTO[] getRepositories(String user) {
        return restTemplate.getForObject(
                repositoriesUrl,
                RepositoryResponseDTO[].class,
                user
        );
    }
}
