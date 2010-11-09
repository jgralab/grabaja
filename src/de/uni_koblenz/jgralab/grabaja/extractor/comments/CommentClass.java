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
package de.uni_koblenz.jgralab.grabaja.extractor.comments;

import java.util.logging.Logger;

import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;

/**
 * Represents a comment found by the <code>JavaLexerAdapter</code> in a
 * java-file.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public abstract class CommentClass {

	/**
	 * Holds the comment's offset in the file where it has been found.
	 */
	protected int offset;

	/**
	 * Holds the comment's length in the file where it has been found.
	 */
	protected int length;

	/**
	 * Holds the comment's line number (beginning at 1) in the file where it has
	 * been found.
	 */
	protected int line;

	/**
	 * Holds the comment's column number (beginning at 1) in the line.
	 */
	protected int column;

	/**
	 * Holds the logger.
	 */
	protected Logger logger;

	/**
	 * Creates a comment from given values.
	 * 
	 * @param offset
	 *            The offset of the comment.
	 * @param length
	 *            The length of the comment.
	 * @param line
	 *            The line number of the comment in the file where it has been
	 *            found.
	 * @param column
	 *            The start column number of the line.
	 */
	public CommentClass(int offset, int length, int line, int column,
			Logger logger) {
		this.offset = offset;
		this.length = length;
		this.line = line;
		this.column = column;
		this.logger = logger;
	}

	/**
	 * Creates the structure for a TGraph resulting from this comment.
	 * 
	 * @param programGraph
	 *            The graph to add this comment to.
	 * @param unitToAttachTo
	 *            The <code>TranslationUnit</code> to attach this comment to.
	 */
	public abstract void createTGraphElements(Java5 programGraph,
			TranslationUnit unitToAttachTo);
}
