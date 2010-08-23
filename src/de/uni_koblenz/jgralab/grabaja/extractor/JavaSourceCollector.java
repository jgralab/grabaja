package de.uni_koblenz.jgralab.grabaja.extractor;

import java.io.File;
import java.util.Vector;

/**
 * Collects java sources.
 * @author ultbreit@uni-koblenz.de
 */
public class JavaSourceCollector {

	/**
	 * List of java files collected.
	 */
	private Vector< String > fileList = new Vector< String >();
	
	/**
	 * Searches recursively for java files. Decides what action to take based on the type of file it is looking at.
	 * @param file The file/path to begin with.
	 */
	public void collectSourcesAt(File file) throws Exception{
		// If this is a directory, walk each file/dir in that directory.
		if( file.isDirectory() ){
			String files[] = file.list();
			for( int i=0; i < files.length; i++ )
				collectSourcesAt( new File( file, files[ i ] ) );
		}
		// Otherwise, if this is a java file add it to the file list.
		else
			if( file.canRead() && isJavaSource(file))
				fileList.add( file.getAbsolutePath() );
	}
	
	private boolean isJavaSource(File file){
		String fileName = file.getName();
		if(fileName.length() > 4)
			return fileName.endsWith(".java") || fileName.endsWith(".jav");
		else
			return false;
	}
	
	/**
	 * Gets list of files found holding java sources. 
	 * @return A list of files.
	 */
	public Vector< String > getFileList(){
		return this.fileList;
	}
}
