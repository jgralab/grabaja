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

import antlr.ANTLRHashString;
import antlr.ANTLRStringBuffer;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;

/**
 * Adapts <code>antlr.CharScanner</code> by adding needed methods to it. These
 * methods have absolutely to be known by the lexer at compile time.
 *
 * @author: ultbreit@uni-koblenz.de
 */
public abstract class CharScannerAdapter extends CharScanner {

	/**
	 * Creates a <code>CharScannerAdapter</code>. Tells the lexer to use
	 * <code>CommonTokenAdapter</code> for token creation.
	 */
	public CharScannerAdapter() {
		text = new ANTLRStringBuffer();
		hashString = new ANTLRHashString(this);
		setTokenObjectClass("de.uni_koblenz.jgralab.grabaja.extractor.adapters.CommonTokenAdapter");
	}

	/**
	 * Creates a <code>CharScannerAdapter</code> from given values.
	 *
	 * @param cb
	 *            The <code>InpuBuffer</code> to use.
	 */
	public CharScannerAdapter(InputBuffer cb) {
		this();
		inputState = new LexerSharedInputStateAdapter(cb);
	}

	/**
	 * Creates a <code>CharScannerAdapter</code> from given values.
	 *
	 * @param sharedInpuState
	 *            The <code>LexerSharedInputState</code> to use.
	 */
	public CharScannerAdapter(LexerSharedInputState sharedInputState) {
		this();
		inputState = sharedInputState;
	}

	/**
	 * Returns the actual offset (starting at 0).
	 */
	public int getOffset() {
		LexerSharedInputStateAdapter jelsis = (LexerSharedInputStateAdapter) inputState;
		return jelsis.getOffset();
	}

	/**
	 * Returns the actual line number (starting at 1).
	 */
	@Override
	public int getLine() {
		LexerSharedInputStateAdapter jelsis = (LexerSharedInputStateAdapter) inputState;
		return jelsis.getLine();
	}

	/**
	 * Returns the actual column number (starting at 1).
	 */
	@Override
	public int getColumn() {
		LexerSharedInputStateAdapter jelsis = (LexerSharedInputStateAdapter) inputState;
		return jelsis.getColumn();
	}
}
