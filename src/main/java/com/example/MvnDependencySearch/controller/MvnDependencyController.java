package com.example.MvnDependencySearch.controller;

import com.example.MvnDependencySearch.entity.*;
import com.example.MvnDependencySearch.mapper.MvnDependencyMapper;
import com.example.MvnDependencySearch.util.MvnDependencyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/MvnDependency")
public class MvnDependencyController {
    @Autowired
    MvnDependencyMapper mvnDependencyMapper;

    @Autowired
    MvnDependencyUtil mvnDependencyUtil;

    @PostMapping("/fileUpload")
    public R handleFormUpload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String project = fileName.replaceAll(".iml","");
        System.out.println("project:" + project);

        if (file.isEmpty()) {
            return R.error("File is empty!");
        }

        byte[] bytes = file.getBytes();
        String imlStr = new String(bytes,"UTF-8");

        Document doc = Jsoup.parse(imlStr);
        Elements orderEntryList = doc.getElementsByTag("orderEntry");
        if(!orderEntryList.isEmpty()){
            mvnDependencyMapper.deleteProjectDependencyByProject(project);
            mvnDependencyMapper.deleteMvnDependencyByProject(project);
        }
        ArrayList<String> libNameList = new ArrayList<>();
        for(Element orderEntry : orderEntryList){
            if(orderEntry.hasAttr("type") && orderEntry.attr("type").equals("library")){
                String libName = orderEntry.attr("name");
                libName = libName.replaceAll("Maven:","").trim();
                System.out.println(libName);
                libNameList.add(libName);
            }
        }

        Collections.sort(libNameList);
        for(String libName : libNameList){
            ProjectDependency projectDependency = new ProjectDependency();
            projectDependency.setProject(project);
            projectDependency.setLibraryName(libName);

            String[] dependency = libName.split(":");
            if(dependency != null && dependency.length == 3) {
                projectDependency.setGroupId(dependency[0]);
                projectDependency.setArtifactId(dependency[1]);
                projectDependency.setVersion(dependency[2]);
            }else{
                throw new Exception("Dependency parse error!");
            }

            mvnDependencyMapper.insertProjectDependency(projectDependency);
        }

