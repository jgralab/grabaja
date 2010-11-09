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
package de.uni_koblenz.jgralab.grabaja.extractor.adapters;

import antlr.CommonToken;

/**
 * This is a token class which can keep track of full extent information. That
 * is, it knows about end positions as well as start positions and the file
 * (name) of origin of a token. Note that, conforming to standard Java usage,
 * the end column value is numerically one more than the actual end column
 * position, making (end column - start column) be the length of the token, if
 * it is on a single line, and allowing for the possibility of 0-length tokens.
 * If, for some reason, a start or end position hasn't been calculated for an
 * instance, it will return <code>0</code> from the accessors in question. Since
 * valid line and column nubmers start at <code>1</code>, it is thus possible to
 * distinguish these from all legitimately set values.
 * <p>
 * This file is in the public domain.
 * </p>
 * 
 * @author Dan Bornstein, danfuzz@milk.com
 * @author: ultbreit@uni-koblenz.de
 */
public class CommonTokenAdapter extends CommonToken {
	/**
	 * the end line of the token
	 */
	protected int endLine;

	/**
	 * the end column of the token (+1, see discussion in the header)
	 */
	protected int endCol;

	/**
	 * null-ok; the file (name) of origin of the token
	 */
	protected java.lang.String fileName;

	/**
	 * the offset of the token
	 */
	protected int offset;

	/**
	 * Construct an instance. The instance will be of type {@link #INVALID_TYPE}
	 * , have empty (<code>""</code>, not <code>null</code>) text, have
	 * <code>0</code> for all position values, and have a <code>null</code> file
	 * name.
	 */
	public CommonTokenAdapter() {
		this("");
	}

	/**
	 * Construct an instance with the given text. The instance will be of type
	 * {@link #INVALID_TYPE}, have <code>0</code> for all position values, and
	 * have a <code>null</code> file name.
	 * 
	 * @param text
	 *            null-ok; the token text
	 */
	public CommonTokenAdapter(String text) {
		this(INVALID_TYPE, text);
	}

	/**
	 * Construct an instance with the given type and text. The instance will
	 * have <code>0</code> for all position values and have a <code>null</code>
	 * file name.
	 * 
	 * @param type
	 *            the token type
	 * @param text
	 *            null-ok; the token text
	 */
	public CommonTokenAdapter(int type, String text) {
		super(type, text);
		line = 0;
		col = 0;
		endLine = 0;
		endCol = 0;
		fileName = null;
		offset = 0;
	}

	/**
	 * Get the end line of this instance.
	 * 
	 * @return the end line number
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * Get the end column of this instance.
	 * 
	 * @return the end column number
	 */
	public int getEndColumn() {
		return endCol;
	}

	/**
	 * Get the file name of this instance.
	 * 
	 * @return null-ok; the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get the offset of this instance.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Set the end line of this instance.
	 * 
	 * @param lineNum
	 *            the new end line number
	 */
	public void setEndLine(int lineNumber) {
		endLine = lineNumber;
	}

	/**
	 * Set the end column of this instance.
	 * 
	 * @param colNum
	 *            the new end column number
	 */
	public void setEndColumn(int columnNumber) {
		endCol = columnNumber;
	}

	/**
	 * Set the file name of this instance.
	 * 
	 * @param name
	 *            null-ok; the file name
	 */
	public void setFileName(String name) {
		fileName = name;
	}

	/**
	 * Set the offset of this instance.
	 * 
	 * @param offset
	 *            the offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Set the extent of this instance from the given AST. The AST is walked,
	 * and each node is consulted. The minimum of all start positions becomes
	 * the start position for this instance, and the maximum of all end
	 * positions becomes the end position for this instance. The file name is
	 * set to be the file name associated with the minimally-positioned token.
	 * (This is as good a heuristic as any given that there is no way to order
	 * multiple files in this implementation.) This method attempts to be smart
	 * by ignoring <code>0</code> position values, and, if no end position is
	 * defined in any of the consulted nodes, it will set the end position of
	 * this instance to be the same as the start position. This method uses the
	 * position variables of this instance during the tree walk, so it is unwise
	 * to use an instance which is pointed at by the given AST.
	 * 
	 * @param ast
	 *            the tree to figure out the extent of
	 */
	public void setFromCommonASTAdapter(CommonASTAdapter ast) {
		line = Integer.MAX_VALUE;
		col = Integer.MAX_VALUE;
		endLine = Integer.MIN_VALUE;
		endCol = Integer.MIN_VALUE;
		fileName = null;

		setFromCommonASTAdapter(this, ast);

		if (line == Integer.MAX_VALUE) {
			line = 0;
			col = 0;
		} else if (col == Integer.MAX_VALUE) {
			col = 0;
		}

		if (endLine == Integer.MIN_VALUE) {
			endLine = line;
			endCol = col;
		} else if (endCol == Integer.MIN_VALUE) {
			endCol = 0;
		}
	}

	/**
	 * Helper method for figuring out the extent of an AST. This is called by
	 * the public method of the same name, and it will in turn call itself
	 * recursively.
	 * 
	 * @param result
	 *            non-null; the token to destructively modify as the result of
	 *            the operation
	 * @param ast
	 *            non-null; the tree to walk
	 */
	static private void setFromCommonASTAdapter(CommonTokenAdapter result,
			CommonASTAdapter ast) {
		CommonTokenAdapter tok = (CommonTokenAdapter) ast.getToken();

		if (tok != null) {
			int line = tok.getLine();
			if (line != 0) {
				int col = tok.getColumn();
				int minLine = result.getLine();
				int minCol = result.getColumn();
				if (line < minLine) {
					result.setLine(line);
					result.setColumn(col);
					result.setFileName(tok.getFileName());
				} else if ((line == minLine) && (col < minCol)) {
					result.setColumn(col);
					result.setFileName(tok.getFileName());
				}
			}

			int endLine = tok.getEndLine();
			if (endLine != 0) {
				int endCol = tok.getEndColumn();
				int maxLine = result.getEndLine();
				int maxCol = result.getEndColumn();
				if (endLine > maxLine) {
					result.setEndLine(endLine);
					result.setEndColumn(endCol);
				} else if ((endLine == maxLine) && (endCol > maxCol)) {
					result.setEndColumn(endCol);
				}
			}
		}

		ast = (CommonASTAdapter) ast.getFirstChild();
		while (ast != null) {
			setFromCommonASTAdapter(result, ast);
			ast = (CommonASTAdapter) ast.getNextSibling();
		}
	}

	/**
	 * @return The length of the token's code in the source file.
	 */
	public int getLength() {
		return text.length();
	}

}
