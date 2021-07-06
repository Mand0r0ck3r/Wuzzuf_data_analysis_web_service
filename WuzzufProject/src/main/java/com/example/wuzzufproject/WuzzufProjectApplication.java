package com.example.wuzzufproject;

import joinery.DataFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class WuzzufProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(WuzzufProjectApplication.class, args);
    }

}

@RestController
class AppController {
    public DAO d = new DAO();

    @RequestMapping("/data")
    public String clean(@RequestParam(value="req",defaultValue = "full") String req, @RequestParam(value="rows",defaultValue = "10") int rows) {
        String controller="HTML";


        if(req.equals("summary")){
            String table = DAO.makeTable(d.summary(),"120");
            return DAO.toHTML("Full DataFrame",table,rows);
        }
        else if(req.equals("structure")){
            String table = DAO.makeTable(d.structure(),"50");
            return DAO.toHTML("Full DataFrame",table,rows);
        }
        else if (req.equals("full")){
            DataFrame df =d.df;
            if((rows<=0)||(rows>df.length())){rows=df.length();}
            String table = DAO.makeTable(d.df.head(rows),"200");
            return DAO.toHTML("Full DataFrame",table,rows);
        }
        if (req.equals("companies")){
            DataFrame cdf=DAO.countDF(d.df,"Company");
            if(rows==0||rows>cdf.length()){rows=cdf.length();}
            String taple=DAO.makeTable(cdf.head(rows),"50");
            String img=DAO.pieCharting(cdf,rows,"Popular Companies",controller);
            return DAO.toHTML("Popular Companies",taple,img,rows);
        }
        else if (req.equals("jobs")){
            DataFrame cdf = DAO.countDF(d.df, "Title");
            int chartrows=150;
            if(rows==0||rows>cdf.length()){rows=cdf.length();}
            if (rows<150){chartrows=rows;}
            String taple = DAO.makeTable(cdf.head(rows), "50");
            String img = DAO.barCharting(cdf, chartrows, "PopularJobs","Jobs",controller);
            return DAO.toHTML("Popular Jobs", taple, img, rows);
        }
        else if (req.equals("areas")){
            DataFrame cdf = DAO.countDF(d.df, "Location");
            int chartrows=100;
            if(rows==0||rows>cdf.length()){ rows=cdf.length();}
            if (rows<100){chartrows=rows;}
            String taple = DAO.makeTable(cdf.head(rows), "50");
            String img = DAO.barCharting(cdf, chartrows, "Popular Areas","Areas",controller);
            return DAO.toHTML("Popular Areas", taple, img, rows);
        }
        else if (req.equals("skills")){
            DataFrame cdf= DAO.skills(d.df);
            if((rows<=0)||(rows>cdf.length())){rows=cdf.length();}
            String table= DAO.makeTable(cdf.head(rows),"50");
            return DAO.toHTML("Top Skills",table,rows);
        }

        else {return "wrong path";}

    }


}
@RestController
@RequestMapping("/forclient")
class ForTesterController{
    public DAO d = new DAO();

    @RequestMapping("/full")
    public Map full(@RequestParam(value="rows",defaultValue = "10") int rows){
        Map m=d.df.head(rows).map();
        m.put("cols", d.df.columns().stream().collect(Collectors.toList()));
        return m;
    }
    @RequestMapping("/summary")
    public Map summary(){
        DataFrame df=d.summary();
        Map m=df.map();
        m.put("cols", df.columns().stream().collect(Collectors.toList()));
        return m;
    }
    @RequestMapping("/structure")
    public Map structure(){
        DataFrame df=d.structure();
        Map m=df.map();
        m.put("cols", df.columns().stream().collect(Collectors.toList()));
        return m;
    }
    @RequestMapping("/companies")
    public Map companies(@RequestParam(value="rows",defaultValue = "10") int rows){
        DataFrame df =DAO.countDF(d.df,"Company").head(rows);
        Map m=df.map();
        m.put("cols", df.columns().stream().collect(Collectors.toList()));
        return m;
    }
    @RequestMapping("/jobs")
    public Map jobs(@RequestParam(value="rows",defaultValue = "10") int rows){
        DataFrame df =DAO.countDF(d.df,"Title").head(rows);
        Map m=df.map();
        m.put("cols", df.columns().stream().collect(Collectors.toList()));
        return m;
    }
    @RequestMapping("/areas")
    public Map areas(@RequestParam(value="rows",defaultValue = "10") int rows,@RequestParam(value = "img",defaultValue = "false")boolean img){
        DataFrame df =DAO.countDF(d.df,"Location").head(rows);
        Map m=df.map();
        m.put("cols", df.columns().stream().collect(Collectors.toList()));
        if (img){
            m.put("img", Arrays.asList(DAO.barCharting(df,rows,"pop com","Areas","")));}
        return m;
    }
    @RequestMapping("/skills")
    public Map skills(@RequestParam(value="rows",defaultValue = "10") int rows){
        DataFrame df =DAO.skills(d.df).head(rows);
        Map m=df.map();
        m.put("cols", df.columns().stream().collect(Collectors.toList()));
        return m;
    }

}