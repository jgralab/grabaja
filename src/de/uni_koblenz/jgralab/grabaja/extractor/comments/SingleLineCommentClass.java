package de.uni_koblenz.jgralab.grabaja.extractor.comments;

import java.util.logging.Logger;

import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCommentIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.SingleLineComment;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;


/**
 * Represents a single-line comment found by the <code>JavaLexerAdapter</code> in a java-file.
 * @author: ultbreit@uni-koblenz.de
 */
public class SingleLineCommentClass extends CommentClass{

    /**
     * Creates a <code>SingleLineComment</code> from given values.
     * @param offset The offset of the comment.
     * @param length The length of the comment.
     * @param line   The line number of the comment in the file where it has been found.
     * @param column The start column number of the line.
     */
    public SingleLineCommentClass( int offset, int length, int line, int column, Logger logger ){
        super( offset, length, line, column, logger );
    }

    /**
     * Creates the structure for a TGraph resulting from this comment.
     * @param programGraph The TGraph containing the <code>TranslationUnit</coe>-vertex.
     * @param unitToAttachTo The <code>TranslationUnit</code>-vertex to attach the <code>SingleLineComment</code>-vertex to.
     */
    public void createTGraphElements( Java5 programGraph, TranslationUnit unitToAttachTo ){
		try{
			SingleLineComment singleLineComment = programGraph.createSingleLineComment();
			IsCommentIn attachingEdge = programGraph.createIsCommentIn( singleLineComment, unitToAttachTo );
			attachingEdge.setOffset(offset);
			attachingEdge.setLength(length);
			attachingEdge.setLine(line);
			attachingEdge.setColumn(column);
		}
		catch( GraphException exception ){
			exception.printStackTrace(System.err);
			logger.severe( Utilities.stackTraceToString( exception ) );
		}
	}
}