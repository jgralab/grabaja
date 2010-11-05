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
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsImportedTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;

/**
 * Provides functionality for creating import elements in graph.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class ImportFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 * @param symbolTable
	 *            The symbol table to be used.
	 */
	public ImportFactory(Java5 pg, SymbolTable symbolTable) {
		programGraph = pg;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates a vertex for an import definition.
	 * 
	 * @param qualifiedNameVertex
	 *            The qualified name of the import.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            qualified name.
	 * @param endAST
	 *            The AST element representing the last element of the qualified
	 *            name.
	 * @return The created vertex.
	 */
	public ImportDefinition createImportDefinition(
			QualifiedName qualifiedNameVertex, AST beginAST, AST endAST) {
		ImportDefinition importDefinitionVertex;
		String fullyQualifiedName = qualifiedNameVertex
				.get_fullyQualifiedName();
		if (fullyQualifiedName.endsWith("*")) {
			PackageImportDefinition packageImportDefinitionVertex = programGraph
					.createPackageImportDefinition();
			// packageImportVertex.setFullyQualifiedName(
			// fullyQualifiedName.substring( 0, fullyQualifiedName.length() - 2
			// ) ); //cuts of ".*"
			importDefinitionVertex = packageImportDefinitionVertex;
		} else {
			ClassImportDefinition classImportDefinitionVertex = programGraph
					.createClassImportDefinition();
			// classImportVertex.setFullyQualifiedName( fullyQualifiedName );
			importDefinitionVertex = classImportDefinitionVertex;
		}
		IsImportedTypeOf isImportedTypeOfEdge = programGraph
				.createIsImportedTypeOf(qualifiedNameVertex,
						importDefinitionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isImportedTypeOfEdge,
				beginAST, endAST);
		return importDefinitionVertex;
	}

	/**
	 * Creates a vertex for a package import definition.
	 * 
	 * @return The created vertex.
	 */
	public PackageImportDefinition createPackageImportDefinition() {
		return programGraph.createPackageImportDefinition();
	}

	/**
	 * Creates a vertex for a type import definition.
	 * 
	 * @return The created vertex.
	 */
	public ClassImportDefinition createClassImportDefinition() {
		return programGraph.createClassImportDefinition();
	}

	/**
	 * Creates an edge between a qualified name and an import definition.
	 * 
	 * @param qualifiedNameVertex
	 *            The qualified name of the import.
	 * @param importDefinitionVertex
	 *            The import definition.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            qualified name.
	 * @param endAST
	 *            The AST element representing the last element of the qualified
	 *            name.
	 */
	public void attach(QualifiedName qualifiedNameVertex,
			ImportDefinition importDefinitionVertex, AST beginAST, AST endAST) {
		IsImportedTypeOf isImportedTypeOfEdge = programGraph
				.createIsImportedTypeOf(qualifiedNameVertex,
						importDefinitionVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isImportedTypeOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between an import definition and its source usage.
	 * 
	 * @param importDefinitionVertex
	 *            The import definition.
	 * @param sourceUsageVertex
	 *            The source usage.
	 * @param beginAST
	 *            The AST element representing the first element of the import
	 *            definition.
	 * @param endAST
	 *            The AST element representing the last element of the import
	 *            definition.
	 */
	public void attach(ImportDefinition importDefinitionVertex,
			SourceUsage sourceUsageVertex, AST beginAST, AST endAST) {
		IsExternalDeclarationIn isExternalDeclarationInEdge = programGraph
				.createIsExternalDeclarationIn(importDefinitionVertex,
						sourceUsageVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isExternalDeclarationInEdge, beginAST, endAST);
	}

}
