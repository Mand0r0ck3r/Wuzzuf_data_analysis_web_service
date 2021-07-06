package com.example.testclient;

import joinery.DataFrame;
import org.knowm.xchart.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

@Component
public class CallRest implements CommandLineRunner {

    public static void callRest(){
        System.setProperty("java.awt.headless","false");


        String path1="http://localhost:8080/forclient/full";
        String path2="http://localhost:8080/forclient/summary";
        String path3="http://localhost:8080/forclient/structure";
        String path4="http://localhost:8080/forclient/companies";
        String path5="http://localhost:8080/forclient/jobs";
        String path6="http://localhost:8080/forclient/areas";
        String path7="http://localhost:8080/forclient/skills";

        Work.test(path1,5,"none","none","none");
        Work.test(path2,5,"none","none","none");
        Work.test(path3,5,"none","none","none");
        Work.test(path4,5,"piechart","Top Companies","none");
        Work.test(path5,5,"barchart","Top Jobs","Jobs");
        Work.test(path6,5,"barchart","Top Areas","Areas");
        Work.test(path7,5,"none","none","none");




    }

    @Override
    public void run(String... args) throws Exception {
        callRest();

    }
}

