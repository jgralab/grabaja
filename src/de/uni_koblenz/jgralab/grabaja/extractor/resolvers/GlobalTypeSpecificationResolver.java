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
package de.uni_koblenz.jgralab.grabaja.extractor.resolvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.ExtractionMode;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPartOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;

/**
 * Resolves type specifications through a global approach. Must be used after
 * parsing of files because only after this all types defined in the parsed
 * files are known to symbol table.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class GlobalTypeSpecificationResolver extends TypeSpecificationResolver {

	/**
	 * Creates an new TypeSpecificationResolver from given values.
	 * 
	 * @param symbolTable
	 *            Symbol table to be used for resolving.
	 */
	public GlobalTypeSpecificationResolver(SymbolTable symbolTable) {
		super.symbolTable = symbolTable;
	}

	/**
	 * Tries to resolve all type specifications stored in symbol table.
	 * 
	 * @param mode
	 *            Extraction mode to use.
	 * @return true if all type specifications were successfully resolved, false
	 *         otherwise.
	 */
	public boolean resolveTypeSpecifications(ExtractionMode mode) {
		if (!symbolTable.hasUnresolvedTypeSpecifications()) {
			return true;
		}
		Set<Vertex> keySet = symbolTable
				.getScopesOfUnresolvedTypeSpecifications();
		Iterator<Vertex> iterator = keySet.iterator();
		int counter = 0;
		ConsoleProgressFunction progressBar = new ConsoleProgressFunction();
		progressBar.init(symbolTable.getAmountOfUnresolvedTypeSpecifications());
		while (iterator.hasNext()) {
			Vertex scope = iterator.next();
			ArrayList<QualifiedType> qualifiedTypes = symbolTable
					.getUnresolvedTypeSpecifications(scope);
			for (int i = 0; i < qualifiedTypes.size(); i++) {
				if (resolveTypeSpecification(scope, qualifiedTypes.get(i), mode)) {
					qualifiedTypes.remove(i);
					i--;
				}
				counter++;
				if (counter % progressBar.getUpdateInterval() == 0) {
					progressBar.progress(1);
				}
			}
		}
		progressBar.finished();
		return !symbolTable.hasUnresolvedTypeSpecifications();
	}

	/**
	 * Tries to resolve a type specification represented by given vertex. If a
	 * type specification could be resolved vertex representing type
	 * specification is attached to vertex representing definition of according
	 * type.
	 * 
	 * @param scope
	 *            Vertex representing scope in which type was specified.
	 * @param qualifiedTypeVertex
	 *            Vertex representing type specification to resolve.
	 * @param mode
	 *            Extraction mode to use.
	 * @return true if type specification was resolved and it's vertex
	 *         succesfully attached, false otherwise.
	 */
	private boolean resolveTypeSpecification(Vertex scope,
			QualifiedType qualifiedTypeVertex, ExtractionMode mode) {
		// First search appropriate type definition in graph.
		String fullyQualifiedName = getSpecifiedTypeFromGraph(scope,
				qualifiedTypeVertex);
		if (fullyQualifiedName.isEmpty()
				&& ((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))) {
			// Not found in graph now search in CLASSPATH.
			fullyQualifiedName = getSpecifiedTypeFromClassPath(scope,
					qualifiedTypeVertex, mode);
		}
		if (fullyQualifiedName.isEmpty()) {
			// Still not found
			return false;
		}
		if (symbolTable.hasTypeDefinition(fullyQualifiedName)) {
			attachToType(qualifiedTypeVertex,
					symbolTable.getTypeDefinition(fullyQualifiedName));
			symbolTable.decreaseAmountOfUnresolvedTypeSpecificationsBy(1);
			return true;
		}
		return false;
	}

	/**
	 * Returns fully qualified name of type. Searches for type in graph. Type
	 * was specified by a type specification represented by given vertex.
	 * 
	 * @param scope
	 *            Vertex representing scope in which type was specified.
	 * @param qualifiedTypeVertex
	 *            Vertex representing type specification to resolve.
	 * @return Fully qualified name of type.
	 */
	private String getSpecifiedTypeFromGraph(Vertex scope,
			QualifiedType qualifiedTypeVertex) {
		String fullyQualifiedName = qualifiedTypeVertex
				.get_fullyQualifiedName();
		// Search using it's fully qualified name.
		if (symbolTable.hasTypeDefinition(fullyQualifiedName)) {
			return fullyQualifiedName;
		}
		while (symbolTable.getParentScope(scope) != null) {
			scope = symbolTable.getParentScope(scope);
		}
		Type supremeTypeVertex = (Type) scope;
		if ((supremeTypeVertex != null)
				&& (supremeTypeVertex
						.getFirstIsExternalDeclarationIn(EdgeDirection.OUT) != null)) {
			SourceUsage sourceUsageVertex = (SourceUsage) supremeTypeVertex
					.getFirstIsExternalDeclarationIn(EdgeDirection.OUT)
					.getOmega();
			// Search in package of file.
			String fullyQualifiedNameOfPackage = getPackageName(sourceUsageVertex);
			if (!fullyQualifiedNameOfPackage.isEmpty()
					&& symbolTable
							.hasTypeDefinition(fullyQualifiedNameOfPackage
									+ "." + fullyQualifiedName)) {
				return fullyQualifiedNameOfPackage + "." + fullyQualifiedName;
			}
			// Search in imports of file.
			for (IsExternalDeclarationIn edge : sourceUsageVertex
					.getIsExternalDeclarationInIncidences(EdgeDirection.IN)) {
				Vertex vertex = edge.getThat();
				if (vertex instanceof ImportDefinition) {
					ImportDefinition importDefinitionVertex = (ImportDefinition) vertex;
					String fullyQualifiedNameOfImport = ResolverUtilities
							.getFullyQualifiedName(importDefinitionVertex);
					if ((vertex instanceof ClassImportDefinition)
							&& fullyQualifiedNameOfImport
									.endsWith(fullyQualifiedName)
							&& symbolTable
									.hasTypeDefinition(fullyQualifiedNameOfImport)) {
						return fullyQualifiedNameOfImport;
					}
					if ((vertex instanceof PackageImportDefinition)
							&& symbolTable
									.hasTypeDefinition(fullyQualifiedNameOfImport
											+ "." + fullyQualifiedName)) {
						return fullyQualifiedNameOfImport + "."
								+ fullyQualifiedName;
					}
				}
			}
			//
			// Iterable< EdgeVertexPair< ? extends IsExternalDeclarationIn,?
			// extends Vertex > > isExternalDeclarationInEdges =
			// sourceUsageVertex.getIsExternalDeclarationInIncidences(
			// EdgeDirection.IN );
			// Iterator< EdgeVertexPair< ? extends IsExternalDeclarationIn,?
			// extends Vertex > > edgeIterator =
			// isExternalDeclarationInEdges.iterator();
			// while( edgeIterator.hasNext() ){
			// EdgeVertexPair pair = edgeIterator.next();
			// Vertex vertex = pair.getVertex();
			// // Check if there is an import in file in which type was
			// specified
			// if( vertex instanceof ImportDefinition ){
			// ImportDefinition importDefinitionVertex = ( ImportDefinition
			// )vertex;
			// String fullyQualifiedNameOfImport =
			// ResolverUtilities.getFullyQualifiedName( importDefinitionVertex
			// );
			// if( vertex instanceof ClassImportDefinition &&
			// fullyQualifiedNameOfImport.endsWith( fullyQualifiedName ) &&
			// symbolTable.hasTypeDefinition( fullyQualifiedNameOfImport ) )
			// return fullyQualifiedNameOfImport;
			// if( vertex instanceof PackageImportDefinition &&
			// symbolTable.hasTypeDefinition( fullyQualifiedNameOfImport + "." +
			// fullyQualifiedName ) )
			// return fullyQualifiedNameOfImport + "." + fullyQualifiedName;
			// }
			// }
		}
		// Not found in graph.
		return "";
	}

	/**
	 * Returns fully qualified name of type. Searches for type in graph. Type
	 * was specified by a type specification represented by given vertex.
	 * 
	 * @param scope
	 *            Vertex representing scope in which type was specified.
	 * @param qualifiedTypeVertex
	 *            Vertex representing type specification to resolve.
	 * @param mode
	 *            Extraction mode to use.
	 * @return Fully qualified name of type.
	 */
	private String getSpecifiedTypeFromClassPath(Vertex scope,
			QualifiedType qualifiedTypeVertex, ExtractionMode mode) {
		// Search using it's fully qualified name.
		String fullyQualifiedName = qualifiedTypeVertex
				.get_fullyQualifiedName();
		if (ResolverUtilities.createTypeUsingReflection(fullyQualifiedName,
				mode, symbolTable)) {
			return fullyQualifiedName;
		}
		// Search in package of file.
		while (symbolTable.getParentScope(scope) != null) {
			scope = symbolTable.getParentScope(scope);
		}
		Type supremeTypeVertex = (Type) scope;
		if ((supremeTypeVertex != null)
				&& (supremeTypeVertex
						.getFirstIsExternalDeclarationIn(EdgeDirection.OUT) != null)) {
			SourceUsage sourceUsageVertex = (SourceUsage) supremeTypeVertex
					.getFirstIsExternalDeclarationIn(EdgeDirection.OUT)
					.getOmega();
			// Search in package of file.
			String fullyQualifiedNameOfPackage = getPackageName(sourceUsageVertex);
			if (!fullyQualifiedNameOfPackage.isEmpty()
					&& ResolverUtilities.createTypeUsingReflection(
							fullyQualifiedNameOfPackage + "."
									+ fullyQualifiedName, mode, symbolTable)) {
				return fullyQualifiedNameOfPackage + "." + fullyQualifiedName;
			}
			// Search in imports
			for (IsExternalDeclarationIn edge : sourceUsageVertex
					.getIsExternalDeclarationInIncidences(EdgeDirection.IN)) {
				Vertex vertex = edge.getThat();
				// Check if there is an import in file in which type was
				// specified
				if (vertex instanceof ImportDefinition) {
					ImportDefinition importVertex = (ImportDefinition) vertex;
					String fullyQualifiedNameOfImport = ResolverUtilities
							.getFullyQualifiedName(importVertex);
					if ((vertex instanceof ClassImportDefinition)
							&& fullyQualifiedNameOfImport
									.endsWith(fullyQualifiedName)
							&& ResolverUtilities.createTypeUsingReflection(
									fullyQualifiedNameOfImport, mode,
									symbolTable)) {
						return fullyQualifiedNameOfImport;
					}
					if ((vertex instanceof PackageImportDefinition)
							&& ResolverUtilities.createTypeUsingReflection(
									fullyQualifiedNameOfImport + "."
											+ fullyQualifiedName, mode,
									symbolTable)) {
						return fullyQualifiedNameOfImport + "."
								+ fullyQualifiedName;
					}
				}
			}

			// Iterable< EdgeVertexPair< ? extends IsExternalDeclarationIn, ?
			// extends Vertex > > isExternalDeclarationInEdges =
			// sourceUsageVertex.getIsExternalDeclarationInIncidences(
			// EdgeDirection.IN );
			// Iterator< EdgeVertexPair< ? extends IsExternalDeclarationIn, ?
			// extends Vertex > > edgeIterator =
			// isExternalDeclarationInEdges.iterator();
			// while( edgeIterator.hasNext() ){
			// EdgeVertexPair pair = edgeIterator.next();
			// Vertex vertex = pair.getVertex();
			// // Check if there is an import in file in which type was
			// specified
			// if( vertex instanceof ImportDefinition ){
			// ImportDefinition importVertex = ( ImportDefinition )vertex;
			// String fullyQualifiedNameOfImport =
			// ResolverUtilities.getFullyQualifiedName( importVertex );
			// if( vertex instanceof ClassImportDefinition &&
			// fullyQualifiedNameOfImport.endsWith( fullyQualifiedName ) &&
			// ResolverUtilities.createTypeUsingReflection(
			// fullyQualifiedNameOfImport, mode, symbolTable ) )
			// return fullyQualifiedNameOfImport;
			// if( vertex instanceof PackageImportDefinition &&
			// ResolverUtilities.createTypeUsingReflection(
			// fullyQualifiedNameOfImport + "." + fullyQualifiedName, mode,
			// symbolTable ) )
			// return fullyQualifiedNameOfImport + "." + fullyQualifiedName;
			// }
			// }
		}
		// Last chance is java.lang
		if (ResolverUtilities.createTypeUsingReflection("java.lang."
				+ fullyQualifiedName, mode, symbolTable)) {
			return "java.lang." + fullyQualifiedName;
		}
		// Not found at all.
		return "";
	}

	/**
	 * Returns fully qualified name of package the file represented by given
	 * vertex belongs to.
	 * 
	 * @param sourceUsageVertex
	 * @return Fully qualified name of package. Empty if file does not belong to
	 *         a package.
	 */
	private String getPackageName(SourceUsage sourceUsageVertex) {
		TranslationUnit translationUnitVertex = (TranslationUnit) sourceUsageVertex
				.getFirstIsSourceUsageIn(EdgeDirection.OUT).getOmega();
		IsPartOf isPartOfEdge = translationUnitVertex
				.getFirstIsPartOf(EdgeDirection.OUT);
		if (isPartOfEdge != null) { // Check for null because file could be part
			// of no package.
			JavaPackage javaPackageVertex = (JavaPackage) isPartOfEdge
					.getOmega();
			if (javaPackageVertex != null) {
				return javaPackageVertex.get_fullyQualifiedName();
			}
		}
		return "";
	}
}
