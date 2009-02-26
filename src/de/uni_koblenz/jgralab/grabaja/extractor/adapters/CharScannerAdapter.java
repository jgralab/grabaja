package de.uni_koblenz.jgralab.grabaja.extractor.adapters;

import antlr.ANTLRHashString;
import antlr.ANTLRStringBuffer;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;

/**
 * Adapts <code>antlr.CharScanner</code> by adding needed methods to it.
 * These methods have absolutely to be known by the lexer at compile time.
 * @author: ultbreit@uni-koblenz.de
 */
public abstract class CharScannerAdapter extends CharScanner{

    /**
     * Creates a <code>CharScannerAdapter</code>.
     * Tells the lexer to use <code>CommonTokenAdapter</code> for token creation.
     */
    public CharScannerAdapter() {
        text = new ANTLRStringBuffer();
        hashString = new ANTLRHashString(this);
        setTokenObjectClass("de.gupro.javaextractor.adapters.CommonTokenAdapter");
    }

    /**
     * Creates a <code>CharScannerAdapter</code> from given values.
     * @param cb The <code>InpuBuffer</code> to use.
     */
    public CharScannerAdapter( InputBuffer cb ){
        this();
        inputState = new LexerSharedInputStateAdapter( cb );
    }

    /**
     * Creates a <code>CharScannerAdapter</code> from given values.
     * @param sharedInpuState The <code>LexerSharedInputState</code> to use.
     */
    public CharScannerAdapter( LexerSharedInputState sharedInputState ){
        this();
        inputState = (LexerSharedInputStateAdapter)sharedInputState;
    }

    /**
     * Returns the actual offset (starting at 0).
     */
    public int getOffset(){
		LexerSharedInputStateAdapter jelsis = (LexerSharedInputStateAdapter)inputState;
        return jelsis.getOffset();
    }

    /**
     * Returns the actual line number (starting at 1).
     */
    public int getLine(){
		LexerSharedInputStateAdapter jelsis = (LexerSharedInputStateAdapter)inputState;
        return jelsis.getLine();
    }

    /**
     * Returns the actual column number (starting at 1).
     */
    public int getColumn(){
		LexerSharedInputStateAdapter jelsis = (LexerSharedInputStateAdapter)inputState;
	    return jelsis.getColumn();
    }
}