        return R.ok();
    }

    @RequestMapping("/getMvnDependencyList")
    public R getMvnDependencyList(@RequestBody Map<String,String> params){
        List<MvnDependency> mvnDependencyResultList = new ArrayList<>();

        String project = params.get("project");
        String checkResult = params.get("checkResult");

        List<ProjectDependency> projectDependencyList = mvnDependencyMapper.getProjectDependencyList(project);
        List<MvnDependency> mvnDependencyList = mvnDependencyMapper.getMvnDependencyListByProject(project);

        for(ProjectDependency projectDependency : projectDependencyList){
            MvnDependency mvnDependency = new MvnDependency();
            mvnDependency.setProject(projectDependency.getProject());
            mvnDependency.setLibraryName(projectDependency.getLibraryName());
            mvnDependency.setMvnRepositoryUrl(projectDependency.getMvnRepositoryUrl());
            mvnDependency.setDependencyXml(projectDependency.getDependencyXml());
            mvnDependency.setCentralUrl(projectDependency.getCentralUrl());
            boolean finalCheckResult = false;

            List<RepositoryCheckResult> repositoryCheckResultList = new ArrayList<>();
            for(MvnDependency mvnDependencyItem : mvnDependencyList){
                if(mvnDependencyItem.getProject().equals(projectDependency.getProject())
                        && mvnDependencyItem.getLibraryName().equals(projectDependency.getLibraryName())){
                    RepositoryCheckResult repositoryCheckResult = new RepositoryCheckResult();
                    repositoryCheckResult.setRepository(mvnDependencyItem.getRepository());
                    repositoryCheckResult.setLibraryUrl(mvnDependencyItem.getLibraryUrl());
                    repositoryCheckResult.setCheckResult(mvnDependencyItem.isCheckResult());
                    if(mvnDependencyItem.isCheckResult()){
                        finalCheckResult = true;
                    }
                    repositoryCheckResultList.add(repositoryCheckResult);
                    mvnDependency.setUpdateTime(mvnDependencyItem.getUpdateTime());
                }
            }

            mvnDependency.setRepositoryCheckResultList(repositoryCheckResultList);

            if(StringUtils.isEmpty(checkResult)){
                mvnDependencyResultList.add(mvnDependency);
            }else{
                if(checkResult.equalsIgnoreCase("true") && finalCheckResult){
                    mvnDependencyResultList.add(mvnDependency);
                }else if(checkResult.equalsIgnoreCase("false") && !finalCheckResult){
                    mvnDependencyResultList.add(mvnDependency);
                }
            }
        }

        return R.ok().put("mvnDependencyResultList",mvnDependencyResultList);
    }

    @RequestMapping("/downloadMvnDependencyExcel")
    public ResponseEntity<byte[]> downloadMvnDependencyExcel(@RequestBody Map<String,String> params) {
        R r = getMvnDependencyList(params);
        List<MvnDependency> mvnDependencyResultList = (List<MvnDependency>)r.get("mvnDependencyResultList");
        try {
            final String filePath = "MvnDependency.xls";
            //生成Excel
            //指定数据存放的位置
            OutputStream outputStream = new FileOutputStream(filePath);
            //1.创建一个工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            //2.创建一个工作表sheet
            HSSFSheet sheet = workbook.createSheet("MvnDependency");

            HSSFRow row1 = sheet.createRow(0);
            //设置值，这里合并单元格后相当于标题
            row1.createCell(0).setCellValue("序号");
            row1.createCell(1).setCellValue("依赖");
            row1.createCell(2).setCellValue("组件在中央库的地址");
            row1.createCell(3).setCellValue("组件标准坐标");

            for(int i = 0;i<mvnDependencyResultList.size();i++){
                MvnDependency mvnDependency = mvnDependencyResultList.get(i);
                //行
                HSSFRow row = sheet.createRow(i+1);
                //对列赋值
                row.createCell(0).setCellValue(i+1);
                row.createCell(1).setCellValue(mvnDependency.getLibraryName());
                row.createCell(2).setCellValue(mvnDependency.getCentralUrl());
                row.createCell(3).setCellValue(mvnDependency.getDependencyXml());
            }
            workbook.write(outputStream);
            outputStream.close();

            //获取文件对象
            byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
            HttpHeaders headers=new HttpHeaders();
            headers.set("Content-Disposition","attachment;filename=" + filePath);
            ResponseEntity<byte[]> entity=new ResponseEntity<>(bytes,headers, HttpStatus.OK);
            return entity;
        } catch (IOException e) {
            return null;
        }
    }

    @RequestMapping("/handleSearch")
    public R handleSearch(@RequestBody Map<String,String> params){
        String project = params.get("project");

        List<MvnRepository> mvnRepositoryList = mvnDependencyMapper.getMvnRepositoryList();
        List<ProjectDependency> projectDependencyList = mvnDependencyMapper.getProjectDependencyList(project);

        List<MvnRepositoryDependency> mvnRepositoryDependencyList = mvnDependencyMapper.getMvnRepositoryDependencyList();
        for(ProjectDependency projectDependency : projectDependencyList) {
            if(StringUtils.isEmpty(projectDependency.getMvnRepositoryUrl()) || StringUtils.isEmpty(projectDependency.getDependencyXml()) || StringUtils.isEmpty(projectDependency.getCentralUrl())) {
                String mvnRepositoryUrl = "";
                String dependencyXml = "";
                String centralUrl = "";

                boolean exist = false;
                for(MvnRepositoryDependency mvnRepositoryDependency : mvnRepositoryDependencyList){
                    if(mvnRepositoryDependency.getLibraryName().equals(projectDependency.getLibraryName())){
                        exist = true;
                        mvnRepositoryUrl = mvnRepositoryDependency.getMvnRepositoryUrl();
                        dependencyXml = mvnRepositoryDependency.getDependencyXml();
                        centralUrl = mvnRepositoryDependency.getCentralUrl();
                    }
                }

                if(!exist) {
                    mvnRepositoryUrl = mvnDependencyUtil.getMvnRepositoryUrl(projectDependency.getGroupId(), projectDependency.getArtifactId(), projectDependency.getVersion());
                    dependencyXml = "<dependency>" + "\n"
                            + "    <groupId>" + projectDependency.getGroupId() + "</groupId>" + "\n"
                            + "    <artifactId>" + projectDependency.getArtifactId() + "</artifactId>" + "\n"
                            + "    <version>" + projectDependency.getVersion() + "</version>" + "\n"
                            + "</dependency>";
                    centralUrl = mvnDependencyUtil.getCentralUrl(mvnRepositoryUrl);

                    MvnRepositoryDependency mvnRepositoryDependency = new MvnRepositoryDependency();
                    mvnRepositoryDependency.setLibraryName(projectDependency.getLibraryName());
                    mvnRepositoryDependency.setGroupId(projectDependency.getGroupId());
                    mvnRepositoryDependency.setArtifactId(projectDependency.getArtifactId());
                    mvnRepositoryDependency.setVersion(projectDependency.getVersion());
                    mvnRepositoryDependency.setMvnRepositoryUrl(mvnRepositoryUrl);
                    mvnRepositoryDependency.setDependencyXml(dependencyXml);
                    mvnRepositoryDependency.setCentralUrl(centralUrl);
                    mvnDependencyMapper.insertMvnRepositoryDependency(mvnRepositoryDependency);
                }

                projectDependency.setMvnRepositoryUrl(mvnRepositoryUrl);
                projectDependency.setDependencyXml(dependencyXml);
                projectDependency.setCentralUrl(centralUrl);
                mvnDependencyMapper.updateProjectDependency(projectDependency);
            }
        }

        mvnDependencyMapper.deleteMvnDependencyByProject(project);

        for(MvnRepository mvnRepository : mvnRepositoryList){
            for(ProjectDependency projectDependency : projectDependencyList){
                MvnDependency mvnDependency = new MvnDependency();
                mvnDependency.setProject(project);
                mvnDependency.setLibraryName(projectDependency.getLibraryName());
                mvnDependency.setRepository(mvnRepository.getRepository());
                String libraryUrl = mvnDependencyUtil.getLibraryUrl(mvnRepository.getRepositoryUrl(),
                        projectDependency.getLibraryName(),
                        projectDependency.getGroupId(),
                        projectDependency.getArtifactId(),
                        projectDependency.getVersion());
                mvnDependency.setLibraryUrl(libraryUrl);
                boolean checkResult = MvnDependencyUtil.checkExist(libraryUrl);
                mvnDependency.setCheckResult(checkResult);

                mvnDependencyMapper.insertMvnDependency(mvnDependency);
            }
        }

        return R.ok();
    }

}
