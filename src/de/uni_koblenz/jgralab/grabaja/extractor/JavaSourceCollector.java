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
import java.util.Vector;

/**
 * Collects java sources.
 * 
 * @author ultbreit@uni-koblenz.de
 */
public class JavaSourceCollector {

	/**
	 * List of java files collected.
	 */
	private Vector<String> fileList = new Vector<String>();

	/**
	 * Searches recursively for java files. Decides what action to take based on
	 * the type of file it is looking at.
	 * 
	 * @param file
	 *            The file/path to begin with.
	 */
	public void collectSourcesAt(File file) throws Exception {
		// If this is a directory, walk each file/dir in that directory.
		if (file.isDirectory()) {
			String files[] = file.list();
			for (int i = 0; i < files.length; i++)
				collectSourcesAt(new File(file, files[i]));
		}
		// Otherwise, if this is a java file add it to the file list.
		else if (file.canRead() && isJavaSource(file))
			fileList.add(file.getAbsolutePath());
	}

	private boolean isJavaSource(File file) {
		String fileName = file.getName();
		if (fileName.length() > 4)
			return fileName.endsWith(".java") || fileName.endsWith(".jav");
		else
			return false;
	}

	/**
	 * Gets list of files found holding java sources.
	 * 
	 * @return A list of files.
	 */
	public Vector<String> getFileList() {
		return this.fileList;
	}
}
