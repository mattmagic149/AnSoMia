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

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
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

import database.MarketValue;

/**
 * The Class MarketValueChart.
 */
public class MarketValueChart extends ApplicationFrame {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The title. */
    private String title;
    
    /** The values1. */
    private ArrayList<MarketValue> values1;
    
    /** The values2. */
    private ArrayList<MarketValue> values2;
    
    /** The values1_name. */
    private String values1_name;
    
    /** The values2_name. */
    private String values2_name;

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
     * @param values1 the values1
     * @param values2 the values2
     */
    public MarketValueChart(String title, ArrayList<MarketValue> values1, ArrayList<MarketValue> values2) {
        super(title);
        this.values1 = values1;
        this.values2 = values2;
        if(values1.size() > 0) {
        	this.values1_name = values1.get(0).getCompany().getName();
        } else {
        	this.values1_name = "Stock prices1";
        }
        
        if(values2.size() > 0) {
        	this.values2_name = values2.get(0).getCompany().getName();
        } else {
        	this.values1_name = "Stock prices2";
        }
        
        this.title = "Comparision: " + this.values1_name + " & " + this.values2_name;

        
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
            "Date",             // x-axis label
            "Price Per Unit",   // y-axis label
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

        return chart;

    }


    /**
     * Creates the dataset.
     *
     * @return the XY dataset
     */
    private XYDataset createDataset() {

        TimeSeries s1 = new TimeSeries(this.values1_name);
        
        MarketValue tmp;
        for(int i = 0; i < this.values1.size(); i++) {
        	tmp = this.values1.get(i);
        	s1.add(new Day(tmp.getDate()), tmp.getHigh());
        }

        TimeSeries s2 = new TimeSeries(this.values2_name);
        
        for(int i = 0; i < this.values2.size(); i++) {
        	tmp = this.values2.get(i);
        	s2.add(new Day(tmp.getDate()), tmp.getHigh());
        }
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

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
     * Execute.
     */
    public void execute() {
    	ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    	
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);

    }

}
