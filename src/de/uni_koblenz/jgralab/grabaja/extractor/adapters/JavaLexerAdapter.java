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

import antlr.LexerSharedInputState;
import antlr.Token;
import de.uni_koblenz.jgralab.grabaja.extractor.parser.JavaLexer;

/**
 * @author: ultbreit@uni-koblenz.de
 */
public class JavaLexerAdapter extends JavaLexer {

    /**
     * Creates an initializes an instance of the lexer adapter.
     * @param inputState The LexerSharedInputState instance to be used.
     */
	public JavaLexerAdapter( LexerSharedInputState inputState ){
		super( inputState );
	}

    /**
     * Creates a token of the given type, augmenting it with end position
     * and file name information based on the shared input state of the
     * instance.
     *
     * @param t The token type for the result.
     * @return The newly-constructed token (should not be null)
     */
    @Override
	protected Token makeToken( int t ){
        CommonTokenAdapter tok = ( CommonTokenAdapter )super.makeToken( t );
        ( ( LexerSharedInputStateAdapter ) inputState ).annotate( tok );
        return tok;
    }

    /**
     * Adds the number of columns of the last line to the temporary offset.
     */
    @Override
	public void newline(){
    	( ( LexerSharedInputStateAdapter ) inputState ).offset += inputState.getColumn() - 1;
        super.newline();
    }

    /**
     * Increases the number of columns by 1, because we do not need a greater tabsize.
     */
    @Override
	public void tab(){
       int i = inputState.getColumn();
       i++;
       ( ( LexerSharedInputStateAdapter ) inputState ).setColumn( i );
    }

}
