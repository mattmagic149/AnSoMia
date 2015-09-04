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


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.Rectangle;
import java.io.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

// TODO: Auto-generated Javadoc
/**
 * The Class NewsHistogram.
 */
public class NewsHistogram extends ApplicationFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The title. */
	private String title;

	/** The values. */
	private double[] values;
	
	/** The description. */
	private String description;
	
	/** The x_axis. */
	private String x_axis;
	
	/** The y_axis. */
	private String y_axis;
	
	/** The number_of_bins. */
	private int number_of_bins;
	
	/**
	 * Instantiates a new news histogram.
	 *
	 * @param title the title
	 * @param values the values
	 * @param description the description
	 * @param x_axis the x_axis
	 * @param y_axis the y_axis
	 * @param number_of_bins the number_of_bins
	 * @param start the start
	 * @param end the end
	 */
	public NewsHistogram(String title, double[] values, String description,
							String x_axis, String y_axis,
							int number_of_bins) {
        super(title);
        this.title = title;
        this.values = values;
        this.description = description;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.number_of_bins = number_of_bins;
        
    }
    
    /**
     * Creates the dataset.
     *
     * @return the interval xy dataset
     */
    private IntervalXYDataset createDataset() {
    	HistogramDataset dataset = new HistogramDataset();
    	
    	//dataset.setType(HistogramType.RELATIVE_FREQUENCY);
    	//dataset.addSeries(this.description, target, this.number_of_bins);
    	
    	dataset.addSeries(this.description, 
    					  this.values, 
    					  this.number_of_bins);/*, 
    					  this.start,
    					  this.end);*/
        return dataset;
    }

    /**
     * Creates the chart.
     *
     * @param dataset the dataset
     * @return the j free chart
     */
    private JFreeChart createChart(IntervalXYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYBarChart(
            this.title,
            this.x_axis, 
            false,
            this.y_axis, 
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return chart;    
    }
    
    private void exportFigure(JFreeChart jfreechart) {
    	// TODO: use a dialog to ask for the filename
    	File svgFile = new File("data/plots/" + this.title.replace("/", "") + ".svg");
    	
    	// write it to file
    	try {
			exportChartAsSVG(jfreechart, 
				getContentPane().getBounds(), svgFile);

	    	// TODO: notify the user the file has been saved (e.g. status bar)
	    	System.out.println("Figured saved as " + svgFile.getAbsolutePath());
    	
    	} catch (IOException e) {
			System.err.println("Error saving file:\n" + e.getMessage());
		}
    }
    
    private void exportChartAsSVG(JFreeChart chart, Rectangle bounds, File svgFile) throws IOException {
        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // draw the chart in the SVG generator
        chart.draw(svgGenerator, bounds);

        // Write svg file
        OutputStream outputStream = new FileOutputStream(svgFile);
        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
        svgGenerator.stream(out, true /* use css */);						
        outputStream.flush();
        outputStream.close();
	}  


    /**
     * Execute.
     */
    public void execute() {
    	
    	IntervalXYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        exportFigure(chart);
        //this.setVisible(true);
        

    }

}
