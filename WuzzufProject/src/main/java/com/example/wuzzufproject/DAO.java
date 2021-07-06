package com.example.wuzzufproject;


import joinery.DataFrame;
import org.knowm.xchart.*;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


class DAO {
    public DataFrame df;
    public Table table;
    public DAO() {
        this.df=null;
        this.table=null;
        try {
            this.df = DataFrame.readCsv("src/main/resources/static/Wuzzuf_Jobs.csv");
            this.table=Table.read().csv("src/main/resources/static/Wuzzuf_Jobs.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.cleanDF();
        this.factorizing();
        this.uselessFactorizing();
    }
    public DataFrame summary(){
        Table t=this.table.summary();
        List<String> ll=(List<String>) t.columns().get(1).asList();
        List<String> cols=t.columnNames();
        DataFrame su = new DataFrame();
        for (int i=0;i< cols.size();i++){
            su.add(cols.get(i), t.columns().get(i).asList());
        }
        return su;
    }
    public DataFrame structure(){
        Table t=this.table.structure();
        List<String> ll=(List<String>) t.columns().get(1).asList();
        List<String> cols=t.columnNames();
        DataFrame su = new DataFrame();
        for (int i=0;i< cols.size();i++){
            su.add(cols.get(i), t.columns().get(i).asList());
        }
        return su.drop("Index");
    }


    public void cleanDF(){
        this.df=this.df.dropna();
        this.df=this.df.unique(0,1,2,3,4,5,6,7);

    }
    public static String structure(DataFrame df){
        DataFrame ndf=new DataFrame<>(df.columns());
        ndf.append(df.types());
        String table=DAO.makeTable(ndf,"120");
        return DAO.toHTML("Data Frame Structure",table,1);
    }
    public static String summary(DataFrame df){
        String table=DAO.makeTable(df.describe(),"50");
        return DAO.toHTML("Data Frame Summary",table,1);
    }
    public static String makeTable(DataFrame df,String width) {
        List<String> cols = (List<String>) df.columns().stream().collect(Collectors.toList());
        List<Integer> inds = new ArrayList<>(df.index());
        String colshtml = "<th>  </th>";
        for (String th : cols) {
            colshtml += "<th>" + th + "</th>";
        }
        String rowshtml = "";
        for (int i = 0; i < df.length(); i++) {
            String row = "<td>"+inds.get(i)+"</td>";
            for (int j = 0; j < df.size(); j++) {
                row += "<td>" + df.get(i, j).toString() + "</td>";
            }
            rowshtml += "<tr>" + row + "</tr>";
        }
        return "<table class=\"table table-hover\" style=\"width:" + width + "%\"><tr>" + colshtml + "</tr>" + rowshtml + "</table>";
    }
    public static DataFrame countDF(DataFrame df,String col){
        DataFrame cdf=df.groupBy(col).count().sortBy(-1);
        List<String> co=new ArrayList<>();
        co.addAll(cdf.col(1));
        return cdf.add("Count",co).retain(col,"Count").resetIndex();
    }
    public static String toHTML(String s1,String table,String img,int rows){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>"+s1+"</h2>\n" +
                "\n" +
                "<p>Displaying "+rows+" rows.</p>"+table+
                "\n" +
                "<img src=\""+img+"\">\n" +
                "</body>\n" +
                "</html>";
    }
    public static String toHTML(String s1,String table,int rows){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>"+s1+"</h2>\n" +
                "\n" +
                "<p>Displaying "+rows+" rows.</p>"+table+
                "\n" +
                "</body>\n" +
                "</html>";
    }
    public static String pieCharting(DataFrame df,int rows,String name,String controller){
        if (rows==0){
            rows= df.length();
        }
        int height=1048;
        if (rows>35){ height=(int)((rows*1048)/35);}
        PieChart pc =new PieChartBuilder().width(1200).height(height).build();
        pc.getStyler().setHasAnnotations(true);
        pc.setTitle(name);
        for (int i=0;i<rows;i++){
            pc.addSeries((String)df.get(i,0),(Integer)df.get(i,1));
        }
        String goPath;
        String returnPath;
        if (controller.equals("HTML")){
            goPath="target/classes/static/"+name+".jpg";
            returnPath=name+".jpg";
        }
        else {
            goPath=System.getProperty("user.dir")+"./"+name+".jpg";
            returnPath=System.getProperty("user.dir")+"./"+name+".jpg";
        }

        try {
            BitmapEncoder.saveJPGWithQuality(pc,goPath , 1f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnPath;
    }
    public static String barCharting(DataFrame df,int rows,String name,String xaxis,String controller){
        if (rows==0){
            rows= df.length();
        }
        int width=1048;
        if (rows>6){ width=(int)((rows*1048)/6);}
        CategoryChart c =new CategoryChartBuilder().width(width).height(1024).build();
        c.setTitle(name);
        c.setXAxisTitle(xaxis);
        c.setYAxisTitle("Count");
        c.addSeries("Top Jobs",df.head(rows).col(0),df.head(rows).col(1));
        c.getStyler().setHasAnnotations(true);

        String goPath;
        String returnPath;
        if (controller.equals("HTML")){
            goPath="target/classes/static./"+name+".jpg";
            returnPath=name+".jpg";
        }
        else {
            goPath=System.getProperty("user.dir")+"./"+name+".jpg";
            returnPath=System.getProperty("user.dir")+"./"+name+".jpg";
        }

        try {
            BitmapEncoder.saveJPGWithQuality(c,goPath , 1f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnPath;}


    public static DataFrame skills(DataFrame df){
        List<String>skillss=((List<String> )df.col(7)).stream().flatMap(a-> Arrays.stream(a.split(","))).collect(Collectors.toList());
        Map<String,Integer> map1 =new HashMap<>();
        skillss.stream().forEach(a->map1.put(a,(map1.get(a) == null) ? 1 : map1.get(a) + 1));
        DataFrame ndf=new DataFrame();
        ndf.add("Skills",new ArrayList<>(map1.keySet()));
        ndf.add("Count",new ArrayList<>(map1.values()));
        return ndf.sortBy(-1).resetIndex();

    }
    public void factorizing() {
        Pattern pattern = Pattern.compile("(\\d+)\\W(\\d*)");
        List<Integer> miniExp = new ArrayList<>();
        List<Integer> maxiExp = new ArrayList<>();
        for (String s : (List<String>) this.df.col("YearsExp")) {
            Matcher matcher = pattern.matcher(s);
            Boolean match = matcher.find();
            miniExp.add((match ? Integer.parseInt(matcher.group(1)) : 0));
            maxiExp.add((match ? (matcher.group(2).equals("") ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(2))) : 0));
        }
        this.df.add("Minimum Experience", miniExp);
        this.df.add("Maximum Experience", maxiExp);
    }
    public void uselessFactorizing(){
        List<String> exp =(List<String>)this.df.retain(5).unique(0).col(0);
        HashMap<String,Integer> map =new HashMap<>();
        for(int i=0;i< exp.size();i++){
            map.put(exp.get(i),i );
        }
        List<Integer>newcol= ((List<String>)this.df.col(5)).stream().map(a->map.get(a)).collect(Collectors.toList());
        this.df.add("factorized Experience", newcol);

    }


}






