package com.example.MvnDependencySearch.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MvnDependency {
    private Integer id;
    private String project;
    private String libraryName;
    private String repository;
    private String libraryUrl;
    private boolean checkResult;
    private Date updateTime;

    private String mvnRepositoryUrl;
    private String dependencyXml;
    private String centralUrl;
    private List<RepositoryCheckResult> repositoryCheckResultList;
}
