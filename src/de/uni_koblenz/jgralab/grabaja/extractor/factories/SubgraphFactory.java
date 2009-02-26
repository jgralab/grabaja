package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;

/**
 * Abstract super class for factories.
 * @author: ultbreit@uni-koblenz.de
 */
public class SubgraphFactory{

	/**
	 * Reference the graph.
	 */
    protected static Java5 programGraph;

	/**
	 * Reference to the symbol table of graph.
	 */
    protected SymbolTable symbolTable;

	/**
	 * Sets the reference to the graph.
	 * @param programGraph The graph.
	 */
    public void setProgramGraph( Java5 pg ){
        programGraph = pg;
    }

	/**
	 * Sets the reference to the symbol table of graph.
	 * @param symbolTable The symbol table.
	 */
    public void setSymbolTable( SymbolTable symbolTable ){
        this.symbolTable = symbolTable;
    }

}