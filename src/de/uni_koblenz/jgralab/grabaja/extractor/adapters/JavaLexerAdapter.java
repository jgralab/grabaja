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
