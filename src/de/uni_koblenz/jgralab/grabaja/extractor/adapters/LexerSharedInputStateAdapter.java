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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import antlr.CommonToken;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;

/**
 * Extension of {@link LexerSharedInputState} that is aware of file names and
 * can annotate {@link ExtentTokens} with them and with end position
 * information.
 * 
 * <p>
 * This file is in the public domain.
 * </p>
 * 
 * @author Dan Bornstein, danfuzz@milk.com
 * @author: ultbreit@uni-koblenz.de
 */
public class LexerSharedInputStateAdapter extends LexerSharedInputState {
	/** the name of the file this instance refers to */
	private String fileName;

	/**
	 * The current offset position in the input file.
	 */
	public int offset = 0;

	/**
	 * Creates an instance.
	 * 
	 * @param s
	 *            The input buffer to use
	 */
	public LexerSharedInputStateAdapter(InputBuffer inputBuffer) {
		super(inputBuffer);
	}

	/**
	 * Construct an instance.
	 * 
	 * @param s
	 *            The input stream to use
	 * @param name
	 *            null-ok; the file name to associate with this instance
	 */
	public LexerSharedInputStateAdapter(InputStream inputStream, String name) {
		super(inputStream);
		fileName = name;
	}

	/**
	 * Construct an instance. The file name is set to <code>null</code>
	 * initially.
	 * 
	 * @param s
	 *            the input stream to use
	 */
	public LexerSharedInputStateAdapter(InputStream inputStream) {
		this(inputStream, null);
	}

	/**
	 * Construct an instance which opens and reads the named file.
	 * 
	 * @param name
	 *            non-null; the name of the file to use
	 */
	public LexerSharedInputStateAdapter(String name) throws IOException {
		this(new FileInputStream(name), name);
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
	 * Annotate an {@link CommonToken} based on this instance. It sets the end
	 * position information as well as the file name.
	 * 
	 * @param token
	 *            non-null; the token to annotate
	 */
	public void annotate(CommonTokenAdapter token) {
		token.setEndLine(line);
		token.setEndColumn(column);
		token.setFileName(fileName);
		token.setOffset(offset + tokenStartColumn - 1);
	}

	/**
	 * @return The current offset position.
	 */
	public int getOffset() {
		return offset + tokenStartColumn - 1;
	}

	/**
	 * Resets all values of this instance.
	 */
	@Override
	public void reset() {
		super.reset();
		offset = 0;
	}

	/**
	 * Sets the current column position.
	 * 
	 * @param col
	 *            The column number
	 */
	public void setColumn(int col) {
		column = col;
	}

}
