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
package test;

import java.io.IOException;
import java.util.logging.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DailyCrawlerLogger.
 */
public class DailyCrawlerLogger {
	 
 	/** The Constant logger. */
 	private static final Logger logger = Logger.getLogger(DailyCrawlerLogger.class.getName());
	 
	   /**
   	 * The main method.
   	 *
   	 * @param args the arguments
   	 * @throws IOException Signals that an I/O exception has occurred.
   	 */
   	public static void main(String[] args) throws IOException {
	      // Construct a default FileHandler.
	      // "%t" denotes the system temp directory, kept in environment variable "tmp"
	      Handler fh = new FileHandler("data/tmp/logger_msg/daily_crawler.log", true);  // append is true
	      //fh.setFormatter(new SimpleFormatter());  // Set the log format
	      // Add the FileHandler to the logger.
	      logger.addHandler(fh);
	      // Set the logger level to produce logs at this level and above.
	      logger.setLevel(Level.FINE);
	 
	      try {
	         // Simulating Exceptions
	         throw new Exception("Simulating an exception");
	      } catch (Exception ex){
	         logger.log(Level.SEVERE, ex.getMessage(), ex);
	      }
	      logger.info("This is a info-level message");
	      logger.config("This is a config-level message");
	      logger.fine("This is a fine-level message");
	      logger.finer("This is a finer-level message");
	      logger.finest("This is a finest-level message");  // below the logger's level
	 
	      fh.flush();
	      fh.close();
	   }
}
