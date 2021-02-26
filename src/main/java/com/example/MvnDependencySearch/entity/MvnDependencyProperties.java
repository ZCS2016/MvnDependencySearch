package com.example.MvnDependencySearch.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mvndependency")
@Data
public class MvnDependencyProperties {
    List<WhiteList> WhiteLists;
}
