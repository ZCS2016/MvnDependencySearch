package com.example.MvnDependencySearch.entity;

import lombok.Data;

@Data
public class RepositoryCheckResult {
    private String repository;
    private String libraryUrl;
    private boolean checkResult;
}
