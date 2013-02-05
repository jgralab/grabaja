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

import de.uni_koblenz.jgralab.exception.GraphException;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCommentIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaDocComment;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;

/**
 * Represents a javadoc comment found by the <code>JavaLexerAdapter</code> in a
 * java-file.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class JavaDocCommentClass extends CommentClass {

	/**
	 * Creates a <code>JavaDocComment</code> from given values.
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
	public JavaDocCommentClass(int offset, int length, int line, int column,
			Logger logger) {
		super(offset, length, line, column, logger);
	}

	/**
	 * Creates the structure for a TGraph resulting from this comment.
	 * 
	 * @param programGraph
	 *            The TGraph containing the <code>TranslationUnit</coe>-vertex.
	 * @param unitToAttachTo
	 *            The <code>TranslationUnit</code>-vertex to attach the
	 *            <code>JavaDocComment</code>-vertex to.
	 */
	@Override
	public void createTGraphElements(Java5 programGraph,
			TranslationUnit unitToAttachTo) {
		try {
			JavaDocComment javaDocComment = programGraph.createJavaDocComment();
			IsCommentIn attachingEdge = programGraph.createIsCommentIn(
					javaDocComment, unitToAttachTo);
			attachingEdge.set_offset(offset);
			attachingEdge.set_length(length);
			attachingEdge.set_line(line);
			attachingEdge.set_column(column);
		} catch (GraphException exception) {
			exception.printStackTrace(System.err);
			logger.severe(Utilities.stackTraceToString(exception));
		}
	}
}
