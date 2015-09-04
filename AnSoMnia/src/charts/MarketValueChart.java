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
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

import demo.CircleDrawer;

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
    
    private String x_axis;
    
    private String y_axis;
    
    private List<Date> dates;

    private String values1_des;
    
    private String values2_des;
    
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
     * @param values the values1
     * @param values2 the values2
     */
    public MarketValueChart(String title, String x_axis, String y_axis) {
        super(title);

        this.title = title;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.values1 = null;
        this.values2 = null;
        
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
        
        
        final CircleDrawer cd = new CircleDrawer(Color.red, new BasicStroke(1.0f), null);
        XYAnnotation bestBid = null;
        XYPointerAnnotation pointer = null;
			
        bestBid = new XYDrawableAnnotation(dates.get(0).getTime(), this.values1[0], 11, 11, cd);
		plot.addAnnotation(bestBid);
        pointer = new XYPointerAnnotation("News Published", 
        									dates.get(0).getTime(), 
        									this.values1[0],
                                            1.0 * Math.PI / 4.0);
        

        Title textTitle = new TextTitle("HAAAAAAALLO");
		XYTitleAnnotation xyTitleAnnotation = new XYTitleAnnotation(dates.get(0).getTime(), this.values1[0], textTitle , RectangleAnchor.TOP_LEFT);
        
        plot.addAnnotation(xyTitleAnnotation);

        
		pointer.setBaseRadius(35.0);
        pointer.setTipRadius(10.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
        pointer.setPaint(Color.black);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        plot.addAnnotation(pointer);

        return chart;

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
    
    private void init(double[] values1, double[] values2, String values1_des, String values2_des, List<Date> dates, boolean save, boolean show) {
    	this.values2 = values2;
    	this.values2_des = values2_des;
    	init(values1, values1_des, dates, save, show);
    }
    
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
     */
    public void execute(double[] values1, double[] values2, String values1_des, String values2_des, List<Date> dates) {
    	init(values1, values2, values1_des, values2_des, dates, false, true);
    }
    
    
    
    public void execute(double[] values, String values_des, List<Date> dates) {
    	init(values, values_des, dates, false, true);
    }

}
