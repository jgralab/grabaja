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
package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import java.util.HashMap;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayType;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInTypes;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElementTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsReturnTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfAnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;

/**
 * Provides functionality for creating type specification elements in graph.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class TypeSpecificationFactory extends SubgraphFactory {

	/**
	 * Stores builtin type vertices already in use.
	 */
	HashMap<BuiltInTypes, BuiltInType> builtInTypes;

	/**
	 * Creates and initializes a TypeSpecificationFactory.
	 * 
	 * @param programGraph
	 *            Graph to be used.
	 * @param symbolTable
	 *            Symbol table to be used.
	 */
	public TypeSpecificationFactory(Java5 programGraph, SymbolTable symbolTable) {
		SubgraphFactory.programGraph = programGraph;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates a vertex for a builtin type.
	 * 
	 * @param builtInTypeType
	 *            The builtin type's type.
	 * @return The created vertex.
	 */
	public BuiltInType createBuiltInType(BuiltInTypes builtInType) {
		if (symbolTable.hasBuiltInType(builtInType)) {
			return symbolTable.getBuiltInType(builtInType);
		}
		BuiltInType builtInTypeVertex = programGraph.createBuiltInType();
		builtInTypeVertex.set_type(builtInType);
		symbolTable.addBuiltInType(builtInType, builtInTypeVertex);
		return builtInTypeVertex;
	}

	/**
	 * Creates an edge between a type specification and a method declaration
	 * (return type), parameter declaration (parameter type) or annotation field
	 * (field type).
	 * 
	 * @param typeSpecificationVertex
	 *            Vertex representing a type specification.
	 * @param parentVertex
	 *            Vertex representing a method, parameter or annotation field.
	 * @param beginAST
	 *            AST element representing first element of type specification
	 *            in source code.
	 * @param endAST
	 *            AST element representing last element of type specification in
	 *            source code.
	 */
	public boolean attachTypeSpecification(
			TypeSpecification typeSpecificationVertex, Vertex parentVertex,
			AST beginAST, AST endAST) {
		if (parentVertex instanceof MethodDeclaration) {
			IsReturnTypeOf isReturnTypeOfEdge = programGraph
					.createIsReturnTypeOf(typeSpecificationVertex,
							(MethodDeclaration) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(isReturnTypeOfEdge,
					beginAST, endAST);
			return true;
		}
		if (parentVertex instanceof ParameterDeclaration) {
			IsTypeOfParameter isTypeOfParameterEdge = programGraph
					.createIsTypeOfParameter(typeSpecificationVertex,
							(ParameterDeclaration) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isTypeOfParameterEdge, beginAST, endAST);
			return true;
		}
		if (parentVertex instanceof AnnotationField) {
			IsTypeOfAnnotationField isTypeOfAnnotationFieldEdge = programGraph
					.createIsTypeOfAnnotationField(typeSpecificationVertex,
							(AnnotationField) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isTypeOfAnnotationFieldEdge, beginAST, endAST);
			return true;
		}
		return false;
	}

	/**
	 * Creates a vertex representing an array type.
	 * 
	 * @param currentDimensionCount
	 *            The amount of dimensions of the array.
	 * @param typeSpecificationVertex
	 *            The type specification of the array's elements.
	 * @param beginAST
	 *            AST element representing the first element of type
	 *            specification.
	 * @param endAST
	 *            AST element representing the last element of type
	 *            specification.
	 * @return A vertex representing an array type.
	 */
	public ArrayType createArrayType(int currentDimensionCount,
			TypeSpecification typeSpecificationVertex, AST beginAST, AST endAST) {
		ArrayType arrayTypeVertex = programGraph.createArrayType();
		arrayTypeVertex.set_dimensions(currentDimensionCount);
		IsElementTypeOf isElementTypeOfEdge = programGraph
				.createIsElementTypeOf(typeSpecificationVertex, arrayTypeVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isElementTypeOfEdge,
				beginAST, endAST);
		return arrayTypeVertex;
	}

	/**
	 * Creates an edge between a variable's type specification and the variable
	 * declaration.
	 * 
	 * @param typeSpecificationVertex
	 *            The type specification.
	 * @param variableDeclarationVertex
	 *            The variable declaration.
	 * @param beginAST
	 *            The AST element representing the first element of the type
	 *            specification.
	 * @param endAST
	 *            The AST element representing the last element of the type
	 *            specification.
	 */
	public void attachTypeSpecification(
			TypeSpecification typeSpecificationVertex,
			VariableDeclaration variableDeclarationVertex, AST beginAST,
			AST endAST) {
		IsTypeOfVariable isTypeOfVariableEdge = programGraph
				.createIsTypeOfVariable(typeSpecificationVertex,
						variableDeclarationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isTypeOfVariableEdge,
				beginAST, endAST);
	}
}
