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

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;

/**
 * Provides functionality for creating qualified name elements in graph.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class QualifiedNameFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 * @param symbolTable
	 *            The symbol table to be used.
	 */
	public QualifiedNameFactory(Java5 pg, SymbolTable symbolTable) {
		programGraph = pg;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates vertex representing a qualified name or retrieves an existing one
	 * from symboltable.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of vertex to create/retrieve.
	 * @return Vertex representing qualified name.
	 */
	public QualifiedName createQualifiedName(String fullyQualifiedName) {
		QualifiedName qualifiedNameVertex = null;
		if (symbolTable.hasQualifiedName(fullyQualifiedName)) {
			qualifiedNameVertex = symbolTable
					.getQualifiedName(fullyQualifiedName);
		} else {
			qualifiedNameVertex = programGraph.createQualifiedName();
			qualifiedNameVertex.set_fullyQualifiedName(fullyQualifiedName);
			symbolTable.addQualifiedName(fullyQualifiedName,
					qualifiedNameVertex);
		}
		return qualifiedNameVertex;
	}

	/*
	 * public QualifiedName createAndAttachQualifiedType( QualifiedName
	 * qualifiedNameParentVertex, AST ast, AST beginAST, AST endAST ){
	 * QualifiedName qualifiedNameVertex = createQualifiedType( null,
	 * qualifiedNameParentVertex.getFullyQualifiedName() + "." + ast.getText()
	 * ); IsQualifiedNameOf isQualifiedNameOfEdge =
	 * programGraph.createIsQualifiedNameOf( qualifiedNameParentVertex,
	 * qualifiedNameVertex ); Utilities.fillEdgeAttributesFromASTDifference(
	 * isQualifiedNameOfEdge, beginAST, endAST ); return qualifiedNameVertex; }
	 */

	public QualifiedType createQualifiedType(Vertex scope,
			String fullyQualifiedName) {
		QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
		qualifiedTypeVertex.set_fullyQualifiedName(fullyQualifiedName);
		symbolTable.addUnresolvedTypeSpecification(scope, qualifiedTypeVertex);
		return qualifiedTypeVertex;
	}
	/*
	 * private Identifier getAttachedIdentifier( Type typeVertex ){ if(
	 * typeVertex instanceof ClassDefinition ) return ( Identifier )( (
	 * ClassDefinition )typeVertex ).getFirstIsClassNameOf().getAlpha(); if(
	 * typeVertex instanceof InterfaceDefinition ) return ( Identifier )( (
	 * InterfaceDefinition )typeVertex ).getFirstIsInterfaceNameOf().getAlpha();
	 * if( typeVertex instanceof EnumDefinition ) return ( Identifier )( (
	 * EnumDefinition )typeVertex ).getFirstIsEnumNameOf().getAlpha(); if(
	 * typeVertex instanceof AnnotationDefinition ) return ( Identifier )( (
	 * AnnotationDefinition )typeVertex
	 * ).getFirstIsAnnotationDefinitionNameOf().getAlpha(); // next step should
	 * never be the case - only added so compiler does not complain @TODO throw
	 * an exception return null; }
	 */
	/*
	 * // [QualifiedType] public QualifiedName createQualifiedName( String
	 * fullyQualifiedName, AST ast ){ QualifiedName qualifiedNameVertex =
	 * programGraph.createQualifiedName();
	 * qualifiedNameVertex.setFullyQualifiedName( fullyQualifiedName ); return
	 * qualifiedNameVertex; }
	 */
	/*
	 * public QualifiedName createAndAttachQualifiedName( QualifiedName
	 * qualifiedNameParentVertex, AST ast, AST beginAST, AST endAST ){
	 * QualifiedName qualifiedNameVertex = createQualifiedName(
	 * qualifiedNameParentVertex.getFullyQualifiedName() + "." + ast.getText(),
	 * ast ); IsQualifiedNameOf isQualifiedNameOfEdge =
	 * programGraph.createIsQualifiedNameOf( qualifiedNameParentVertex,
	 * qualifiedNameVertex ); Utilities.fillEdgeAttributesFromASTDifference(
	 * isQualifiedNameOfEdge, beginAST, endAST ); return qualifiedNameVertex; }
	 */
	/**
	 * Replaces a QualifiedName in graph by a QualifiedType.
	 * 
	 * @param qualifiedNameVertex
	 *            QualifiedName vertex to replace.
	 * @param scope
	 *            Scope of vertex to replace (to add to symbol table).
	 */
	/*
	 * public QualifiedType replaceByQualifiedType( QualifiedName
	 * qualifiedNameVertex, Vertex scope ){ if( qualifiedNameVertex instanceof
	 * QualifiedType ) return ( QualifiedType )qualifiedNameVertex;
	 * QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
	 * qualifiedTypeVertex.setFullyQualifiedName(
	 * qualifiedNameVertex.getFullyQualifiedName() ); IsQualifiedNameOf
	 * isQualifiedNameOfEdge = qualifiedNameVertex.getFirstIsQualifiedNameOf();
	 * if( isQualifiedNameOfEdge != null ) isQualifiedNameOfEdge.setOmega(
	 * qualifiedTypeVertex ); Vector< Edge > edges = new Vector< Edge >(); for(
	 * EdgeVertexPair<? extends IsTypeArgumentOfTypeSpecification, ? extends
	 * Vertex> pair :
	 * qualifiedNameVertex.getIsTypeArgumentOfTypeSpecificationIncidences(
	 * EdgeDirection.IN, true ) ) edges.add( pair.getEdge() ); for( int i = 0; i
	 * < edges.size(); i++ ) edges.get( i ).setOmega( qualifiedTypeVertex );
	 * symbolTable.addUnresolvedTypeSpecification( scope, qualifiedTypeVertex );
	 * qualifiedNameVertex.delete(); return qualifiedTypeVertex; }
	 */
}
