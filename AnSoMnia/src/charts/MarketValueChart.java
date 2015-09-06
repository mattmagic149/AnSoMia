/*
 * @Author: Matthias Ivantsits
 * Supported by TU-Graz (KTI)
 * 
 * Tool, to gather market information, in quantitative and qualitative manner.
 * Copyright (C) 2015  Matthias Ivantsits
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

import analysers.MarketValueAnalyser;
import demo.CircleDrawer;

// TODO: Auto-generated Javadoc
/**
 * The Class MarketValueChart.
 */
public class MarketValueChart extends ApplicationFrame {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The title. */
    private String title;
    
    /** The values1. */
    private double[] values1;
    
    /** The values2. */
    private double[] values2;
    
    /** The x_axis. */
    private String x_axis;
    
    /** The y_axis. */
    private String y_axis;
    
    /** The dates. */
    private List<Date> dates;

    /** The values1_des. */
    private String values1_des;
    
    /** The values2_des. */
    private String values2_des;
    
    private MarketValueAnalyser.Analyse period;
    
    static {
        // set a theme using the new shadow generator feature available in
        // 1.0.14 - for backwards compatibility it is not enabled by default
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
                true));
    }


    /**
     * Instantiates a new market value chart.
     *
     * @param title the title
     * @param x_axis the x_axis
     * @param y_axis the y_axis
     */
    public MarketValueChart(String title, String x_axis, String y_axis) {
        super(title);

        this.title = title;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.values1 = null;
        this.values2 = null;
        
    }
    
    public MarketValueChart(String title, String x_axis, String y_axis, 
    						MarketValueAnalyser.Analyse period) {
        super(title);
     
        this.title = title;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.values1 = null;
        this.values2 = null;
        this.period = period;
        
    }

    /**
     * Creates the chart.
     *
     * @param dataset the dataset
     * @return the j free chart
     */
    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,  // title
            x_axis,             // x-axis label
            y_axis,   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM"));
        
        if(this.values2 == null) {
        	this.drawDescriptionToDataPoint(plot);
        }

        return chart;

    }
    
    private void drawDescriptionToDataPoint(XYPlot plot) {
    	 final CircleDrawer cd = new CircleDrawer(Color.red, new BasicStroke(1.0f), null);
         XYAnnotation bestBid = null;
         XYPointerAnnotation pointer = null;
         long x = 0;
         double y = 0;
         double mult = 0;
         
         if(this.period == MarketValueAnalyser.Analyse.AFTER) {
        	 x = dates.get(0).getTime();
        	 y = this.values1[0];
        	 mult = 1.0;
         } else if(this.period == MarketValueAnalyser.Analyse.BEFORE) {
        	 x = dates.get(dates.size() - 2).getTime();
        	 y = this.values1[this.values1.length - 1];
        	 mult = 3.0;
         } else if(this.period == MarketValueAnalyser.Analyse.BEFORE_AFTER) {
        	 int tmp = dates.size() - 1;
        	 int to_add = (int) Math.floor(tmp/2.0);
        	 x = dates.get(to_add).getTime();
        	 y = this.values1[to_add];
         }
 			
         bestBid = new XYDrawableAnnotation(x, y, 11, 11, cd);
 		 plot.addAnnotation(bestBid);
         pointer = new XYPointerAnnotation("News Published", x, y, mult * Math.PI / 4.0);

         
 		 pointer.setBaseRadius(35.0);
         pointer.setTipRadius(10.0);
         pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
         pointer.setPaint(Color.black);
         pointer.setTextAnchor(TextAnchor.TOP_CENTER);
         plot.addAnnotation(pointer);
    }


    /**
     * Creates the dataset.
     *
     * @return the XY dataset
     */
    private XYDataset createDataset() {

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries s1 = new TimeSeries(this.values1_des);
        
        for(int i = 0; i < this.values1.length; i++) {
        	s1.add(new Day(dates.get(i)), this.values1[i]);
        }
        dataset.addSeries(s1);
        
        if(this.values2 != null) {
	        TimeSeries s2 = new TimeSeries(this.values2_des);
	        
	        for(int i = 0; i < this.values2.length; i++) {
	        	s2.add(new Day(dates.get(i)), this.values2[i]);
	        }
	        
	        dataset.addSeries(s2);
        }

        return dataset;

    }

    /**
     * Creates the demo panel.
     *
     * @return the j panel
     */
    private JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }
    
    /**
     * Inits the.
     *
     * @param values1 the values1
     * @param values2 the values2
     * @param values1_des the values1_des
     * @param values2_des the values2_des
     * @param dates the dates
     * @param save the save
     * @param show the show
     */
    private void init(double[] values1, double[] values2, String values1_des, String values2_des, List<Date> dates, boolean save, boolean show) {
    	this.values2 = values2;
    	this.values2_des = values2_des;
    	init(values1, values1_des, dates, save, show);
    }
    
    /**
     * Inits the.
     *
     * @param values the values
     * @param values_des the values_des
     * @param dates the dates
     * @param save the save
     * @param show the show
     */
    private void init(double[] values, String values_des, List<Date> dates, boolean save, boolean show) {
    	this.values1 = values;
    	this.values1_des = values_des;
    	this.dates = dates;
    	
    	ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    	
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        
        if(show) {
        	this.setVisible(true);
        }
    }

    /**
     * Execute.
     *
     * @param values1 the values1
     * @param values2 the values2
     * @param values1_des the values1_des
     * @param values2_des the values2_des
     * @param dates the dates
     */
    public void execute(double[] values1, double[] values2, String values1_des, String values2_des, List<Date> dates) {
    	init(values1, values2, values1_des, values2_des, dates, false, true);
    }
    
    
    
    /**
     * Execute.
     *
     * @param values the values
     * @param values_des the values_des
     * @param dates the dates
     */
    public void execute(double[] values, String values_des, List<Date> dates) {
    	init(values, values_des, dates, false, true);
    }

}
