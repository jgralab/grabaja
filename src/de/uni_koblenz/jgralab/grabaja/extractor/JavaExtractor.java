/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.grabaja.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Main class of javaextractor project.
 * 
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class JavaExtractor {

	/**
	 * Name of parsed program, can be set by command line argument.
	 */
	private static String programName = "";

	/**
	 * Path to file storing extracted graph, can be set by command line
	 * argument.
	 */
	private static String targetFilePath = "";

	/**
	 * Path to log file, can be set by command line argument.
	 */
	private static String logFilePath = "";

	/**
	 * Extraction mode, can be set by command line argument.
	 */
	private static ExtractionMode mode = ExtractionMode.LAZY;

	/**
	 * List of files to parse.
	 */
	public static Vector<String> fileList = new Vector<String>();

	/**
	 * Logger to log events to disk.
	 */
	private static Logger logger;

	/**
	 * Main method.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {

		System.out.println("\nJavaExtractor v1.0 University of Koblenz\n");
		Date startDate = new Date(); // Getting start time so we can calculate
										// duration of extraction process.

		try {
			parseCommandLineArguments(args); // Checks arguments and collects
												// files to extract graph from.
			initializeLogging();
			if (!fileList.isEmpty()) {
				GraphBuilder graphBuilder = new GraphBuilder(programName,
						logger);
				graphBuilder.parseFiles(fileList);
				graphBuilder.finalizeGraph(mode);
				graphBuilder.saveGraphToDisk(targetFilePath);
			}
		} catch (Exception exception) {
			System.out
					.println(" An unexpected exception occurred in main-method!");
			exception.printStackTrace(System.err);
			logger.warning(Utilities.stackTraceToString(exception));
		} finally {
			finalizeLogging(startDate);
		}
	}

	/**
	 * Initializes the logger.
	 */
	private static void initializeLogging() {
		SimpleFormatter simpleFormater = new SimpleFormatter();
		try {
			FileHandler fileHandler = new FileHandler(logFilePath + ".log");
			fileHandler.setFormatter(simpleFormater);
			logger = Logger.getLogger("javaextractor.logger");
			logger.addHandler(fileHandler);
		} catch (IOException exception) {
			System.out
					.println(" Log file could not be created at specified location.\n");
			System.exit(0);
		}
	}

	private static void printHelpMessageAndExit() {
		System.out
				.println("Usage:\n"
						+ "java javaextractor.JavaExtractor [-log <file name>] [-out <file name>] [-name <name>] <directory or file name> {directory or file name}");
		System.exit(0);
	}

	/**
	 * Parses the commandline arguments passed over to the program and sets the
	 * according settings.
	 * 
	 * @param args
	 *            A string array of command line arguments.
	 */
	private static void parseCommandLineArguments(String[] args)
			throws Exception {
		if (args.length <= 0) {
			// argument
			printHelpMessageAndExit();
		}
		boolean nextArgIsLog = false;
		boolean nextArgIsOut = false;
		boolean nextArgIsName = false;
		for (String arg : args) {
			if (nextArgIsLog || nextArgIsOut || nextArgIsName) {
				if ((arg.toLowerCase().equals("-log"))
						|| (arg.toLowerCase().equals("-l"))) {
					nextArgIsLog = true;
					nextArgIsOut = false;
					nextArgIsName = false;
					// logger.info(
					// "Detected invalid usage of command parameters, skipping parameter."
					// );
				} else if ((arg.toLowerCase().equals("-out"))
						|| (arg.toLowerCase().equals("-o"))) {
					nextArgIsOut = true;
					nextArgIsLog = false;
					nextArgIsName = false;
					// logger.info(
					// "Detected invalid usage of command parameters, skipping parameter."
					// );
				} else if ((arg.toLowerCase().equals("-name"))
						|| (arg.toLowerCase().equals("-n"))) {
					nextArgIsName = true;
					nextArgIsLog = false;
					nextArgIsOut = false;
					// logger.info(
					// "Detected invalid usage of command parameters, skipping parameter."
					// );
				} else if ((arg.toLowerCase().equals("-eager"))
						|| (arg.toLowerCase().equals("-e"))) {
					nextArgIsName = false;
					nextArgIsLog = false;
					nextArgIsOut = false;
					mode = ExtractionMode.EAGER;
					// logger.info(
					// "Detected invalid usage of command parameters, skipping parameter."
					// );
					// logger.info( "Mode set to eager." );
				} else if ((arg.toLowerCase().equals("-complete"))
						|| (arg.toLowerCase().equals("-c"))) {
					nextArgIsName = false;
					nextArgIsLog = false;
					nextArgIsOut = false;
					mode = ExtractionMode.COMPLETE;
				} else {
					// set the according parameter
					if (nextArgIsLog) {
						logFilePath = arg;
					} else if (nextArgIsOut) {
						targetFilePath = arg;
					} else if (nextArgIsName) {
						programName = arg;
					}
					nextArgIsName = false;
					nextArgIsLog = false;
					nextArgIsOut = false;
				}
			} else {
				if ((arg.toLowerCase().equals("-log"))
						|| (arg.toLowerCase().equals("-l"))) {
					nextArgIsLog = true;
				} else if ((arg.toLowerCase().equals("-out"))
						|| (arg.toLowerCase().equals("-o"))) {
					nextArgIsOut = true;
				} else if ((arg.toLowerCase().equals("-name"))
						|| (arg.toLowerCase().equals("-n"))) {
					nextArgIsName = true;
				} else if ((arg.toLowerCase().equals("-eager"))
						|| (arg.toLowerCase().equals("-e"))) {
					mode = ExtractionMode.EAGER;
				} else if ((arg.toLowerCase().equals("-complete"))
						|| (arg.toLowerCase().equals("-c"))) {
					mode = ExtractionMode.COMPLETE;
				} else {
					// Check for given files or directories.
					File file = new File(arg);
					if (file.exists()) {
						searchFilesToParse(file);
					} else {
						System.out.println("Given file or directory \"" + arg
								+ "\" does not exist!");
						System.exit(0);
					}
				}
			}
		}
		if (nextArgIsLog || nextArgIsOut || nextArgIsName) {
			System.out
					.println(" Detected invalid usage of command parameters, skipping parameter.");
		}
		if (logFilePath == "") {
			System.out
					.println("No path for the log file has been given, using default...");
			logFilePath = "javaextractor";
		}
		if (programName == "") {
			System.out
					.println("No program name has been given, using default...");
			programName = "Program";
		}
		if (targetFilePath == "") {
			System.out
					.println("No path for the generated graph file has been given, using default...");
			targetFilePath = "extractedgraph.tg";
		}
	}

	/**
	 * Searches recursively for java files. The method decides what action to
	 * take based on the type of file it is looking at.
	 * 
	 * @param file
	 *            The file/path to begin with.
	 */
	private static void searchFilesToParse(File file) throws Exception {
		// If this is a directory, walk each file/dir in that directory.
		if (file.isDirectory()) {
			String files[] = file.list();
			for (String file2 : files) {
				searchFilesToParse(new File(file, file2));
			}
		}
		// Otherwise, if this is a java file add it to the file list.
		else {
			String fileName = file.getName();
			if (file.canRead()
					&& (fileName.length() > 5)
					&& fileName.substring(fileName.length() - 5)
							.equals(".java")) {
				// @TODO add files with suffix .JAV, too.
				fileList.add(file.getAbsolutePath());
			}
		}
	}

	/**
	 * Calculates time taken.
	 * 
	 * @param startDate
	 *            The date/time to use as start time.
	 */
	private static void finalizeLogging(Date startDate) {
		Date endDate = new Date();
		logger.info("End time: " + endDate.toString());
		long timeRequired = endDate.getTime() - startDate.getTime();
		logger.info("Time required: " + Long.toString(timeRequired / 3600000)
				+ ":" + Long.toString((timeRequired % 3600000) / 60000) + ":"
				+ Long.toString((timeRequired % 60000) / 1000) + ","
				+ Long.toString(timeRequired % 1000) + " (="
				+ Long.toString(timeRequired) + "ms)");
		System.out.println("Log file written to " + logFilePath);
	}
}
