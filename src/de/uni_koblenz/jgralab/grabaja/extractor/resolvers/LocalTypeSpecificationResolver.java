package de.uni_koblenz.jgralab.grabaja.extractor.resolvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeDefinitionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterUsage;

/**
 * Resolves type specifications using a local approach. It tries to resolve them
 * to types which are defined in same file. Must be used after each parsing of a
 * file.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class LocalTypeSpecificationResolver extends TypeSpecificationResolver {

	/**
	 * Creates new<code>LocalResolver</code> from given values.
	 */
	public LocalTypeSpecificationResolver(SymbolTable symbolTable) {
		super.symbolTable = symbolTable;
	}

	/**
	 * Indicates if the current vertex representing a type specification can be
	 * deleted after being successfully resolved.
	 */
	private boolean deleteCurrentQualifiedTypeVertex = false;

	/**
	 * Tries to resolve all type specifications in a file to locally defined
	 * types. If a type specification could be resolved vertex representing type
	 * specification is attached to vertex representing definition of according
	 * type. If a type could not be resolved, it is marked in symbol table for a
	 * (later) global approach.
	 * 
	 * @return true if all vertices were successfully attached, false otherwise.
	 */
	public boolean resolveTypeSpecifications() {
		if (!symbolTable.hasUnresolvedTypeSpecificationsInCurrentlyParsedFile()) {
			return false;
		}
		Set<Vertex> keySet = symbolTable
				.getScopesOfUnresolvedTypeSpecificationsInCurrentlyParsedFile();
		Iterator<Vertex> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			Vertex scope = iterator.next();
			ArrayList<QualifiedType> qualifiedTypes = symbolTable
					.getUnresolvedTypeSpecificationsInCurrentlyParsedFile(scope);
			for (int j = 0; j < qualifiedTypes.size(); j++) {
				QualifiedType qualifiedTypeVertex = qualifiedTypes.get(j);
				if (resolveTypeSpecification(scope, qualifiedTypeVertex)) {
					qualifiedTypes.remove(j);
					j--;
					symbolTable
							.decreaseAmountOfUnresolvedTypeSpecificationsBy(1);
					if (deleteCurrentQualifiedTypeVertex) {
						qualifiedTypeVertex.delete();
						deleteCurrentQualifiedTypeVertex = false;
					}
					// else symbolTable.addResolvedTypeSpecification(
					// qualifiedTypeVertex );
				}
			}
		}
		return !symbolTable
				.hasUnresolvedTypeSpecificationsInCurrentlyParsedFile();
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
	 * @return true if type specification was resolved and it's vertex
	 *         succesfully attached, false otherwise.
	 */
	public boolean resolveTypeSpecification(Vertex scope,
			QualifiedType qualifiedTypeVertex) {
		String fullyQualifiedName = qualifiedTypeVertex
				.get_fullyQualifiedName();
		// First compare to type parameter declarations in same and superior
		// scopes of type specification.
		if (symbolTable.hasTypeParameters()) {
			while (scope != null) {
				if (symbolTable.hasTypeParameter(scope, fullyQualifiedName)) {
					String prefix = "";
					if (scope instanceof Type) {
						Type typeVertex = (Type) scope;
						prefix = typeVertex.get_fullyQualifiedName() + ".";
					}
					// check if there is a nested type in same scope with same
					// name as type parameter
					if (symbolTable.hasNestedType(prefix + fullyQualifiedName)) {
						// yes: so it hides type parameter
						attachToType(qualifiedTypeVertex, symbolTable
								.getNestedType(prefix + fullyQualifiedName));
					}
					// no: replace QualifiedType by TypeParameterUsage in graph
					else {
						replaceByTypeParameterUsage(scope, qualifiedTypeVertex);
					}
					return true;
				}
				scope = symbolTable.getParentScope(scope);
			}
		}
		// Then compare to the first type definition in same file the type was
		// specified.
		if (specifiesSupremeTypeInFile(fullyQualifiedName)) {
			attachToType(qualifiedTypeVertex, symbolTable
					.getSupremeTypeInFile());
			return true;
		}
		// At last compare to nested types in same file the type was specified.
		fullyQualifiedName = getSpecifiedNestedType(scope, fullyQualifiedName);
		if (!fullyQualifiedName.isEmpty()) {
			attachToType(qualifiedTypeVertex, symbolTable
					.getNestedType(fullyQualifiedName));
			return true;
		}
		return false;
	}

	/**
	 * Replaces a vertex representing a type specification by a vertex
	 * representing a type parameter usage.
	 * 
	 * @param scope
	 *            Vertex representing scope in which type was specified.
	 * @param qualifiedTypeVertex
	 *            Vertex to replace.
	 */
	private void replaceByTypeParameterUsage(Vertex scope,
			QualifiedType qualifiedTypeVertex) {
		TypeParameterDeclaration typeParameterDeclarationVertex = symbolTable
				.getTypeParameter(scope, qualifiedTypeVertex
						.get_fullyQualifiedName());
		// Determine if a TypeParameterUsage for this file already exists
		TypeParameterUsage typeParameterUsageVertex = null;
		IsTypeDefinitionOf isTypeDefinitionOfEdge = typeParameterDeclarationVertex
				.getFirstIsTypeDefinitionOf();
		if (isTypeDefinitionOfEdge != null) {
			// yes: do not create a new one and use existing one instead
			typeParameterUsageVertex = (TypeParameterUsage) isTypeDefinitionOfEdge
					.getOmega();
		} else {
			// no: create a new one
			Java5 programGraph = symbolTable.getGraph();
			typeParameterUsageVertex = programGraph.createTypeParameterUsage();
			isTypeDefinitionOfEdge = programGraph.createIsTypeDefinitionOf(
					typeParameterDeclarationVertex, typeParameterUsageVertex); // edge
			// has
			// no
			// attributes
		}
		replaceBy(qualifiedTypeVertex, typeParameterUsageVertex); // Get all
		// edges
		// attached
		// to vertex
		// QualifiedType
		// and
		// attach
		// them to
		// vertex
		// TypeParameterUsage
		deleteCurrentQualifiedTypeVertex = true;
	}

	/**
	 * Determines if a type specification specifies first type definition in
	 * same file.
	 * 
	 * @param typeSpecification
	 *            Specified type.
	 * @return true if it specifies first type definition, otherwise false.
	 */
	private boolean specifiesSupremeTypeInFile(String typeSpecification) {
		// Compare to simple name of type.
		String type = symbolTable.getSupremeTypeInFile().get_name();
		if (type.equals(typeSpecification)) {
			return true;
		}
		// Compare to fully qualified name of type.
		type = symbolTable.getSupremeTypeInFile().get_fullyQualifiedName();
		return type.equals(typeSpecification);
	}

	/**
	 * Returns fully qualified name of nested type specified by given string.
	 * 
	 * @param scope
	 *            Vertex representing scope in which type was specified.
	 * @param typeSpecification
	 *            Specified type.
	 * @return Fully qualified name of a nested type, if none can be found an
	 *         empty string.
	 */
	private String getSpecifiedNestedType(Vertex scope, String typeSpecification) {
		if (!symbolTable.hasNestedTypes()) {
			return "";
		}
		// Compare to fully qualified name of nested class.
		if (symbolTable.hasNestedType(typeSpecification)) {
			return typeSpecification;
		}
		// Failure! It could be that the type specification was not fully
		// qualified, so build fully qualified name.
		if (scope != null) { // @TODO check for scope == null in resolve(), so
			// it will not try to resolve imports with
			// nested types, typeparameters etc.
			while ((symbolTable.getParentScope(scope) != null)
					&& !(scope instanceof Type)) {
				scope = symbolTable.getParentScope(scope);
			}
			Type typeVertex = (Type) scope;
			String fullyQualifiedName = typeVertex.get_fullyQualifiedName()
					+ "." + typeSpecification;
			if (symbolTable.hasNestedType(fullyQualifiedName)) {
				return fullyQualifiedName;
			}
			// Again failure! Now check for type specification which was more
			// than simple, but not really fully qualified.
			int indexOfDot = typeSpecification.indexOf(".");
			if ((indexOfDot >= 1)
					&& typeVertex.get_name().equals(
							typeSpecification.substring(0, indexOfDot))) {
				String simpleTypeSpecification = typeSpecification.substring(
						indexOfDot + 1, typeSpecification.length());
				fullyQualifiedName = typeVertex.get_fullyQualifiedName() + "."
						+ simpleTypeSpecification;
				if (symbolTable.hasNestedType(fullyQualifiedName)) {
					return fullyQualifiedName;
				}
			}
		}
		return "";
	}
}