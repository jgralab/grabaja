package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.AttributedEdge;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifiers;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;


/**
 * Provides functionality for creating field / variable and access elements in graph.
 * @author: abaldauf@uni-koblenz.de
 */
public class FieldFactory extends SubgraphFactory{

	/**
	 * Instantiates and initializes an instance.
	 * @param programGraph The graph to be used.
	 * @param symbolTable The symbol table to be used.
	 */
    public FieldFactory( Java5 pg, SymbolTable symbolTable ){
        programGraph = pg;
        this.symbolTable = symbolTable;
    }

	/**
	 * Creates a vertex for a field access.
	 * @param fieldNameAST The AST element representing the name of the accessed field.
	 * @param scopeOfFieldAccess The vertex of the field access' scope.
	 * @return The created vertex.
	 */
    public FieldAccess createFieldAccess( AST fieldNameAST, Vertex scopeOfFieldAccess ){
        FieldAccess fieldAccessVertex;
        fieldAccessVertex = programGraph.createFieldAccess();
        createIdentifier( fieldAccessVertex, fieldNameAST );
        addFieldAccess( fieldAccessVertex, scopeOfFieldAccess );
        return fieldAccessVertex;
    }

	/**
	 * Creates a vertex for a field access which has a leading expression (like method().field).
	 * @param fieldContainerVertex The expression in front of the field access in the cascade.
	 * @param fieldNameAST The AST element representing the name of the accessed field.
	 * @param fieldContainerBeginAST The AST element representing the first element of the leading expression.
	 * @param fieldContainerEndAST The AST element representing the last element of the leading side expression.
	 * @param scopeOfFieldAccess The vertex of the field access' scope.
	 * @return The created vertex.
	 */
    public FieldAccess createFieldAccess( Expression fieldContainerVertex, AST fieldNameAST, AST fieldContainerBeginAST, AST fieldContainerEndAST, Vertex scopeOfFieldAccess ){
        FieldAccess fieldAccessVertex;
        fieldAccessVertex = programGraph.createFieldAccess();
        createIdentifier( fieldAccessVertex, fieldNameAST );
        IsFieldContainerOf isFieldContainerOfEdge = programGraph.createIsFieldContainerOf( fieldContainerVertex, fieldAccessVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isFieldContainerOfEdge, fieldContainerBeginAST, fieldContainerEndAST );
        addFieldAccess( fieldAccessVertex, scopeOfFieldAccess );
        return fieldAccessVertex;
    }

	/**
	 * Creates an identifier vertex and attaches it to a field access.
	 * @param fieldAccessVertex The field access.
	 * @param ast The AST element representing the name of the accessed field.
	 */
    private void createIdentifier( FieldAccess fieldAccessVertex, AST ast ){
        Identifier identifierVertex = programGraph.createIdentifier();
        identifierVertex.setName( ast.getText() );
        IsFieldNameOf isFieldNameOfEdge = programGraph.createIsFieldNameOf( identifierVertex, fieldAccessVertex );
        Utilities.fillEdgeAttributesFromAST( isFieldNameOfEdge, ast );
    }

