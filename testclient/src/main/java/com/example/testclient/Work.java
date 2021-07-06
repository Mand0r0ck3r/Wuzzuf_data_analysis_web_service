package com.example.testclient;

import joinery.DataFrame;
import org.knowm.xchart.*;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class Work {

    public static void test(String path,int rows,String chart,String title,String xaxis){
        RestTemplate restTemplate=new RestTemplate();
        HashMap<String, List> m = restTemplate.getForObject(path+"?rows="+ rows ,HashMap.class);
        DataFrame df=Work.toDf(m);
        System.out.println(df);
        if (chart.equals("piechart")){ Work.pieCharting(df,title);}
        else if(chart.equals("barchart")){Work.barCharting(df,xaxis,title);}

    }




    public static DataFrame toDf(HashMap<String, List> m){
        DataFrame df = new DataFrame<>((List<String>) m.get("cols"));
        m.remove("cols");
        if(m.get("img")!=null){
            Work.toChart(m.get("img").get(0).toString());
            m.remove("img");}
        for (int i = 0; i < m.size() ; i++) {

            df.append(i, m.get(String.valueOf(i)));
        }
        return df;}
    public static void toChart(String img){
        JFrame frame =new JFrame();
        ImageIcon icon =new ImageIcon(img);
        JLabel label = new JLabel(icon);
        //frame.setSize(1,2);
        frame.add(label);
        //qframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    public static void barCharting(DataFrame df,String xaxis,String title){
        int rows= df.length();
        int width=1024;
        if (rows>6){ width=(int)((rows*1048)/6);}
        CategoryChart c =new CategoryChartBuilder().width(width).height(1024).build();
        c.setXAxisTitle(xaxis);
        c.setTitle(title);
        c.setYAxisTitle("Count");
        c.addSeries("Top Jobs",df.head(rows).col(0),df.head(rows).col(1));
        c.getStyler().setHasAnnotations(true);
        new SwingWrapper<>(c).displayChart();
    }
    public static void pieCharting(DataFrame df,String title) {

        int rows = df.length();

        if (rows == 0) {
            rows = df.length();
        }
        int height = 1048;
        if (rows > 35) {
            height = (int) ((rows * 1048) / 35);
        }
        PieChart pc = new PieChartBuilder().width(1200).height(height).build();
        pc.setTitle(title);
        pc.getStyler().setHasAnnotations(true);
        for (int i = 0; i < rows; i++) {
            pc.addSeries((String) df.get(i, 0), (Integer) df.get(i, 1));
        }
        new SwingWrapper<>(pc).displayChart();
    }

}
