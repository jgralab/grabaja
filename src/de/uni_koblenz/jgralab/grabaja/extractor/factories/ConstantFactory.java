package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.adapters.CommonASTAdapter;
import de.uni_koblenz.jgralab.grabaja.java5schema.BooleanConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.CharConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoubleConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.FloatConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.IntegerConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.LongConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.StringConstant;

/**
 * Provides functionality for creating builtin type literal elements in graph.
 * 
 * @author: abaldauf@uni-koblenz.de
 */
public class ConstantFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 * @param symbolTable
	 *            The symbol table to be used.
	 */
	public ConstantFactory(Java5 pg, SymbolTable symbolTable) {
		programGraph = pg;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates a vertex for an integer literal.
	 * 
	 * @param integerAST
	 *            The AST element representing the integer literal.
	 * @return The created vertex.
	 */
	public IntegerConstant createIntegerConstant(AST integerAST) {
		CommonASTAdapter integerConstantAST = (CommonASTAdapter) integerAST;
		if (symbolTable.hasIntegerConstant(integerConstantAST.getText())) {
			return symbolTable.getIntegerConstant(integerConstantAST.getText());
		}
		IntegerConstant integerConstantVertex = programGraph
				.createIntegerConstant();
		try {
			integerConstantVertex.set_value(Long.decode(integerConstantAST
					.getText()));
		} catch (Exception e) {
		}
		integerConstantVertex.set_literal(integerConstantAST.getText());
		symbolTable.addIntegerConstant(integerConstantAST.getText(),
				integerConstantVertex);
		return integerConstantVertex;
	}

	/**
	 * Creates a vertex for a char literal.
	 * 
	 * @param charAST
	 *            The AST element representing the char literal.
	 * @return The created vertex.
	 */
	public CharConstant createCharConstant(AST charAST) {
		CommonASTAdapter charConstantAST = (CommonASTAdapter) charAST;
		if (symbolTable.hasCharConstant(charConstantAST.getText())) {
			return symbolTable.getCharConstant(charConstantAST.getText());
		}
		CharConstant charConstantVertex = programGraph.createCharConstant();
		charConstantVertex.set_value(charConstantAST.getText().charAt(0));
		charConstantVertex.set_literal(charConstantAST.getText());
		symbolTable.addCharConstant(charConstantAST.getText(),
				charConstantVertex);
		return charConstantVertex;
	}

	/**
	 * Creates a vertex for a String literal.
	 * 
	 * @param stringAST
	 *            The AST element representing the String literal.
	 * @return The created vertex.
	 */
	public StringConstant createStringConstant(AST stringAST) {
		CommonASTAdapter stringConstantAST = (CommonASTAdapter) stringAST;
		if (symbolTable.hasStringConstant(stringConstantAST.getText())) {
			return symbolTable.getStringConstant(stringConstantAST.getText());
		}
		StringConstant stringConstantVertex = programGraph
				.createStringConstant();
		stringConstantVertex.set_value(stringConstantAST.getText());
		symbolTable.addStringConstant(stringConstantAST.getText(),
				stringConstantVertex);
		return stringConstantVertex;
	}

	/**
	 * Creates a vertex for a float literal.
	 * 
	 * @param floatAST
	 *            The AST element representing the float literal.
	 * @return The created vertex.
	 */
	public FloatConstant createFloatConstant(AST floatAST) {
		CommonASTAdapter floatConstantAST = (CommonASTAdapter) floatAST;
		if (symbolTable.hasFloatConstant(floatConstantAST.getText())) {
			return symbolTable.getFloatConstant(floatConstantAST.getText());
		}
		FloatConstant floatConstantVertex = programGraph.createFloatConstant();
		try {
			floatConstantVertex.set_value(Float.parseFloat(floatConstantAST
					.getText()));
		} catch (Exception e) {
		}
		floatConstantVertex.set_literal(floatConstantAST.getText());
		symbolTable.addFloatConstant(floatConstantAST.getText(),
				floatConstantVertex);
		return floatConstantVertex;
	}

	/**
	 * Creates a vertex for a double literal.
	 * 
	 * @param doubleAST
	 *            The AST element representing the double literal.
	 * @return The created vertex.
	 */
	public DoubleConstant createDoubleConstant(AST doubleAST) {
		CommonASTAdapter doubleConstantAST = (CommonASTAdapter) doubleAST;
		if (symbolTable.hasDoubleConstant(doubleConstantAST.getText())) {
			return symbolTable.getDoubleConstant(doubleConstantAST.getText());
		}
		DoubleConstant doubleConstantVertex = programGraph
				.createDoubleConstant();
		try {
			doubleConstantVertex.set_value(Double.parseDouble(doubleConstantAST
					.getText()));
		} catch (Exception e) {
		}
		doubleConstantVertex.set_literal(doubleConstantAST.getText());
		symbolTable.addDoubleConstant(doubleConstantAST.getText(),
				doubleConstantVertex);
		return doubleConstantVertex;
	}

	/**
	 * Creates a vertex for a long literal.
	 * 
	 * @param longAST
	 *            The AST element representing the long literal.
	 * @return The created vertex.
	 */
	public LongConstant createLongConstant(AST longAST) {
		CommonASTAdapter longConstantAST = (CommonASTAdapter) longAST;
		if (symbolTable.hasLongConstant(longConstantAST.getText())) {
			return symbolTable.getLongConstant(longConstantAST.getText());
		}
		LongConstant longConstantVertex = programGraph.createLongConstant();
		try {
			if (longConstantAST.getText().toUpperCase().endsWith("L")) {
				longConstantVertex.set_value(Long.decode(longConstantAST
						.getText().substring(0,
								longConstantAST.getText().length() - 1)));
			} else {
				longConstantVertex.set_value(Long.decode(longConstantAST
						.getText()));
			}
		} catch (Exception e) {
		}
		longConstantVertex.set_literal(longConstantAST.getText());
		symbolTable.addLongConstant(longConstantAST.getText(),
				longConstantVertex);
		return longConstantVertex;
	}

	/**
	 * Creates a vertex for a boolean "true" literal.
	 * 
	 * @return The created vertex.
	 */
	public BooleanConstant createBooleanTrueConstant() {
		if (!symbolTable.hasBooleanTrueConstant()) {
			BooleanConstant booleanTrueConstantVertex = programGraph
					.createBooleanConstant();
			booleanTrueConstantVertex.set_value(true);
			symbolTable.setBooleanTrueConstant(booleanTrueConstantVertex);
		}
		return symbolTable.getBooleanTrueConstant();
	}

	/**
	 * Creates a vertex for a boolean "false" literal.
	 * 
	 * @return The created vertex.
	 */
	public BooleanConstant createBooleanFalseConstant() {
		if (!symbolTable.hasBooleanFalseConstant()) {
			BooleanConstant booleanFalseConstantVertex = programGraph
					.createBooleanConstant();
			booleanFalseConstantVertex.set_value(false);
			symbolTable.setBooleanFalseConstant(booleanFalseConstantVertex);
		}
		return symbolTable.getBooleanFalseConstant();
	}

}