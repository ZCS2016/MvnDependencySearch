package com.example.MvnDependencySearch.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MvnRepository {
    private Integer id;
    private String repository;
    private String repositoryUrl;
    private Date updateTime;
}
