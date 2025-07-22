package com.github.viewer.controller;

import com.github.viewer.model.Repository;
import com.github.viewer.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService service;

    @GetMapping("/users/{username}/repositories")
    public List<Repository> getRepositoriesByUsername(@PathVariable String username) {
        return service.getRepositories(username);
    }

}
