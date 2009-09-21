package de.uni_koblenz.jgralab.grabaja.extractor.comments;

import java.util.logging.Logger;

import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCommentIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.MultiLineComment;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;

/**
 * Represents a multiple-line comment found by the <code>JavaLexerAdapter</code>
 * in a java-file.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class MultiLineCommentClass extends CommentClass {

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
	public MultiLineCommentClass(int offset, int length, int line, int column,
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
	 *            <code>MultiLineComment</code>-vertex to.
	 */
	@Override
	public void createTGraphElements(Java5 programGraph,
			TranslationUnit unitToAttachTo) {
		try {
			MultiLineComment multiLineComment = programGraph
					.createMultiLineComment();
			IsCommentIn attachingEdge = programGraph.createIsCommentIn(
					multiLineComment, unitToAttachTo);
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