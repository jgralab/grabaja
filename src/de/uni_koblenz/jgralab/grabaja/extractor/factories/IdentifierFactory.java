package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationDefinitionNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationFieldNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsClassNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsEnumConstantNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsEnumNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMethodContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsNameOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsNameOfInvokedMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsNameOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterDeclarationNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsVariableNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;

/**
 * Provides functionality for creating identifier elements in graph.
 * 
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class IdentifierFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param pg
	 *            The graph to be used.
	 */
	public IdentifierFactory(Java5 pg) {
		programGraph = pg;
	}

	/**
	 * Creates a vertex for an identifier.
	 * 
	 * @param ast
	 *            The AST element representing the identifier.
	 * @return The created vertex.
	 */
	public Identifier createIdentifier(AST ast) {
		Identifier identifierVertex = programGraph.createIdentifier();
		identifierVertex.set_name(ast.getText());
		return identifierVertex;
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a class definition.
	 * 
	 * @param classDefinitionVertex
	 *            The class definition.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(ClassDefinition classDefinitionVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsClassNameOf isClassNameOfEdge = programGraph.createIsClassNameOf(
				identifierVertex, classDefinitionVertex);
		Utilities.fillEdgeAttributesFromAST(isClassNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an annotation
	 * definition.
	 * 
	 * @param annotationDefinitionVertex
	 *            The annotation definition.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(
			AnnotationDefinition annotationDefinitionVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsAnnotationDefinitionNameOf isAnnotationDefinitionNameOfEdge = programGraph
				.createIsAnnotationDefinitionNameOf(identifierVertex,
						annotationDefinitionVertex);
		Utilities.fillEdgeAttributesFromAST(isAnnotationDefinitionNameOfEdge,
				ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an interface
	 * definition.
	 * 
	 * @param interfaceDefinitionVertex
	 *            The interface definition.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(InterfaceDefinition interfaceDefinitionVertex,
			AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsInterfaceNameOf isInterfaceNameOfEdge = programGraph
				.createIsInterfaceNameOf(identifierVertex,
						interfaceDefinitionVertex);
		Utilities.fillEdgeAttributesFromAST(isInterfaceNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an enum definition.
	 * 
	 * @param enumDefinitionVertex
	 *            The enum definition.
	 * @param ast
	 *            AST element representing identifier.
	 */
	public void createIdentifier(EnumDefinition enumDefinitionVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsEnumNameOf isEnumNameOfEdge = programGraph.createIsEnumNameOf(
				identifierVertex, enumDefinitionVertex);
		Utilities.fillEdgeAttributesFromAST(isEnumNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a type parameter
	 * declaration.
	 * 
	 * @param typeParameterDeclarationVertex
	 *            The type parameter declaration.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(
			TypeParameterDeclaration typeParameterDeclarationVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsTypeParameterDeclarationNameOf isTypeParameterDeclarationNameOfEdge = programGraph
				.createIsTypeParameterDeclarationNameOf(identifierVertex,
						typeParameterDeclarationVertex);
		Utilities.fillEdgeAttributesFromAST(
				isTypeParameterDeclarationNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a field access.
	 * 
	 * @param fieldAccessVertex
	 *            The field access.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(FieldAccess fieldAccessVertex, AST ast) {
		// @TODO check if field has already been declared, so no new identifier
		// is needed.
		Identifier identifierVertex = createIdentifier(ast);
		IsFieldNameOf isFieldNameOfEdge = programGraph.createIsFieldNameOf(
				identifierVertex, fieldAccessVertex);
		Utilities.fillEdgeAttributesFromAST(isFieldNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a field access.
	 * Also creates the edge to the leading expression for a field access inside
	 * a cascade.
	 * 
	 * @param fieldAccessVertex
	 *            The field access.
	 * @param ast
	 *            The AST element representing the identifier.
	 * @param beginAST
	 *            The AST element representing the first element of the leading
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the leading
	 *            side expression.
	 */
	public void createIdentifier(FieldAccess fieldAccessVertex,
			Expression expressionVertex, AST ast, AST beginAST, AST endAST) {
		createIdentifier(fieldAccessVertex, ast);
		IsFieldContainerOf isFieldContainerOfEdge = programGraph
				.createIsFieldContainerOf(expressionVertex, fieldAccessVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isFieldContainerOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a parameter
	 * declaration.
	 * 
	 * @param parameterDeclarationVertex
	 *            The parameter declaration.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(
			ParameterDeclaration parameterDeclarationVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsParameterNameOf isParameterNameOfEdge = programGraph
				.createIsParameterNameOf(identifierVertex,
						parameterDeclarationVertex);
		Utilities.fillEdgeAttributesFromAST(isParameterNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an annotation
	 * field.
	 * 
	 * @param annotationFieldVertex
	 *            The annotation field.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(AnnotationField annotationFieldVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsAnnotationFieldNameOf isAnnotationFieldNameOfEdge = programGraph
				.createIsAnnotationFieldNameOf(identifierVertex,
						annotationFieldVertex);
		Utilities.fillEdgeAttributesFromAST(isAnnotationFieldNameOfEdge, ast);
		// return identifierVertex;
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an enum constant.
	 * 
	 * @param enumConstantVertex
	 *            The enum constant.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(EnumConstant enumConstantVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsEnumConstantNameOf isEnumConstantNameOfEdge = programGraph
				.createIsEnumConstantNameOf(identifierVertex,
						enumConstantVertex);
		Utilities.fillEdgeAttributesFromAST(isEnumConstantNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a constructor
	 * definition or a method declaration / definition.
	 * 
	 * @param parentVertex
	 *            The constructor definition or method declaration / definition.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(Vertex parentVertex, AST ast) {
		if (parentVertex instanceof ConstructorDefinition) {
			createIdentifier((ConstructorDefinition) parentVertex, ast);
		} else if (parentVertex instanceof MethodDeclaration) {
			createIdentifier((MethodDeclaration) parentVertex, ast);
		}
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a constructor
	 * definition.
	 * 
	 * @param constructorDefinitionVertex
	 *            The constructor definition.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(
			ConstructorDefinition constructorDefinitionVertex, AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsNameOfConstructor isNameOfConstructorEdge = programGraph
				.createIsNameOfConstructor(identifierVertex,
						constructorDefinitionVertex);
		Utilities.fillEdgeAttributesFromAST(isNameOfConstructorEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to a method
	 * declaration / definition.
	 * 
	 * @param methodDeclarationVertex
	 *            The method declaration / definition.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(MethodDeclaration methodDeclarationVertex,	AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsNameOfMethod isNameOfMethodEdge = programGraph.createIsNameOfMethod(identifierVertex, methodDeclarationVertex);
		Utilities.fillEdgeAttributesFromAST(isNameOfMethodEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an invocation.
	 * 
	 * @param methodInvocationVertex
	 *            The invocation.
	 * @param ast
	 *            The AST element representing the identifier.
	 */
	public void createIdentifier(MethodInvocation methodInvocationVertex,
			AST ast) {
		Identifier identifierVertex = createIdentifier(ast);
		IsNameOfInvokedMethod isNameOfInvokedMethodEdge = programGraph
				.createIsNameOfInvokedMethod(identifierVertex,
						methodInvocationVertex);
		Utilities.fillEdgeAttributesFromAST(isNameOfInvokedMethodEdge, ast);
	}

	/**
	 * Creates a vertex for an identifier and attaches it to an invocation. Also
	 * creates the edge to the leading expression for an invocation inside a
	 * cascade.
	 * 
	 * @param methodInvocationVertex
	 *            The invocation.
	 * @param expressionVertex
	 *            The leading expression.
	 * @param ast
	 *            AST element representing identifier.
	 * @param beginAST
	 *            AST element representing first element of leading expression.
	 * @param endAST
	 *            AST element representing last element of leading side
	 *            expression.
	 */
	public void createIdentifier(MethodInvocation methodInvocationVertex,
			Expression expressionVertex, AST ast, AST beginAST, AST endAST) {
		createIdentifier(methodInvocationVertex, ast);
		IsMethodContainerOf isMethodContainerOfEdge = programGraph
				.createIsMethodContainerOf(expressionVertex,
						methodInvocationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isMethodContainerOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between an identifier vertex and a variable declaration.
	 * 
	 * @param identifierVertex
	 *            The identifier.
	 * @param variableDeclarationVertex
	 *            The variable declaration.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            identifier.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            identifier.
	 */
	public void attachIdentifier(Identifier identifierVertex,
			VariableDeclaration variableDeclarationVertex, AST beginAST,
			AST endAST) {
		IsVariableNameOf isVariableNameOfEdge = programGraph
				.createIsVariableNameOf(identifierVertex,
						variableDeclarationVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isVariableNameOfEdge,
				beginAST, endAST);
	}
}