package com.example.MvnDependencySearch.mapper;

import com.example.MvnDependencySearch.entity.MvnDependency;
import com.example.MvnDependencySearch.entity.MvnRepository;
import com.example.MvnDependencySearch.entity.MvnRepositoryDependency;
import com.example.MvnDependencySearch.entity.ProjectDependency;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MvnDependencyMapper {

    @Insert("INSERT INTO project_dependency\n" +
            "(project,library_name,group_id,artifact_id,version,mvnrepository_url,dependency_xml,central_url)\n" +
            "VALUES\n" +
            "(#{project},#{libraryName},#{groupId},#{artifactId},#{version},#{mvnRepositoryUrl},#{dependencyXml},#{centralUrl})")
    void insertProjectDependency(ProjectDependency projectDependency);

    @Delete("DELETE FROM project_dependency WHERE project = #{project}")
    void deleteProjectDependencyByProject(@Param("project") String project);

    @Delete("DELETE FROM mvn_dependency WHERE project = #{project}")
    void deleteMvnDependencyByProject(@Param("project") String project);

    @Select("SELECT * FROM mvn_repository")
    @Results(id = "mvnRepositoryMap",value = {
            @Result(column = "id",property = "id",id = true),
            @Result(column = "repository",property = "repository"),
            @Result(column = "repository_url",property = "repositoryUrl"),
            @Result(column = "update_time",property = "updateTime")
    })
    List<MvnRepository> getMvnRepositoryList();

    @Select("SELECT * FROM project_dependency WHERE project = #{project}")
    @Results(id = "projectDependencyMap",value = {
            @Result(column = "id",property = "id",id = true),
            @Result(column = "project",property = "project"),
            @Result(column = "library_name",property = "libraryName"),
            @Result(column = "group_id",property = "groupId"),
            @Result(column = "artifact_id",property = "artifactId"),
            @Result(column = "version",property = "version"),
            @Result(column = "mvnrepository_url",property = "mvnRepositoryUrl"),
            @Result(column = "dependency_xml",property = "dependencyXml"),
            @Result(column = "central_url",property = "centralUrl"),
            @Result(column = "update_time",property = "updateTime")
    })
    List<ProjectDependency> getProjectDependencyList(@Param("project") String project);

    @Select("SELECT * FROM mvn_dependency WHERE project = #{project}")
    @Results(id = "mvnDependencyMap",value = {
            @Result(column = "id",property = "id",id = true),
            @Result(column = "project",property = "project"),
            @Result(column = "library_name",property = "libraryName"),
            @Result(column = "repository",property = "repository"),
            @Result(column = "library_url",property = "libraryUrl"),
            @Result(column = "check_result",property = "checkResult"),
            @Result(column = "update_time",property = "updateTime")
    })
    List<MvnDependency> getMvnDependencyListByProject(@Param("project") String project);

    @Select("SELECT * FROM mvnrepository_dependency")
    @Results(id = "mvnRepositoryDependencyMap",value = {
            @Result(column = "id",property = "id",id = true),
            @Result(column = "library_name",property = "libraryName"),
            @Result(column = "group_id",property = "groupId"),
            @Result(column = "artifact_id",property = "artifactId"),
            @Result(column = "version",property = "version"),
            @Result(column = "mvnrepository_url",property = "mvnRepositoryUrl"),
            @Result(column = "dependency_xml",property = "dependencyXml"),
            @Result(column = "central_url",property = "centralUrl"),
            @Result(column = "update_time",property = "updateTime")
    })
    List<MvnRepositoryDependency> getMvnRepositoryDependencyList();

    @Insert("INSERT INTO mvn_dependency\n" +
            "(project,library_name,repository,library_url,check_result)\n" +
            "VALUES\n" +
            "(#{project},#{libraryName},#{repository},#{libraryUrl},#{checkResult})")
    void insertMvnDependency(MvnDependency mvnDependency);

    @Insert("INSERT INTO mvnrepository_dependency\n" +
            "(library_name,group_id,artifact_id,version,mvnrepository_url,dependency_xml,central_url)\n" +
            "VALUES\n" +
            "(#{libraryName},#{groupId},#{artifactId},#{version},#{mvnRepositoryUrl},#{dependencyXml},#{centralUrl})")
    void insertMvnRepositoryDependency(MvnRepositoryDependency mvnRepositoryDependency);

    @Update("UPDATE project_dependency\n" +
            "SET mvnrepository_url = #{mvnRepositoryUrl}," +
            "    dependency_xml = #{dependencyXml}," +
            "    central_url = #{centralUrl}\n" +
            "WHERE project = #{project} AND library_name = #{libraryName}")
    void updateProjectDependency(ProjectDependency projectDependency);
}