	/**
	 * Adds a variable for resolving. Decides if this is a local variable or a global variable (field).
	 * @param declaredVariable The variable declaration.
	 * @param scopeOfVariable The vertex of the variable's scope.
	 * @param fullyQualifiedNameOfContainingClass The fully qualified name of the type in which the variable is declared.
	 */
	public void addVariableDeclaration( VariableDeclaration declaredVariable, Vertex scopeOfVariable, String fullyQualifiedNameOfContainingClass ){
		if (
		      ( scopeOfVariable == null )
		   || ( ( scopeOfVariable instanceof ClassDefinition ) &&  ( symbolTable.getParentScope( scopeOfVariable ) == null ) )
		   || ( ( scopeOfVariable instanceof InterfaceDefinition ) && ( symbolTable.getParentScope( scopeOfVariable ) == null ) )
		   || (
			     ( scopeOfVariable instanceof Block )
		         && (
				       ( symbolTable.getParentScope( scopeOfVariable ) == null )
				    || ( ( symbolTable.getParentScope( scopeOfVariable ) instanceof ClassDefinition ) &&  ( symbolTable.getParentScope( symbolTable.getParentScope( scopeOfVariable ) ) == null ) )
				    || ( ( symbolTable.getParentScope( scopeOfVariable ) instanceof InterfaceDefinition ) && ( symbolTable.getParentScope( symbolTable.getParentScope( scopeOfVariable ) ) == null ) )
				    )
			  )
		   ){
			// this is a global variable
			addGlobalVariableDeclaration( declaredVariable, fullyQualifiedNameOfContainingClass );
		}
		else{
			// this is a local variable
			symbolTable.addLocalFieldDeclaration( declaredVariable, scopeOfVariable );
		}
	}

	/**
	 * Adds a parameter for resolving.
	 * @param declaredParameter The parameter declaration.
	 * @param scopeOfParameter The vertex of the parameter's scope.
	 */
	public void addParameterDeclaration( ParameterDeclaration declaredParameter, Vertex scopeOfParameter ){
		// scope of null should not occur, but for stability we don't want our scope / var HashMap wrecked.
		if (scopeOfParameter != null){
			symbolTable.addLocalFieldDeclaration( declaredParameter, scopeOfParameter );
		}
	}

	/**
	 * Adds a global variable (field) for resolving. Also checks for local accesses to the variable (global variable may be used before it's declaration).
	 * @param declaredVariable The variable declaration.
	 * @param fullyQualifiedNameOfContainingClass The fully qualified name of the type in which the variable is declared.
	 */
	private void addGlobalVariableDeclaration( VariableDeclaration declaredVariable, String fullyQualifiedNameOfContainingClass ){
		// Determine if this is a private, public or protected variable
		boolean isPublic = false;
		boolean isPrivate = false;
		for( IsModifierOfVariable edge: declaredVariable.getIsModifierOfVariableIncidences(EdgeDirection.IN) ){
			Modifier currentModifier = (Modifier)edge.getAlpha();
			if (currentModifier.getType() == Modifiers.PUBLIC)
				isPublic = true;
			if (currentModifier.getType() == Modifiers.PRIVATE)
				isPrivate = true;
		}
		symbolTable.addGlobalVariableDeclaration( ((Identifier)declaredVariable.getFirstIsVariableNameOf(EdgeDirection.IN).getAlpha()).getName(), declaredVariable );
		checkYetUndeclaredVariables( declaredVariable );
		if ( ( isPublic ) || ( !isPrivate ) ){
			symbolTable.addVariableDeclaration( fullyQualifiedNameOfContainingClass, declaredVariable );
		}
		// Nothing more to do for private variables.
	}

	/**
	 * Adds a field access for resolving. Also resolves it if it is a local variable / parameter.
	 * @param fieldAccessVertex The field access.
	 * @param scopeOfFieldAccess The vertex of the field access' scope.
	 */
	private void addFieldAccess( FieldAccess fieldAccessVertex, Vertex scopeOfFieldAccess ){
		if ( fieldAccessVertex != null ){
			FieldDeclaration declarationOfField = null;
			Identifier identifierVertexOfAccess = ( Identifier )fieldAccessVertex.getFirstIsFieldNameOf( EdgeDirection.IN ).getAlpha();
			if(identifierVertexOfAccess != null ){
				if( fieldAccessVertex.getFirstIsFieldContainerOf(EdgeDirection.IN) == null ){
					// this definitely has to be a local or global (here or inherited) variable.
					declarationOfField = symbolTable.getFieldDeclarationFromScope( identifierVertexOfAccess.getName(), scopeOfFieldAccess );
					if( declarationOfField == null )
						declarationOfField = symbolTable.getGlobalVariableDeclaration( identifierVertexOfAccess.getName() );
					if( declarationOfField == null )
						declarationOfField = symbolTable.getEnumConstantFromCurrent( identifierVertexOfAccess.getName() );
					if( declarationOfField == null ){
						symbolTable.addAccessOfUndeclaredInternalVariable( fieldAccessVertex );
						symbolTable.addScopeOfFieldAccess( fieldAccessVertex, scopeOfFieldAccess );
					}
				}
				else{
					symbolTable.addFieldAccess( fieldAccessVertex );
					symbolTable.addScopeOfFieldAccess( fieldAccessVertex, scopeOfFieldAccess );
				}
				linkFieldAccessToDeclaration( fieldAccessVertex, identifierVertexOfAccess, declarationOfField );
			}
		}
	}

