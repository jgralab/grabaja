package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLowerBoundOfWildcardArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSimpleArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfSimpleArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterDeclarationNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsUpperBoundOfTypeParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsUpperBoundOfWildcardArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsWildcardArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.SimpleArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.WildcardArgument;

/**
 * Provides functionality for creating type parameter and type argument elements in graph.
 * @author: ultbreit@uni-koblenz.de
 */
public class TypeParameterFactory extends SubgraphFactory{

	/**
	 * Instantiates and initializes an instance.
	 * @param programGraph The graph to be used.
	 */
    public TypeParameterFactory( SymbolTable symbolTable ){
		this.symbolTable = symbolTable;
        programGraph = symbolTable.getGraph();
    }

    /**
     * Attaches a <code>TypeParameterDeclaration</code> to a <code>ClassDefinition</code>,
     * <code>InterfaceDefinition</code>, <code>ConstructorDefinition</code> or <code>MethodDeclaration</code>.
     * @param typeParameterDeclarationVertex
     * @param parentVertex
     * @param beginAST
     * @param endAST
     */
    public void attachTypeParameter( TypeParameterDeclaration typeParameterDeclarationVertex, Vertex parentVertex, AST beginAST, AST endAST ){
        if( parentVertex instanceof ClassDefinition ){
            IsTypeParameterOfClass isTypeParameterOfClassEdge = programGraph.createIsTypeParameterOfClass( typeParameterDeclarationVertex, ( ClassDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isTypeParameterOfClassEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof InterfaceDefinition ){
            IsTypeParameterOfInterface isTypeParameterOfInterfaceEdge = programGraph.createIsTypeParameterOfInterface( typeParameterDeclarationVertex, ( InterfaceDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isTypeParameterOfInterfaceEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof ConstructorDefinition ){
            IsTypeParameterOfConstructor isTypeParameterOfConstructorEdge = programGraph.createIsTypeParameterOfConstructor( typeParameterDeclarationVertex, ( ConstructorDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isTypeParameterOfConstructorEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof MethodDeclaration ){
            IsTypeParameterOfMethod isTypeParameterOfMethodEdge = programGraph.createIsTypeParameterOfMethod( typeParameterDeclarationVertex, ( MethodDeclaration )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isTypeParameterOfMethodEdge, beginAST, endAST );
        }
    }

	/**
	 * Creates a vertex for a type parameter.
	 * @param ast The AST element representing type parameter's identifier.
	 * @param scope The vertex of the type parameter's scope.
	 * @return The created vertex.
	 */
    public TypeParameterDeclaration createTypeParameterDeclaration( AST ast, Vertex scope ){
	    //creation of Identifier same as in identifierFactory
        TypeParameterDeclaration typeParameterDeclarationVertex = programGraph.createTypeParameterDeclaration();
        typeParameterDeclarationVertex.setName( ast.getText() );
        typeParameterDeclarationVertex.setFullyQualifiedName( ast.getText() );
        typeParameterDeclarationVertex.setExternal( false );
        symbolTable.addTypeParameter( scope, ast.getText(), typeParameterDeclarationVertex );
        Identifier identifierVertex = programGraph.createIdentifier();
        identifierVertex.setName( ast.getText() );
        IsTypeParameterDeclarationNameOf isTypeParameterDeclarationNameOfEdge = programGraph.createIsTypeParameterDeclarationNameOf( identifierVertex, typeParameterDeclarationVertex );
        Utilities.fillEdgeAttributesFromAST( isTypeParameterDeclarationNameOfEdge, ast );
        return typeParameterDeclarationVertex;
    }

	/**
	 * Creates an edge between an upper bound type specification and it's type parameter.
	 * @param typeSpecificationVertex The type specification.
	 * @param typeParameterDeclarationVertex The type parameter.
	 * @param beginAST The AST element representing the first element of the type specification.
	 * @param endAST The AST element representing the last element of the type specification.
	 */
    public void attachUpperBound( TypeSpecification typeSpecificationVertex, TypeParameterDeclaration typeParameterDeclarationVertex, AST beginAST, AST endAST ){
        IsUpperBoundOfTypeParameter isUpperBoundOfTypeParameterEdge = programGraph.createIsUpperBoundOfTypeParameter( typeSpecificationVertex, typeParameterDeclarationVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isUpperBoundOfTypeParameterEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a simple argument type specification and it's type argument. Also creates the required simple argument vertex
	 * @param typeArgumentVertex The type argument.
	 * @param typeSpecificationVertex The type specification.
	 * @param beginAST The AST element representing the first element of the type specification.
	 * @param endAST The AST element representing the last element of the type specification.
	 */
    public void createSimpleArgument( TypeArgument typeArgumentVertex, TypeSpecification typeSpecificationVertex, AST beginAST, AST endAST ){
        SimpleArgument simpleArgumentVertex = programGraph.createSimpleArgument();
        IsSimpleArgumentOf isSimpleArgumentOfEdge = programGraph.createIsSimpleArgumentOf( simpleArgumentVertex, typeArgumentVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isSimpleArgumentOfEdge, beginAST, endAST );
        IsTypeOfSimpleArgument isTypeOfSimpleArgumentEdge = programGraph.createIsTypeOfSimpleArgument( typeSpecificationVertex, simpleArgumentVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isTypeOfSimpleArgumentEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a wildcard argument and it's type argument.
	 * @param wildcardArgumentVertex The wildcard argument.
	 * @param typeArgumentVertex The type argument.
	 * @param beginAST The AST element representing the first element of the wildcard argument.
	 * @param endAST The AST element representing the last element of the wildcard argument.
	 */
    public void attachWildCardArgument( WildcardArgument wildcardArgumentVertex, TypeArgument typeArgumentVertex, AST beginAST, AST endAST ){
        IsWildcardArgumentOf isWildcardArgumentOfEdge = programGraph.createIsWildcardArgumentOf( wildcardArgumentVertex, typeArgumentVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isWildcardArgumentOfEdge, beginAST, beginAST );
    }

	/**
	 * Creates an edge between an upper bound type specification and it's wildcard argument.
	 * @param wildcardArgumentVertex The wildcard argument.
	 * @param typeSpecificationVertex The type specification.
	 * @param beginAST The AST element representing the first element of the type specification.
	 * @param endAST The AST element representing the last element of the type specification.
	 */
    public void attachUpperBound( WildcardArgument wildcardArgumentVertex, TypeSpecification typeSpecificationVertex, AST beginAST, AST endAST ){
        IsUpperBoundOfWildcardArgument isUpperBoundOfWildcardArgumentEdge = programGraph.createIsUpperBoundOfWildcardArgument( typeSpecificationVertex, wildcardArgumentVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isUpperBoundOfWildcardArgumentEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between an lower bound type specification and it's wildcard argument.
	 * @param wildcardArgumentVertex The wildcard argument.
	 * @param typeSpecificationVertex The type specification.
	 * @param beginAST The AST element representing the first element of the type specification.
	 * @param endAST The AST element representing the last element of the type specification.
	 */
    public void attachLowerBound( WildcardArgument wildcardArgumentVertex, TypeSpecification typeSpecificationVertex, AST beginAST, AST endAST ){
        IsLowerBoundOfWildcardArgument isLowerBoundOfWildcardArgumentEdge = programGraph.createIsLowerBoundOfWildcardArgument( typeSpecificationVertex, wildcardArgumentVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isLowerBoundOfWildcardArgumentEdge, beginAST, endAST );
    }

}