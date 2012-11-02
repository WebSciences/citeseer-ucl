/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.misc.charts;

import java.awt.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.data.time.*;
import org.jfree.ui.*;

import java.sql.SQLException;
import javax.sql.DataSource;
import java.io.*;
import java.util.*;

import edu.psu.citeseerx.dao.*;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.dbcp.*;

/**
 * ChartBuilder implementation using JFreeCharts.  I could never get charts
 * that weren't ugly, so this implementation is deprecated.
 *
 * @author Isaac Councill
 * @version $Rev: 665 $ $Date: 2008-07-27 13:10:35 -0400 (Sun, 27 Jul 2008) $
 * @deprecated
 */
public class CiteChartBuilderJFree {

    private static final int MAX_CITING = 5000;
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;        
    }
    
    
    protected JFreeChart buildChart(Document doc) throws SQLException {
        
        Long clusterid = doc.getClusterID();
        if (clusterid == null) {
            return null;
        }
        
        java.util.List<ThinDoc> citingDocs =
            citedao.getCitingDocuments(clusterid, 0, MAX_CITING);
        XYDataset dataset = collectData(citingDocs);
        if (dataset.getItemCount(0) <= 1) {
            return null;
        }
        
        XYBarDataset ivl_dataset = new XYBarDataset(dataset, 15.0);
        JFreeChart chart = ChartFactory.createXYBarChart(null, "Year", true,
                "Citation Count", ivl_dataset, PlotOrientation.VERTICAL,
                false, false, false);
        chart.setBackgroundPaint(Color.WHITE);
        
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        
        NumberAxis axis = (NumberAxis)plot.getDomainAxis();
        axis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        //axis.setTickUnit(new DateTickUnit(DateTickUnit.YEAR, -1));
        //axis.setDateFormatOverride(new SimpleDateFormat("yyyy"));
        return chart;
        
    }  //- buildChart
    
    
    private XYDataset collectData(java.util.List<ThinDoc> docs) {
        
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        
        HashMap<Integer,DataPoint> data = new HashMap<Integer,DataPoint>();
        for (ThinDoc doc : docs) {
            try {
                Integer year = new Integer(doc.getYear());
                if (year.intValue() < 1930 ||
                        year.intValue() > currentYear + 2) {
                    continue;
                }
                DataPoint point;
                if (data.containsKey(year)) {
                    point = data.get(year);
                } else {
                    point = new DataPoint(year.intValue());
                    data.put(year, point);
                }
                point.ncites++;
            } catch (Exception e) { }
        }
        XYSeries series = new XYSeries("Years");
        for (DataPoint point : data.values()) {
            System.out.println(point.year);
            System.out.println(point.ncites);
            series.add(point.year, point.ncites);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
        
    } //- collectData
    
    
    public static void main(String[] args) throws Exception {
        
        DataSource csxDataSource = DBCPFactory.createDataSource("citeseerx");
        DataSource cgDataSource = DBCPFactory.createDataSource("citegraph");
        
        CSXDAO csxdao = new CSXDAO();
        csxdao.setDataSource(csxDataSource);
        
        CiteClusterDAO citedao = new CiteClusterDAOImpl();
        citedao.setDataSource(cgDataSource);
        
        CiteChartBuilderJFree builder = new CiteChartBuilderJFree();
        builder.setCiteClusterDAO(citedao);
        
        Document doc = csxdao.getDocumentFromDB("10.1.1.1.3288", false, false);
        JFreeChart chart = builder.buildChart(doc);
        
        ApplicationFrame frame = new ApplicationFrame("Chart Test");
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500,500));
        frame.setContentPane(chartPanel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
        
    }
    
}  //- class CiteChartBuilderJFree


class DataPoint {
    public int year;
    public int ncites;
    public DataPoint(int year) {
        this.year = year;
        ncites = 0;
    }
}

class DataPointComparator implements Comparator {
    
    public int compare(Object o1, Object o2) {
        DataPoint p1 = (DataPoint)o1;
        DataPoint p2 = (DataPoint)o2;
        if (p1.year > p2.year) {
            return 1;
        }
        if (p2.year > p1.year) {
            return -1;
        }
        return 0;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof DataPointComparator) {
            return true;
        } else {
            return false;
        }
    }
}
