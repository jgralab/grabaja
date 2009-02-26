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
import de.uni_koblenz.jgralab.impl.ProgressFunctionImpl;

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
	 * @return true if all type specifications were succesfully resolved, false
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
		ProgressFunctionImpl progressBar = new ProgressFunctionImpl(60);
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
				&& (mode == ExtractionMode.EAGER || mode == ExtractionMode.COMPLETE)) {
			// Not found in graph now search in CLASSPATH.
			fullyQualifiedName = getSpecifiedTypeFromClassPath(scope,
					qualifiedTypeVertex, mode);
		}
		if (fullyQualifiedName.isEmpty()) {
			// Still not found
			return false;
		}
		if (symbolTable.hasTypeDefinition(fullyQualifiedName)) {
			attachToType(qualifiedTypeVertex, symbolTable
					.getTypeDefinition(fullyQualifiedName));
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
		String fullyQualifiedName = qualifiedTypeVertex.getFullyQualifiedName();
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
					if (vertex instanceof ClassImportDefinition
							&& fullyQualifiedNameOfImport
									.endsWith(fullyQualifiedName)
							&& symbolTable
									.hasTypeDefinition(fullyQualifiedNameOfImport)) {
						return fullyQualifiedNameOfImport;
					}
					if (vertex instanceof PackageImportDefinition
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
		String fullyQualifiedName = qualifiedTypeVertex.getFullyQualifiedName();
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
					if (vertex instanceof ClassImportDefinition
							&& fullyQualifiedNameOfImport
									.endsWith(fullyQualifiedName)
							&& ResolverUtilities.createTypeUsingReflection(
									fullyQualifiedNameOfImport, mode,
									symbolTable)) {
						return fullyQualifiedNameOfImport;
					}
					if (vertex instanceof PackageImportDefinition
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
				return javaPackageVertex.getFullyQualifiedName();
			}
		}
		return "";
	}
}