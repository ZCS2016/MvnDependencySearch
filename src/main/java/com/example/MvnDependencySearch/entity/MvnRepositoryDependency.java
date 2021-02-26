package com.example.MvnDependencySearch.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MvnRepositoryDependency {
    private Integer id;
    private String libraryName;
    private String groupId;
    private String artifactId;
    private String version;
    private String mvnRepositoryUrl;
    private String dependencyXml;
    private String centralUrl;
    private Date updateTime;
}
