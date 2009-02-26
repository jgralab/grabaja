package de.uni_koblenz.jgralab.grabaja.extractor;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * The main class of the javaextractor project.
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class JavaExtractor{

    /**
     * The name of the parsed program.
     * Default value or setting will be set in commandline argument interpretation.
     */
	private static String programName = "";

    /**
     * The name / path of the generated graph file.
     * Default value or setting will be set in commandline argument interpretation.
     */
	private static String targetFilePath = "";

    /**
     * The name / path of the log file.
     * Default value or setting will be set in commandline argument interpretation.
     */
    private static String logFilePath = "";

	/**
	 * Flag indicates if command line argument <code>-eager</code> has been given.
	 * Default behaviour is lazy.
	 */
	private static ExtractionMode mode = ExtractionMode.LAZY;

	/**
	 * List of the files to parse.
	 */
	public static Vector< String > fileList = new Vector< String >();

	/**
	 * The logger which logs the messages to disk.
	 */
	private static Logger logger;

	/**
	 * The main method.
	 * @param args A string array of command line arguments.
	 */
	public static void main( String[] args ){

		System.out.println("\nJavaExtractor v1.0 University of Koblenz\n");
		Date startDate = new Date(); // Getting start time so we can calculate duration of extraction process.

		try{
			parseCommandLineArguments( args ); // Checks arguments and collects files to extract graph from.
			initializeLogging();
			if( !fileList.isEmpty() ){
				GraphBuilder graphBuilder = new GraphBuilder( programName, logger ); // Automatically builds graph from file list.
				graphBuilder.parseFiles( fileList );
				graphBuilder.finalizeGraph( mode );
				graphBuilder.saveGraphToDisk( targetFilePath ); // Saves graph to disk.
			}
		}
		catch( Exception exception ){
			System.out.println( " An unexpected exception occurred in main-method!" );
			exception.printStackTrace( System.err );
			logger.warning( Utilities.stackTraceToString( exception ) );
		}
		finally{ finalizeLogging( startDate ); }
	}

	/**
	 * Initializes the logger.
	 */
	private static void initializeLogging(){
		SimpleFormatter simpleFormater = new SimpleFormatter();
		try{
			FileHandler fileHandler = new FileHandler( logFilePath + ".log");
			fileHandler.setFormatter( simpleFormater );
			logger = Logger.getLogger( "javaextractor.logger");
			logger.addHandler( fileHandler );
		}
		catch( IOException exception ){
			System.out.println( " Log file could not be created at specified location.\n" );
			System.exit( 0 );
		}
	}

	/**
	 * Parses the commandline arguments passed over to the program and sets the according settings.
	 * @param args A string array of command line arguments.
	 */
	private static void parseCommandLineArguments( String[] args ) throws Exception{
		if( args.length <= 0 ){ // Check if we have at least one command-line argument
			System.out.println( "Usage:\n java javaextractor.JavaExtractor [-log <file name>] [-out <file name>] [-name <name>] <directory or file name> {directory or file name}" );
			System.exit( 0 );
		}
		boolean nextArgIsLog = false;
		boolean nextArgIsOut = false;
		boolean nextArgIsName = false;
		for( int i = 0; i < args.length; i++ ){
			if( nextArgIsLog || nextArgIsOut || nextArgIsName ){
				if( ( args[i].toLowerCase().equals("-log") ) || ( args[i].toLowerCase().equals("-l") ) ) {
					nextArgIsLog = true;
					nextArgIsOut = false;
					nextArgIsName = false;
					//logger.info( "Detected invalid usage of command parameters, skipping parameter." );
				}
				else if( ( args[i].toLowerCase().equals("-out") ) || ( args[i].toLowerCase().equals("-o") ) ) {
					nextArgIsOut = true;
					nextArgIsLog = false;
					nextArgIsName = false;
					//logger.info( "Detected invalid usage of command parameters, skipping parameter." );
				}
				else if( ( args[i].toLowerCase().equals("-name") ) || ( args[i].toLowerCase().equals("-n") ) ) {
					nextArgIsName = true;
					nextArgIsLog = false;
					nextArgIsOut = false;
					//logger.info( "Detected invalid usage of command parameters, skipping parameter." );
				}
				else if( ( args[ i ].toLowerCase().equals( "-eager" ) ) || ( args[ i ].toLowerCase().equals( "-e" ) ) ) {
					nextArgIsName = false;
					nextArgIsLog = false;
					nextArgIsOut = false;
					mode = ExtractionMode.EAGER;
					//logger.info( "Detected invalid usage of command parameters, skipping parameter." );
			    	//logger.info( "Mode set to eager." );
				}
				else if( ( args[ i ].toLowerCase().equals( "-complete" ) ) || ( args[ i ].toLowerCase().equals( "-c" ) ) ) {
					nextArgIsName = false;
					nextArgIsLog = false;
					nextArgIsOut = false;
					mode = ExtractionMode.COMPLETE;
				}
			    else{
					// set the according parameter
					if( nextArgIsLog ){
						logFilePath = args[ i ];
					}
					else if( nextArgIsOut ){
						targetFilePath = args[ i ];
					}
					else if( nextArgIsName ){
						programName = args[ i ];
					}
					nextArgIsName = false;
					nextArgIsLog = false;
					nextArgIsOut = false;
				}
			}
			else{
				if( ( args[ i ].toLowerCase().equals( "-log" ) ) || ( args[ i ].toLowerCase().equals( "-l" ) ) ) {
					nextArgIsLog = true;
				}
				else if( ( args[ i ].toLowerCase().equals( "-out" ) ) || ( args[ i ].toLowerCase().equals( "-o" ) ) ) {
					nextArgIsOut = true;
				}
				else if( ( args[ i ].toLowerCase().equals( "-name" ) ) || ( args[ i ].toLowerCase().equals( "-n" ) ) ) {
					nextArgIsName = true;
				}
				else if( ( args[ i ].toLowerCase().equals( "-eager" ) ) || ( args[ i ].toLowerCase().equals( "-e" ) ) ) {
					mode = ExtractionMode.EAGER;
				}
				else if( ( args[ i ].toLowerCase().equals( "-complete" ) ) || ( args[ i ].toLowerCase().equals( "-c" ) ) ) {
					mode = ExtractionMode.COMPLETE;
				}
			    else{
			    	// Check for given files or directories.
					File file = new File( args[ i ] );
					if( file.exists() ) searchFilesToParse( file );
				    else{
						System.out.println( "Given file or directory \"" + args[ i ] + "\" does not exist!" );
						System.exit( 0 );
					}
				}
			}
		}
		if( nextArgIsLog || nextArgIsOut || nextArgIsName ){
			System.out.println( " Detected invalid usage of command parameters, skipping parameter." );
		}
		if( logFilePath == "" ){
			System.out.println( "No path for the log file has been given, using default..." );
			logFilePath = "javaextractor";
		}
		if( programName == "" ){
			System.out.println( "No program name has been given, using default..." );
			programName = "Program";
		}
		if( targetFilePath == "" ){
			System.out.println( "No path for the generated graph file has been given, using default..." );
			targetFilePath = "extractedgraph.tg";
		}
	}

	/**
	 * Searches recursively for java files. The method decides what action to take based on the type of file it is looking at.
	 * @param file The file/path to begin with.
	 */
	private static void searchFilesToParse( File file ) throws Exception{
		// If this is a directory, walk each file/dir in that directory.
		if( file.isDirectory() ){
			String files[] = file.list();
			for( int i=0; i < files.length; i++ )
				searchFilesToParse( new File( file, files[ i ] ) );
		}
		// Otherwise, if this is a java file add it to the file list.
		else{
			String fileName = file.getName();
			if( file.canRead() && ( fileName.length() > 5 ) && fileName.substring(fileName.length() - 5).equals( ".java" ) ){
				// @TODO add files with suffix .JAV, too.
				fileList.add( file.getAbsolutePath() );
			}
		}
	}

	/**
	 * Calculates time taken.
	 * @param startDate The date/time to use as start time.
	 */
	private static void finalizeLogging( Date startDate ){
		Date endDate = new Date();
		logger.info( "End time: " + endDate.toString() );
		long timeRequired = endDate.getTime() - startDate.getTime();
		logger.info( "Time required: " + Long.toString( timeRequired / 3600000 )
									  + ":"
									  + Long.toString( ( timeRequired % 3600000 ) / 60000 )
									  + ":" + Long.toString( ( timeRequired % 60000 ) / 1000 )
									  + "," + Long.toString( timeRequired % 1000 )
									  + " (="
									  + Long.toString( timeRequired )
									  + "ms)" );
		System.out.println( "Log file written to " + logFilePath );
	}

}