package com.example.MvnDependencySearch.util;

import com.example.MvnDependencySearch.entity.MvnDependencyProperties;
import com.example.MvnDependencySearch.entity.ProjectDependency;
import com.example.MvnDependencySearch.entity.WhiteList;
import com.example.MvnDependencySearch.mapper.MvnDependencyMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MvnDependencyUtil {
    private static RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MvnDependencyProperties mvnDependencyProperties;


    public String getMvnRepositoryUrl(String groupId, String artifactId, String version){
        String mvnRepositoryUrl = "https://mvnrepository.com/artifact/" + groupId + "/" + artifactId + "/" + version;
        return mvnRepositoryUrl;
    }

    public String getCentralUrl(String mvnRepositoryUrl){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            String responseHtml = restTemplate.exchange(mvnRepositoryUrl, HttpMethod.GET,entity,String.class).toString();

            Document doc = Jsoup.parse(responseHtml);
            Elements vbtnList = doc.getElementsByClass("vbtn");
            for(Element element: vbtnList){
                if(element.tagName().equals("a") && element.attributes().hasKey("href") && element.html().contains("jar")){
                    String href = element.attributes().get("href");
                    System.out.println(href);
                    return href;
                }
            }
            for(Element element: vbtnList){
                if(element.tagName().equals("a") && element.attributes().hasKey("href") && element.html().contains("bundle")){
                    String href = element.attributes().get("href");
                    System.out.println(href);
                    return href;
                }
            }
        } catch (RestClientException e){
            if(e.getMessage().contains("404")){
                System.out.println(mvnRepositoryUrl + " " + 404);
            }
        }

        return "404";
    }

    public String getLibraryUrl(String repositoryUrl, String libName, String groupId, String artifactId, String version){
        String libraryUrl = "";

        List<WhiteList> whiteLists = mvnDependencyProperties.getWhiteLists();
        for(WhiteList whiteList : whiteLists){
            if(whiteList.getLibName().equals(libName)){
                libraryUrl = repositoryUrl + whiteList.getLibraryUrl();
                return libraryUrl;
            }
        }

        libraryUrl = repositoryUrl + groupId.replaceAll("\\.","/") + "/" + artifactId.replaceAll("\\.","/") + "/" + version;
        return libraryUrl;
    }

    public static boolean checkExist(String libraryUrl){
        boolean checkResult = false;

        try {
            MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
            String libraryGetHtml = restTemplate.getForObject(libraryUrl, String.class, param);
            checkResult = true;
        }catch (RestClientException e){
            if(e.getMessage().contains("404")){
                checkResult = false;
                System.out.println(libraryUrl + " " + checkResult);
            }
        }

        return checkResult;
    }

}
