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

import antlr.collections.AST;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.extractor.adapters.CommonASTAdapter;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayInitializer;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConditionalExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixOperators;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArgumentOfMethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArrayElementIndexOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCastedBuiltInTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCastedObjectOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCastedTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCastedValueOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsConditionOfExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsConstructorInvocationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsContentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDimensionInitializerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElementTypeOfCreatedArray;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInitializerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInitializerOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLeftHandSideOfInfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLeftHandSideOfPostfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMatchOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMismatchOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsNameOfInvokedMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsRightHandSideOfInfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsRightHandSideOfPrefixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSizeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfObject;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocationTypes;
import de.uni_koblenz.jgralab.grabaja.java5schema.Null;
import de.uni_koblenz.jgralab.grabaja.java5schema.ObjectCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.PostfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.PostfixOperators;
import de.uni_koblenz.jgralab.grabaja.java5schema.PrefixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.PrefixOperators;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableInitializer;

/**
 * Provides functionality for creating expression elements in graph.
 * 
 * @author: abaldauf@uni-koblenz.de
 */
public class ExpressionFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 * @param symbolTable
	 *            The symbol table to be used.
	 */
	public ExpressionFactory(Java5 pg, SymbolTable symbolTable) {
		programGraph = pg;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates a vertex for a "null" expression.
	 * 
	 * @return The created vertex.
	 */
	public Null createNull() {
		if (!symbolTable.hasNull()) {
			symbolTable.setNull(programGraph.createNull());
		}
		return symbolTable.getNull();
	}

	/**
	 * Creates a vertex for an infix expression.
	 * 
	 * @param operator
	 *            The operator of the expression.
	 * @return The created vertex.
	 */
	public InfixExpression createInfixExpression(InfixOperators operator) {
		InfixExpression infixExpressionVertex = programGraph
				.createInfixExpression();
		infixExpressionVertex.set_operator(operator);
		return infixExpressionVertex;
	}

	/**
	 * Creates a vertex for an infix expression.
	 * 
	 * @param operator
	 *            The operator of the expression.
	 * @param expressionVertexOnLeftSide
	 *            The expression on the left-hand side of operator.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            left-hand side expression.
	 * @param endAST
	 *            The AST element representing the last element of the left-hand
	 *            side expression.
	 * @return The created vertex.
	 */
	public InfixExpression createInfixExpression(InfixOperators operator,
			Expression expressionVertexOnLeftSide, AST beginAST, AST endAST) {
		InfixExpression infixExpressionVertex = createInfixExpression(operator);
		IsLeftHandSideOfInfixExpression isLeftHandSideOfInfixExpressionEdge = programGraph
				.createIsLeftHandSideOfInfixExpression(
						expressionVertexOnLeftSide, infixExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isLeftHandSideOfInfixExpressionEdge, beginAST, endAST);
		return infixExpressionVertex;
	}

	/**
	 * Creates an edge between a left-hand side field access and an infix
	 * expression.
	 * 
	 * @param infixExpressionVertex
	 *            The infix expression.
	 * @param fieldAccessVertex
	 *            The field access on the left-hand side of operator.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            left-hand side field access.
	 * @param endAST
	 *            The AST element representing the last element of the left-hand
	 *            side field access.
	 */
	public void attachLeftHandSide(InfixExpression infixExpressionVertex,
			FieldAccess fieldAccessVertex, AST beginAST, AST endAST) {
		IsLeftHandSideOfInfixExpression isLeftHandSideOfInfixExpressionEdge = programGraph
				.createIsLeftHandSideOfInfixExpression(fieldAccessVertex,
						infixExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isLeftHandSideOfInfixExpressionEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between a right-hand side expression and an infix
	 * expression.
	 * 
	 * @param infixExpressionVertex
	 *            The infix expression.
	 * @param expressionVertexOnRightSide
	 *            The expression on the right-hand side of operator.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            right-hand side expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            right-hand side expression.
	 */
	public void attachRightHandSide(InfixExpression infixExpressionVertex,
			Expression expressionVertexOnRightSide, AST beginAST, AST endAST) {
		IsRightHandSideOfInfixExpression isRightHandSideOfInfixExpressionEdge = programGraph
				.createIsRightHandSideOfInfixExpression(
						expressionVertexOnRightSide, infixExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isRightHandSideOfInfixExpressionEdge, beginAST, endAST);
	}

	/**
	 * Creates a vertex for a postfix expression.
	 * 
	 * @param operator
	 *            The operator of the expression.
	 * @param expressionVertexOnLeftSide
	 *            The expression on the left-hand side of operator.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            left-hand side expression.
	 * @param endAST
	 *            The AST element representing the last element of the left-hand
	 *            side expression.
	 * @return The created vertex.
	 */
	public PostfixExpression createPostfixExpression(PostfixOperators operator,
			Expression expressionVertexOnLeftSide, AST beginAST, AST endAST) {
		PostfixExpression postfixExpressionVertex = programGraph
				.createPostfixExpression();
		postfixExpressionVertex.set_operator(operator);
		IsLeftHandSideOfPostfixExpression isLeftHandSideOfPostfixExpressionEdge = programGraph
				.createIsLeftHandSideOfPostfixExpression(
						expressionVertexOnLeftSide, postfixExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isLeftHandSideOfPostfixExpressionEdge, beginAST, endAST);
		return postfixExpressionVertex;
	}

	/**
	 * Creates a vertex for a prefix expression.
	 * 
	 * @param operator
	 *            The operator of the expression.
	 * @param expressionVertexOnRightSide
	 *            The expression on the right-hand side of operator.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            right-hand side expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            right-hand side expression.
	 * @return The created vertex.
	 */
	public PrefixExpression createPrefixExpression(PrefixOperators operator,
			Expression expressionVertexOnRightSide, AST beginAST, AST endAST) {
		PrefixExpression prefixExpressionVertex = programGraph
				.createPrefixExpression();
		prefixExpressionVertex.set_operator(operator);
		IsRightHandSideOfPrefixExpression isRightHandSideOfPrefixExpressionEdge = programGraph
				.createIsRightHandSideOfPrefixExpression(
						expressionVertexOnRightSide, prefixExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isRightHandSideOfPrefixExpressionEdge, beginAST, endAST);
		return prefixExpressionVertex;
	}

	/**
	 * Creates a vertex for an array creation.
	 * 
	 * @param arrayInitializerVertex
	 *            The array initializer.
	 * @param beginAST
	 *            The AST element representing the first element of the array
	 *            initializer.
	 * @param endAST
	 *            The AST element representing the last element of the array
	 *            initializer.
	 * @return The created vertex.
	 */
	public ArrayCreation createArrayCreation(
			ArrayInitializer arrayInitializerVertex, AST beginAST, AST endAST) {
		ArrayCreation arrayCreationVertex = programGraph.createArrayCreation();
		IsDimensionInitializerOf isDimensionInitializerOfEdge = programGraph
				.createIsDimensionInitializerOf(arrayInitializerVertex,
						arrayCreationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isDimensionInitializerOfEdge, beginAST, endAST);
		return arrayCreationVertex;
	}

	/**
	 * Creates a vertex for an object creation (class instantiation).
	 * 
	 * @param ast
	 *            The AST element representing the last element of fully
	 *            qualified name of the object's type.
	 * @param methodInvocationVertex
	 *            The constructor invocation.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            constructor invocation.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            constructor invocation.
	 * @return The created vertex.
	 */
	public ObjectCreation createObjectCreation(AST ast,
			MethodInvocation methodInvocationVertex, AST beginAST, AST endAST) {
		ObjectCreation objectCreationVertex = programGraph
				.createObjectCreation();
		if (((CommonASTAdapter) ast).getText().equals("super")) {
			methodInvocationVertex
					.set_type(MethodInvocationTypes.SUPERCONSTRUCTOR);
		} else if (((CommonASTAdapter) ast).getText().equals("this")) {
			methodInvocationVertex
					.set_type(MethodInvocationTypes.EXPLICITCONSTRUCTOR);
		} else {
			methodInvocationVertex.set_type(MethodInvocationTypes.CONSTRUCTOR);
		}
		IsConstructorInvocationOf isConstructorInvocationOfEdge = programGraph
				.createIsConstructorInvocationOf(methodInvocationVertex,
						objectCreationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isConstructorInvocationOfEdge, beginAST, endAST);
		return objectCreationVertex;
	}

	/**
	 * Creates an edge between a type specification and an object creation
	 * (class instantiation).
	 * 
	 * @param objectCreationVertex
	 *            The object creation.
	 * @param objectType
	 *            The type specification.
	 * @param beginAST
	 *            The AST element representing the first element of the type
	 *            specification.
	 * @param endAST
	 *            The AST element representing the last element of the type
	 *            specification.
	 */
	public void attachObjectCreationType(ObjectCreation objectCreationVertex,
			TypeSpecification objectType, AST beginAST, AST endAST) {
		if (objectType != null) {
			IsTypeOfObject isTypeOfObjectEdge = programGraph
					.createIsTypeOfObject(objectType, objectCreationVertex);
			Utilities.fillEdgeAttributesFromASTDifference(isTypeOfObjectEdge,
					beginAST, endAST);
		}
	}

	/**
	 * Creates an edge between a variable's initializer expression and it's
	 * declaration.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param variableDeclarationVertex
	 *            The variable declaration.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachExpression(Expression expressionVertex,
			VariableDeclaration variableDeclarationVertex, AST beginAST,
			AST endAST) {
		IsInitializerOfVariable isInitializerOfVariableEdge = programGraph
				.createIsInitializerOfVariable(expressionVertex,
						variableDeclarationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isInitializerOfVariableEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between an array's dimension initializer expression and
	 * it's creation. Also creates the according array initializer vertex.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param arrayCreationVertex
	 *            The array creation.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void createArrayInitializer(Expression expressionVertex,
			ArrayCreation arrayCreationVertex, AST beginAST, AST endAST) {
		ArrayInitializer arrayInitializerVertex = programGraph
				.createArrayInitializer();
		IsSizeOf isSizeOfEdge = programGraph.createIsSizeOf(expressionVertex,
				arrayInitializerVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isSizeOfEdge, beginAST,
				endAST);
		IsDimensionInitializerOf isDimensionInitializerOfEdge = programGraph
				.createIsDimensionInitializerOf(arrayInitializerVertex,
						arrayCreationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isDimensionInitializerOfEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between a variable's initializer expression and it's
	 * initializer vertex.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param variableInitializerVertex
	 *            The variable's initializer vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachVariableInitializer(Expression expressionVertex,
			VariableInitializer variableInitializerVertex, AST beginAST,
			AST endAST) {
		IsInitializerOf isInitializerOfEdge = programGraph
				.createIsInitializerOf(expressionVertex,
						variableInitializerVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isInitializerOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates a vertex for an builtin type cast.
	 * 
	 * @param builtInTypeVertex
	 *            The specification of the builtin type.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            specification.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            specification.
	 * @return The created vertex.
	 */
	public BuiltInCast createBuiltInCast(BuiltInType builtInTypeVertex,
			AST beginAST, AST endAST) {
		BuiltInCast builtInCastVertex = programGraph.createBuiltInCast();
		IsCastedBuiltInTypeOf isCastedBuiltInTypeOfEdge = programGraph
				.createIsCastedBuiltInTypeOf(builtInTypeVertex,
						builtInCastVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isCastedBuiltInTypeOfEdge, beginAST, endAST);
		return builtInCastVertex;
	}

	/**
	 * Creates an edge between a casted expression it's builtin type cast
	 * vertex.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param builtInCastVertex
	 *            The builtin type cast vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachCastedValue(Expression expressionVertex,
			BuiltInCast builtInCastVertex, AST beginAST, AST endAST) {
		IsCastedValueOf isCastedValueOfEdge = programGraph
				.createIsCastedValueOf(expressionVertex, builtInCastVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isCastedValueOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates a vertex for a non-builtin type cast (type or array cast).
	 * 
	 * @param typeSpecificationVertex
	 *            The type specification.
	 * @param beginAST
	 *            The AST element representing the first element of the type
	 *            specification.
	 * @param endAST
	 *            The AST element representing the last element of the type
	 *            specification.
	 * @return The created vertex.
	 */
	public ClassCast createClassCast(TypeSpecification typeSpecificationVertex,
			AST beginAST, AST endAST) {
		ClassCast classCastVertex = programGraph.createClassCast();
		IsCastedTypeOf isCastedTypeOfEdge = programGraph.createIsCastedTypeOf(
				typeSpecificationVertex, classCastVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isCastedTypeOfEdge,
				beginAST, endAST);
		return classCastVertex;
	}

	/**
	 * Creates an edge between a casted expression it's type cast vertex.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param classCastVertex
	 *            The type cast vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachCastedObject(Expression expressionVertex,
			ClassCast classCastVertex, AST beginAST, AST endAST) {
		IsCastedObjectOf isCastedObjectOfEdge = programGraph
				.createIsCastedObjectOf(expressionVertex, classCastVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isCastedObjectOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates a vertex for an invocation (method or constructor alike).
	 * 
	 * @param invocationType
	 *            The type of the invocation.
	 * @return The created vertex.
	 */
	public MethodInvocation createMethodInvocation(
			MethodInvocationTypes invocationType) {
		MethodInvocation methodInvocationVertex = programGraph
				.createMethodInvocation();
		methodInvocationVertex.set_type(invocationType);
		return methodInvocationVertex;
	}

	/**
	 * Creates an edge between an expression that is an element index and a
	 * field access (obviously accessing an array).
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param fieldAccessVertex
	 *            The field access vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachArrayElementIndex(Expression expressionVertex,
			FieldAccess fieldAccessVertex, AST beginAST, AST endAST) {
		IsArrayElementIndexOf isArrayElementIndexOfEdge = programGraph
				.createIsArrayElementIndexOf(expressionVertex,
						fieldAccessVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isArrayElementIndexOfEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between the identifier of an invocation and the
	 * invocation.
	 * 
	 * @param identifierVertex
	 *            The identifier.
	 * @param methodInvocationVertex
	 *            The invocation vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            identifier.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            identifier.
	 */
	public void attachNameOfInvokedMethod(Identifier identifierVertex,
			MethodInvocation methodInvocationVertex, AST beginAST, AST endAST) {
		IsNameOfInvokedMethod isNameOfInvokedMethodEdge = programGraph
				.createIsNameOfInvokedMethod(identifierVertex,
						methodInvocationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isNameOfInvokedMethodEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between the specification of an array's element type and
	 * it's creation.
	 * 
	 * @param typeSpecificationVertex
	 *            The specification of the element type.
	 * @param arrayCreationVertex
	 *            The array creation vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            specification.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            specification.
	 */
	public void attachElementType(TypeSpecification typeSpecificationVertex,
			ArrayCreation arrayCreationVertex, AST beginAST, AST endAST) {
		IsElementTypeOfCreatedArray isElementTypeOfCreatedArrayEdge = programGraph
				.createIsElementTypeOfCreatedArray(typeSpecificationVertex,
						arrayCreationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isElementTypeOfCreatedArrayEdge, beginAST, endAST);
	}

	/**
	 * Creates a vertex for a conditional expression.
	 * 
	 * @param expressionVertex
	 *            The expression which is the condition of the conditional
	 *            expression.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            condition expression.
	 * @param endAST
	 *            The AST element representing the last element of the condition
	 *            expression.
	 * @return The created vertex.
	 */
	public ConditionalExpression createConditionalExpression(
			Expression expressionVertex, AST beginAST, AST endAST) {
		ConditionalExpression conditionalExpressionVertex = programGraph
				.createConditionalExpression();
		IsConditionOfExpression isConditionOfExpressionEdge = programGraph
				.createIsConditionOfExpression(expressionVertex,
						conditionalExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isConditionOfExpressionEdge, beginAST, endAST);
		return conditionalExpressionVertex;
	}

	/**
	 * Creates an edge between the match case expression and the according
	 * conditional expression.
	 * 
	 * @param expressionVertex
	 *            The expression which is the match of the conditional
	 *            expression.
	 * @param conditionalExpressionVertex
	 *            The conditional expression vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the match
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the match
	 *            expression.
	 */
	public void attachMatch(Expression expressionVertex,
			ConditionalExpression conditionalExpressionVertex, AST beginAST,
			AST endAST) {
		IsMatchOf isMatchOfEdge = programGraph.createIsMatchOf(
				expressionVertex, conditionalExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isMatchOfEdge, beginAST,
				endAST);
	}

	/**
	 * Creates an edge between the mismatch case expression and the according
	 * conditional expression.
	 * 
	 * @param expressionVertex
	 *            The expression which is the mismatch of the conditional
	 *            expression.
	 * @param conditionalExpressionVertex
	 *            The conditional expression vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the mismatch
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the mismatch
	 *            expression.
	 */
	public void attachMismatch(Expression expressionVertex,
			ConditionalExpression conditionalExpressionVertex, AST beginAST,
			AST endAST) {
		IsMismatchOf isMismatchOfEdge = programGraph.createIsMismatchOf(
				expressionVertex, conditionalExpressionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isMismatchOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between an expression which is argument for an invocation
	 * and the according invocation.
	 * 
	 * @param expressionVertex
	 *            The argument expression .
	 * @param methodInvocationVertex
	 *            The invocation vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachArgumentOfMethodInvocation(Expression expressionVertex,
			MethodInvocation methodInvocationVertex, AST beginAST, AST endAST) {
		IsArgumentOfMethodInvocation isArgumentOfMethodInvocationEdge = programGraph
				.createIsArgumentOfMethodInvocation(expressionVertex,
						methodInvocationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isArgumentOfMethodInvocationEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between an expression which is content of an array
	 * initializer and the according initializer.
	 * 
	 * @param expressionVertex
	 *            The expression .
	 * @param arrayInitializerVertex
	 *            The array initializer vertex.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachContent(Expression expressionVertex,
			ArrayInitializer arrayInitializerVertex, AST beginAST, AST endAST) {
		IsContentOf isContentOfEdge = programGraph.createIsContentOf(
				expressionVertex, arrayInitializerVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isContentOfEdge,
				beginAST, endAST);
	}

}
