package com.github.viewer.model;

import java.util.List;

public record Repository(String repositoryName,
                         String ownerLogin,
                         List<Branch> branches) {}
