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
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.Field;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArgumentOfEnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfStaticConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfStaticInitializer;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsEnumConstantBlockOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldCreationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMemberOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Member;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.Statement;
import de.uni_koblenz.jgralab.grabaja.java5schema.StaticConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.StaticInitializerDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;

/**
 * Provides functionality for creating member elements in graph.
 * 
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class MemberFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 */
	public MemberFactory(Java5 pg) {
		programGraph = pg;
	}

	/**
	 * Creates a vertex for a static constructor. Also creates edges between it
	 * and the block containing it, as well as between the constructors block
	 * and itself.
	 * 
	 * @param childBlockVertex
	 *            The constructor's block.
	 * @param parentBlockVertex
	 *            The block containing the constructor.
	 * @param beginAST
	 *            The AST element representing the first element of the static
	 *            constructor.
	 * @param endAST
	 *            The AST element representing the last element of the static
	 *            constructor.
	 */
	public void createStaticConstructor(Block childBlockVertex,
			Block parentBlockVertex, AST beginAST, AST endAST) {
		StaticConstructorDefinition staticConstructorVertex = programGraph
				.createStaticConstructorDefinition();
		IsBodyOfStaticConstructor isBodyOfStaticConstructorEdge = programGraph
				.createIsBodyOfStaticConstructor(childBlockVertex,
						staticConstructorVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isBodyOfStaticConstructorEdge, beginAST, endAST);
		attachMember(staticConstructorVertex, parentBlockVertex, beginAST,
				endAST);
	}

	/**
	 * Creates a vertex for a static initializer. Also creates edges between it
	 * and the block containing it, as well as between the initializers block
	 * and itself.
	 * 
	 * @param childBlockVertex
	 *            The initializer's block.
	 * @param parentBlockVertex
	 *            The block containing the initializer.
	 * @param beginAST
	 *            The AST element representing the first element of the static
	 *            initializer.
	 * @param endAST
	 *            The AST element representing the last element of the static
	 *            initializer.
	 */
	public void createStaticInitializer(Block childBlockVertex,
			Block parentBlockVertex, AST beginAST, AST endAST) {
		StaticInitializerDefinition staticInitializerVertex = programGraph
				.createStaticInitializerDefinition();
		IsBodyOfStaticInitializer isBodyOfStaticInitializerEdge = programGraph
				.createIsBodyOfStaticInitializer(childBlockVertex,
						staticInitializerVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isBodyOfStaticInitializerEdge, beginAST, endAST);
		attachMember(staticInitializerVertex, parentBlockVertex, beginAST,
				endAST);
	}

	/**
	 * Creates an edge between a member and the block containing it.
	 * 
	 * @param memberVertex
	 *            The member's block.
	 * @param blockVertex
	 *            The block containing the member.
	 * @param beginAST
	 *            The AST element representing the first element of the member.
	 * @param endAST
	 *            The AST element representing the last element of the member.
	 */
	public void attachMember(Member memberVertex, Block blockVertex,
			AST beginAST, AST endAST) {
		IsMemberOf isMemberOfEdge = programGraph.createIsMemberOf(memberVertex,
				blockVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isMemberOfEdge, beginAST,
				endAST);
	}

	/**
	 * Creates a vertex for a field.
	 * 
	 * @param blockVertex
	 *            The block containing the member.
	 * @param ast
	 *            The AST element representing the field's identifier.
	 * @return The created vertex.
	 */
	public Field createField(Block blockVertex, AST ast) {
		Field fieldVertex = programGraph.createField();
		IsMemberOf isMemberOfEdge = programGraph.createIsMemberOf(fieldVertex,
				blockVertex);
		Utilities.fillEdgeAttributesFromAST(isMemberOfEdge, ast); // We only
																	// want the
																	// position
																	// informations
																	// of the
																	// field's
																	// *name*
																	// here.
		return fieldVertex;
	}

	/**
	 * Creates an edge between a constructor's block and the constructor
	 * definition.
	 * 
	 * @param blockVertexOfConstructor
	 *            The constructor's block.
	 * @param constructorDefinitionVertex
	 *            The constructor.
	 * @param beginAST
	 *            The AST element representing the first element of the block.
	 * @param endAST
	 *            The AST element representing the last element of the block.
	 */
	public void attachBlock(Block blockVertexOfConstructor,
			ConstructorDefinition constructorDefinitionVertex, AST beginAST,
			AST endAST) {
		IsBodyOfConstructor isBodyOfConstructorEdge = programGraph
				.createIsBodyOfConstructor(blockVertexOfConstructor,
						constructorDefinitionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isBodyOfConstructorEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a method's block and the method definition.
	 * 
	 * @param blockVertexOfMethod
	 *            The method's block.
	 * @param methodDefinitionVertex
	 *            The method.
	 * @param beginAST
	 *            The AST element representing the first element of the block.
	 * @param endAST
	 *            The AST element representing the last element of the block.
	 */
	public void attachBlock(Block blockVertexOfMethod,
			MethodDefinition methodDefinitionVertex, AST beginAST, AST endAST) {
		IsBodyOfMethod isBodyOfMethodEdge = programGraph.createIsBodyOfMethod(
				blockVertexOfMethod, methodDefinitionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isBodyOfMethodEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between an enum constant's block and the enum constant.
	 * 
	 * @param blockVertex
	 *            The enum constant's block.
	 * @param enumConstantVertex
	 *            The enum constant.
	 * @param beginAST
	 *            The AST element representing the first element of the block.
	 * @param endAST
	 *            The AST element representing the last element of the block.
	 */
	public void attachBlock(Block blockVertex, EnumConstant enumConstantVertex,
			AST beginAST, AST endAST) {
		IsEnumConstantBlockOf isEnumConstantBlockOfEdge = programGraph
				.createIsEnumConstantBlockOf(blockVertex, enumConstantVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isEnumConstantBlockOfEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between an enum constant's argument expression and the
	 * enum constant.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param enumConstantVertex
	 *            The enum constant.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachArgumentOfEnumConstant(Expression expressionVertex,
			EnumConstant enumConstantVertex, AST beginAST, AST endAST) {
		IsArgumentOfEnumConstant isArgumentOfEnumConstantEdge = programGraph
				.createIsArgumentOfEnumConstant(expressionVertex,
						enumConstantVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isArgumentOfEnumConstantEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between a parameter declaration and the method /
	 * constructor it belongs to.
	 * 
	 * @param parameterDeclarationVertex
	 *            The parameter declaration.
	 * @param parentVertex
	 *            The method / constructor.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            parameter declaration.
	 * @param endAST
	 *            The AST element representing the last element of the parameter
	 *            declaration.
	 */
	public void attachParameterDeclaration(
			ParameterDeclaration parameterDeclarationVertex,
			Vertex parentVertex, AST beginAST, AST endAST) {
		if (parentVertex instanceof MethodDeclaration)
			attachParameterDeclaration(parameterDeclarationVertex,
					(MethodDeclaration) parentVertex, beginAST, endAST);
		else if (parentVertex instanceof ConstructorDefinition)
			attachParameterDeclaration(parameterDeclarationVertex,
					(ConstructorDefinition) parentVertex, beginAST, endAST);
	}

	/**
	 * Creates an edge between a parameter declaration and the method it belongs
	 * to.
	 * 
	 * @param parameterDeclarationVertex
	 *            The parameter declaration.
	 * @param methodDeclarationVertex
	 *            The method.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            parameter declaration.
	 * @param endAST
	 *            The AST element representing the last element of the parameter
	 *            declaration.
	 */
	public void attachParameterDeclaration(
			ParameterDeclaration parameterDeclarationVertex,
			MethodDeclaration methodDeclarationVertex, AST beginAST, AST endAST) {
		IsParameterOfMethod isParameterOfMethodEdge = programGraph
				.createIsParameterOfMethod(parameterDeclarationVertex,
						methodDeclarationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isParameterOfMethodEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a parameter declaration and the constructor it
	 * belongs to.
	 * 
	 * @param parameterDeclarationVertex
	 *            The parameter declaration.
	 * @param constructorDefinitionVertex
	 *            The constructor.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            parameter declaration.
	 * @param endAST
	 *            The AST element representing the last element of the parameter
	 *            declaration.
	 */
	public void attachParameterDeclaration(
			ParameterDeclaration parameterDeclarationVertex,
			ConstructorDefinition constructorDefinitionVertex, AST beginAST,
			AST endAST) {
		IsParameterOfConstructor isParameterOfConstructorEdge = programGraph
				.createIsParameterOfConstructor(parameterDeclarationVertex,
						constructorDefinitionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isParameterOfConstructorEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between a thrown expception's specification and the
	 * method / constructor it belongs to.
	 * 
	 * @param typeSpecificationVertex
	 *            The expception's specification.
	 * @param parentVertex
	 *            The method / constructor.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expception's specification.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expception's specification.
	 */
	public void attachExceptionThrown(
			TypeSpecification typeSpecificationVertex, Vertex parentVertex,
			AST beginAST, AST endAST) {
		if (parentVertex instanceof MethodDeclaration) {
			IsExceptionThrownByMethod isExceptionThrownByMethodEdge = programGraph
					.createIsExceptionThrownByMethod(typeSpecificationVertex,
							(MethodDeclaration) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isExceptionThrownByMethodEdge, beginAST, endAST);
		} else if (parentVertex instanceof ConstructorDefinition) {
			IsExceptionThrownByConstructor isExceptionThrownByConstructorEdge = programGraph
					.createIsExceptionThrownByConstructor(
							typeSpecificationVertex,
							(ConstructorDefinition) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isExceptionThrownByConstructorEdge, beginAST, endAST);
		}
	}

	/**
	 * Creates an edge between a field creation and the field itself.
	 * 
	 * @param statementVertex
	 *            The field creation (should be a variable declaration).
	 * @param fieldVertex
	 *            The field.
	 * @param beginAST
	 *            The AST element representing the first element of the field
	 *            creation.
	 * @param endAST
	 *            The AST element representing the last element of the field
	 *            creation.
	 * @return The created edge.
	 */
	public IsFieldCreationOf attachFieldCreation(Statement statementVertex,
			Field fieldVertex, AST beginAST, AST endAST) {
		IsFieldCreationOf isFieldCreationOfEdge = programGraph
				.createIsFieldCreationOf(statementVertex, fieldVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isFieldCreationOfEdge,
				beginAST, endAST);
		return isFieldCreationOfEdge;
	}

}