	/**
	 * Creates a semantic edge between a field access and the field's declaration. Used only for local resolving.
	 * @param fieldAccessVertex The field access.
	 * @param identifierVertexOfAccess The identifier of the field access.
	 * @param fieldDeclarationVertex The vertex of the field's declaration.
	 */
	private void linkFieldAccessToDeclaration( FieldAccess fieldAccessVertex, Identifier identifierVertexOfAccess, FieldDeclaration fieldDeclarationVertex ){
		if ( ( fieldDeclarationVertex != null ) && ( fieldAccessVertex != null ) && ( identifierVertexOfAccess != null ) ){
		//	IsDeclarationOfAccessedField linkingEdge = programGraph.createIsDeclarationOfAccessedField( fieldDeclarationVertex, fieldAccessVertex );
			Identifier identifierVertexOfDeclaration;
			if ( fieldDeclarationVertex instanceof VariableDeclaration ){
				identifierVertexOfDeclaration = (Identifier)((VariableDeclaration)fieldDeclarationVertex).getFirstIsVariableNameOf(EdgeDirection.IN).getAlpha();
			}
			else if( fieldDeclarationVertex instanceof ParameterDeclaration ){
				identifierVertexOfDeclaration = (Identifier)((ParameterDeclaration)fieldDeclarationVertex).getFirstIsParameterNameOf(EdgeDirection.IN).getAlpha();
			}
			else{
				identifierVertexOfDeclaration = (Identifier)((EnumConstant)fieldDeclarationVertex).getFirstIsEnumConstantNameOf(EdgeDirection.IN).getAlpha();
			}
			AttributedEdge edgeToReattach = identifierVertexOfAccess.getFirstIsFieldNameOf(EdgeDirection.OUT);
			if ( (identifierVertexOfDeclaration != null) && (edgeToReattach != null) ){
				edgeToReattach.setAlpha( identifierVertexOfDeclaration );
				identifierVertexOfAccess.delete();
			}
		}
	}

	/**
	 * Checks for unresolved local accesses to a variable (global variable may be used before it's declaration) and tries to resolves them.
	 * @param declaredVariable The vertex of the variable's declaration.
	 */
	private void checkYetUndeclaredVariables( VariableDeclaration declaredVariable ){
		if ( declaredVariable != null ){
			String declaredVariableName = ((Identifier)declaredVariable.getFirstIsVariableNameOf(EdgeDirection.IN).getAlpha()).getName();
			for ( int counter=0; counter < symbolTable.getAmountOfAccessesOfUndeclaredInternalVariables(); counter++ ){
				FieldAccess fieldAccessVertex = symbolTable.getAccessOfUndeclaredInternalVariable( counter );
				Identifier identifierVertexOfAccess = (Identifier)fieldAccessVertex.getFirstIsFieldNameOf(EdgeDirection.IN).getAlpha();
				if ( identifierVertexOfAccess.getName().equals(declaredVariableName) ){
					linkFieldAccessToDeclaration( fieldAccessVertex, identifierVertexOfAccess, declaredVariable );
					symbolTable.removeAccessOfUndeclaredInternalVariable( counter );
					counter--;
				}
			}
		}
	}

}