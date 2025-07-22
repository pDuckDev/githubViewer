package com.github.viewer.service;

import com.github.viewer.client.GitHubClient;
import com.github.viewer.model.Branch;
import com.github.viewer.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.viewer.util.CollectionUtil.safeStream;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private final GitHubClient client;

    public List<Repository> getRepositories(String username) {
        return safeStream(client.listNonForkRepos(username))
                .map(repo -> {
                    var branches = safeStream(client.listBranches(repo.owner().login(), repo.name()))
                            .map(b -> new Branch(b.name(), b.commit().sha()))
                            .toList();
                    return new Repository(repo.name(), repo.owner().login(), branches);
                })
                .toList();
    }

}
