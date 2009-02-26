package de.uni_koblenz.jgralab.grabaja.extractor.comments;

import java.util.logging.Logger;

import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;


/**
 * Represents a comment found by the <code>JavaLexerAdapter</code> in a java-file.
 * @author: ultbreit@uni-koblenz.de
 */
public abstract class CommentClass{

	/**
	 * Holds the comment's offset in the file where it has been found.
	 */
	protected int offset;

	/**
	 * Holds the comment's length in the file where it has been found.
	 */
	protected int length;

	/**
	 * Holds the comment's line number (beginning at 1) in the file where it has been found.
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
     * @param offset The offset of the comment.
     * @param length The length of the comment.
     * @param line   The line number of the comment in the file where it has been found.
     * @param column The start column number of the line.
     */
    public CommentClass( int offset, int length, int line, int column, Logger logger ){
        this.offset = offset;
        this.length = length;
        this.line = line;
        this.column = column;
        this.logger = logger;
    }

    /**
     * Creates the structure for a TGraph resulting from this comment.
     * @param programGraph The graph to add this comment to.
     * @param unitToAttachTo The <code>TranslationUnit</code> to attach this comment to.
     */
    public abstract void createTGraphElements( Java5 programGraph, TranslationUnit unitToAttachTo );
}
