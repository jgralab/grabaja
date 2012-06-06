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

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.ExtractionMode;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.ImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDeclarationOfAccessedField;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaVertex;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ObjectCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;

/**
 * Resolves fields accesses to fields, variables and enum constants which could
 * not be resolved locally.
 *
 * @author: abaldauf@uni-koblenz.de
 */
public class FieldResolver extends Resolver {

	/**
	 * A reference to the instance of the method resolver used during global
	 * resolving.
	 */
	private MethodResolver methodResolver = null;

	/**
	 * The progress bar to be shown during field access resolving (but not if a
	 * field resolving is triggered during one of the other resolvers
	 * execution).
	 */
	private ConsoleProgressFunction fieldProgressBar = null;

	/**
	 * Instantiates and initializes an instance.
	 *
	 * @param symbolTable
	 *            The symbol table to be used for resolving.
	 */
	public FieldResolver(SymbolTable symbolTable) {
		super.symbolTable = symbolTable;
	}

	/**
	 * Sets the reference to the method resolver instance.
	 *
	 * @param resolver
	 *            The instance of the method resolver.
	 */
	public void setMethodResolver(MethodResolver resolver) {
		this.methodResolver = resolver;
	}

	/**
	 * Resolves all the field accesses stored in the symbol table.
	 *
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if all of the accessed fields could be resolved, false if at
	 *         least one could not be resolved.
	 */
	public boolean resolveFields(ExtractionMode mode) {
		boolean result = true;
		if ((symbolTable != null)
				&& (symbolTable.getFieldAccessVertices() != null)) {
			fieldProgressBar = new ConsoleProgressFunction();
			fieldProgressBar.init(symbolTable.amountOfFieldAccesses());
			Iterator<FieldAccess> fieldAccessIterator = symbolTable
					.getFieldAccessVertices().iterator();
			while (fieldAccessIterator.hasNext()) {
				FieldAccess currentFieldAccess = fieldAccessIterator.next();
				if (!resolveSingleField(mode, currentFieldAccess)) {
					result = false;
				}
			}
			fieldProgressBar.finished();
			fieldProgressBar = null;
		}
		return result;
	}

	/**
	 * Resolves a singe field accesses.
	 *
	 * @param mode
	 *            The extraction mode to use.
	 * @param fieldAccess
	 *            The field access to be resolved.
	 * @return true if the field could be resolved, false if not.
	 */
	protected boolean resolveSingleField(ExtractionMode mode,
			FieldAccess fieldAccess) {
		if (fieldAccess == null) {
			return false;
		}
		if ((symbolTable.isProcessedFieldAccess(fieldAccess) == null)
				|| symbolTable.isProcessedFieldAccess(fieldAccess)
						.booleanValue()) {
			return true;
		}
		String fieldName = ((Type) fieldAccess
				.getFirstIsFieldNameOfIncidence(EdgeDirection.IN).getAlpha())
				.get_name();
		JavaVertex scope = (JavaVertex) symbolTable
				.getScopeOfFieldAccess(fieldAccess);
		if (fieldAccess.getFirstIsFieldContainerOfIncidence(EdgeDirection.IN) == null) {
			if (fieldName.equals("this")) {
				Type enclosingType = ResolverUtilities
						.getEnclosingTypeFromScope(scope, symbolTable);
				if (enclosingType != null) {
					return linkFieldAccessToDeclaration(fieldAccess,
							enclosingType, scope, fieldName);
				} else {
					// There is no real "else" here because if there is a field
					// access in a file, it has to be inside a method
					// of a class (or something of the like)... Reflection is
					// definitely not an option here!
					return finishUnresolvedFieldAccess(fieldAccess);
				}
			} else if (fieldName.equals("super")) {
				ClassDefinition enclosingClass = ResolverUtilities
						.getEnclosingClassFromScope(scope, symbolTable);
				if (enclosingClass != null) {
					ClassDefinition superClass = null;
					try {
						superClass = ResolverUtilities
								.getSuperClass(enclosingClass);
					} catch (Exception e) {
						return finishUnresolvedFieldAccess(fieldAccess);
					} // a superclass has been defined, but could not be
						// resolved, there is nothing left to be done here!
					if (superClass == null) {
						// There is no explicit superclass, so set
						// java.lang.Object as superClass by reflection (or
						// existing vertex)...
						if (symbolTable.hasTypeDefinition("java.lang.Object")) {
							superClass = (ClassDefinition) symbolTable
									.getTypeDefinition("java.lang.Object");
						} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
								&& (ResolverUtilities
										.createTypeUsingReflection(
												"java.lang.Object", mode,
												symbolTable))) {
							superClass = (ClassDefinition) symbolTable
									.getTypeDefinition("java.lang.Object");
						}
					}
					if (superClass != null) {
						return linkFieldAccessToDeclaration(fieldAccess,
								superClass, scope, fieldName);
					} else {
						return finishUnresolvedFieldAccess(fieldAccess);
					}
				} else {
					// There is no "else" here because the superclass can be
					// accessed only inside a class.
					// Reflection also isn't an option here, because if type
					// resolving hasn't already linked the
					// according type specification with the definition of the
					// superclass, there's nothing else
					// that could be done here.
					return finishUnresolvedFieldAccess(fieldAccess);
				}
			} else {
				Type enclosingType = ResolverUtilities
						.getEnclosingTypeFromScope(scope, symbolTable);
				if (enclosingType != null) {
					if (enclosingType instanceof ClassDefinition) {
						VariableDeclaration variableInSuperClass = null;
						ClassDefinition currentSuperClass = (ClassDefinition) enclosingType;
						do {
							try {
								currentSuperClass = ResolverUtilities
										.getSuperClass(currentSuperClass);
							} catch (Exception e) {
								currentSuperClass = null;
							} // a superclass has been defined, but could not be
								// resolved, there is nothing left to be done
								// here!
							if (currentSuperClass != null) {
								variableInSuperClass = symbolTable
										.getVariableDeclaration(
												currentSuperClass
														.get_fullyQualifiedName(),
												fieldName);
							}
						} while ((variableInSuperClass == null)
								&& (currentSuperClass != null));
						if (variableInSuperClass != null) {
							return linkFieldAccessToDeclaration(fieldAccess,
									variableInSuperClass, scope, fieldName);
						}
					}
				}
				Type supremeType = ResolverUtilities.getSupremeTypeFromScope(
						scope, symbolTable);
				if (supremeType == null) {
					return finishUnresolvedFieldAccess(fieldAccess);
				}
				String packageName = ResolverUtilities
						.getPackageNameFromSupremeType(supremeType);
				if (packageName == null) {
					return finishUnresolvedFieldAccess(fieldAccess);
				}
				String possibleFullyQualifiedNameToCheck = null;
				if (packageName == "") {
					possibleFullyQualifiedNameToCheck = fieldName;
				} else {
					possibleFullyQualifiedNameToCheck = packageName + "."
							+ fieldName;
				}
				if (symbolTable
						.hasTypeDefinition(possibleFullyQualifiedNameToCheck)) {
					return linkFieldAccessToDeclaration(
							fieldAccess,
							symbolTable
									.getTypeDefinition(possibleFullyQualifiedNameToCheck),
							scope, fieldName);
				} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
						&& (ResolverUtilities.createTypeUsingReflection(
								possibleFullyQualifiedNameToCheck, mode,
								symbolTable))) {
					return linkFieldAccessToDeclaration(
							fieldAccess,
							symbolTable
									.getTypeDefinition(possibleFullyQualifiedNameToCheck),
							scope, fieldName);
				}
				ArrayList<ImportDefinition> imports = ResolverUtilities
						.getImportsFromSupremeType(supremeType);
				if (imports == null) {
					return finishUnresolvedFieldAccess(fieldAccess);
				}
				for (int counter = 0; counter < imports.size(); counter++) {
					ImportDefinition importVertex = imports.get(counter);
					String fullyQualifiedNameOfImport = ResolverUtilities
							.getFullyQualifiedName(importVertex);
					if ((importVertex instanceof ClassImportDefinition)
							&& (fullyQualifiedNameOfImport.endsWith("."
									+ fieldName))) {
						if (symbolTable
								.hasTypeDefinition(fullyQualifiedNameOfImport)) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition(fullyQualifiedNameOfImport),
									scope, fieldName);
						} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
								&& (ResolverUtilities
										.createTypeUsingReflection(
												fullyQualifiedNameOfImport,
												mode, symbolTable))) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition(fullyQualifiedNameOfImport),
									scope, fieldName);
						}
					}
					if (importVertex instanceof PackageImportDefinition) {
						if (symbolTable
								.hasTypeDefinition(fullyQualifiedNameOfImport
										+ "." + fieldName)) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition(fullyQualifiedNameOfImport
													+ "." + fieldName), scope,
									fieldName);
						} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
								&& (ResolverUtilities
										.createTypeUsingReflection(
												fullyQualifiedNameOfImport
														+ "." + fieldName,
												mode, symbolTable))) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition(fullyQualifiedNameOfImport
													+ "." + fieldName), scope,
									fieldName);
						}
					}
				}
			}
		} else {
			Expression fieldContainer = (Expression) fieldAccess
					.getFirstIsFieldContainerOfIncidence(EdgeDirection.IN)
					.getAlpha();
			if (fieldContainer instanceof FieldAccess) {
				FieldAccess containerAccess = (FieldAccess) fieldContainer;
				// try to resolve the container field first, as it might not
				// have been processed (If it has been already, the method
				// returns at once so there are no infinite loops).
				resolveSingleField(mode, containerAccess);
				if ((containerAccess
						.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) != null)
						&& (containerAccess
								.getFirstIsDeclarationOfAccessedFieldIncidence(
										EdgeDirection.IN).getAlpha() instanceof Type)) {
					Type containerType = (Type) containerAccess
							.getFirstIsDeclarationOfAccessedFieldIncidence(
									EdgeDirection.IN).getAlpha();
					if (fieldName.equals("this")) {
						return linkFieldAccessToDeclaration(fieldAccess,
								containerType, scope, fieldName);
					}
					if (fieldName.equals("class")) {
						if (symbolTable.hasTypeDefinition("java.lang.Class")) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition("java.lang.Class"),
									scope, fieldName);
						} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
								&& (ResolverUtilities
										.createTypeUsingReflection(
												"java.lang.Class", mode,
												symbolTable))) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition("java.lang.Class"),
									scope, fieldName);
						} else {
							finishUnresolvedFieldAccess(fieldAccess);
						}
					}
					return resolveAccessedFieldFromContainingType(
							containerType, fieldName, fieldAccess, mode, scope);
				} else if ((containerAccess
						.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) != null)
						&& (containerAccess
								.getFirstIsDeclarationOfAccessedFieldIncidence(
										EdgeDirection.IN).getAlpha() instanceof FieldDeclaration)) {
					FieldDeclaration containerDeclaration = (FieldDeclaration) containerAccess
							.getFirstIsDeclarationOfAccessedFieldIncidence(
									EdgeDirection.IN).getAlpha();
					if (fieldName.equals("this")) {
						return linkFieldAccessToDeclaration(fieldAccess,
								containerDeclaration, scope, fieldName);
					}
					if (fieldName.equals("class")) {
						if (symbolTable.hasTypeDefinition("java.lang.Class")) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition("java.lang.Class"),
									scope, fieldName);
						} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
								&& (ResolverUtilities
										.createTypeUsingReflection(
												"java.lang.Class", mode,
												symbolTable))) {
							return linkFieldAccessToDeclaration(
									fieldAccess,
									symbolTable
											.getTypeDefinition("java.lang.Class"),
									scope, fieldName);
						} else {
							finishUnresolvedFieldAccess(fieldAccess);
						}
					}
					Type containerType = ResolverUtilities
							.getTypeFromFieldDeclaration(containerDeclaration,
									containerAccess);
					if (containerType != null) {
						return resolveAccessedFieldFromContainingType(
								containerType, fieldName, fieldAccess, mode,
								scope);
					} else {
						return finishUnresolvedFieldAccess(fieldAccess);
					}
				} else if (containerAccess
						.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) == null) {
					String possibleFullyQualifiedNameToCheck = getQualifiedNameFromFieldContainers(fieldAccess);
					if (possibleFullyQualifiedNameToCheck == null) {
						return finishUnresolvedFieldAccess(fieldAccess);
					}
					if (symbolTable
							.hasTypeDefinition(possibleFullyQualifiedNameToCheck)) {
						return linkFieldAccessToDeclaration(
								fieldAccess,
								symbolTable
										.getTypeDefinition(possibleFullyQualifiedNameToCheck),
								scope, fieldName);
					} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
							&& (ResolverUtilities.createTypeUsingReflection(
									possibleFullyQualifiedNameToCheck, mode,
									symbolTable))) {
						return linkFieldAccessToDeclaration(
								fieldAccess,
								symbolTable
										.getTypeDefinition(possibleFullyQualifiedNameToCheck),
								scope, fieldName);
					}
				}
			} else if (fieldContainer instanceof MethodInvocation) {
				Type containerInvocationReturnType = ResolverUtilities
						.getReturnTypeFromMethodInvocation(
								(MethodInvocation) fieldContainer, mode,
								methodResolver);
				if (containerInvocationReturnType != null) {
					return resolveAccessedFieldFromContainingType(
							containerInvocationReturnType, fieldName,
							fieldAccess, mode, scope);
				} else {
					return finishUnresolvedFieldAccess(fieldAccess);
				}
			} else if (fieldContainer instanceof ClassCast) {
				Type containerCastType = ResolverUtilities
						.getCastedTypeFromClassCast((ClassCast) fieldContainer);
				if (containerCastType != null) {
					return resolveAccessedFieldFromContainingType(
							containerCastType, fieldName, fieldAccess, mode,
							scope);
				} else {
					return finishUnresolvedFieldAccess(fieldAccess);
				}
			} else if (fieldContainer instanceof ObjectCreation) {
				Type containerCreationType = ResolverUtilities
						.getTypeFromObjectCreation((ObjectCreation) fieldContainer);
				if (containerCreationType != null) {
					return resolveAccessedFieldFromContainingType(
							containerCreationType, fieldName, fieldAccess,
							mode, scope);
				} else {
					return finishUnresolvedFieldAccess(fieldAccess);
				}
			} else if (fieldContainer instanceof ArrayCreation) {
				// Nothing to do here for arrays, as fields of an array (like
				// arr.length; not(!) an element of an array!) can be neither
				// queried nor reflected (arrays are a bit of
				// "compiler magic")...
				return finishUnresolvedFieldAccess(fieldAccess);
			}
		}
		return finishUnresolvedFieldAccess(fieldAccess);
	}

	/**
	 * Converts a chain of fieldAccesses
	 * (fieldAccess.fieldAccess.fieldAccess...) to a qualified name.
	 *
	 * @param fieldAccess
	 *            The last field access in the chain.
	 * @return The qualified name; null if any of the leading field accesses has
	 *         already been resolved.
	 */
	private String getQualifiedNameFromFieldContainers(FieldAccess fieldAccess) {
		if (fieldAccess == null) {
			return null;
		}
		String result = ((Type) fieldAccess
				.getFirstIsFieldNameOfIncidence(EdgeDirection.IN).getAlpha())
				.get_name();
		FieldAccess nextContainer = fieldAccess;
		while (nextContainer
				.getFirstIsFieldContainerOfIncidence(EdgeDirection.IN) != null) {
			if (nextContainer.getFirstIsFieldContainerOfIncidence(
					EdgeDirection.IN).getAlpha() instanceof FieldAccess) {
				nextContainer = (FieldAccess) nextContainer
						.getFirstIsFieldContainerOfIncidence(EdgeDirection.IN)
						.getAlpha();
				if (nextContainer
						.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) != null) {
					return null;
				}
				result = ((Type) nextContainer
						.getFirstIsFieldNameOfIncidence(EdgeDirection.IN)
						.getAlpha()).get_name()
						+ "." + result;
			} else {
				return null;
			}
		}
		return result;
	}

	/**
	 * Resolves a field access to a field (with a known containing type).
	 *
	 * @param containingType
	 *            The vertex of the type in which the field is assumed.
	 * @param fieldName
	 *            The name of the field.
	 * @param fieldAccess
	 *            The vertex of the field access.
	 * @param mode
	 *            The extraction mode to use.
	 * @param scope
	 *            The scope of the field access vertex.
	 * @return true if the field could be resolved, false if not.
	 */
	private boolean resolveAccessedFieldFromContainingType(Type containingType,
			String fieldName, FieldAccess fieldAccess, ExtractionMode mode,
			JavaVertex scope) {
		FieldDeclaration accessedField = symbolTable.getVariableDeclaration(
				containingType.get_fullyQualifiedName(), fieldName);
		if (accessedField == null) {
			accessedField = symbolTable.getEnumConstant(
					containingType.get_fullyQualifiedName(), fieldName);
		}
		if ((accessedField == null)
				&& (containingType instanceof ClassDefinition)) {
			ClassDefinition currentContainerTypeSuperClass = (ClassDefinition) containingType;
			do {
				try {
					currentContainerTypeSuperClass = ResolverUtilities
							.getSuperClass(currentContainerTypeSuperClass);
				} catch (Exception e) {
					return finishUnresolvedFieldAccess(fieldAccess);
				} // a superclass has been defined, but could not be resolved,
					// there is nothing left to be done here!
				if (currentContainerTypeSuperClass != null) {
					accessedField = symbolTable.getVariableDeclaration(
							currentContainerTypeSuperClass
									.get_fullyQualifiedName(), fieldName);
				}
			} while ((accessedField == null)
					&& (currentContainerTypeSuperClass != null));
		}
		if ((accessedField == null) && containingType.is_external()
				&& (mode == ExtractionMode.EAGER)) {
			Class<?> externalClass = null;
			try {
				externalClass = Class.forName(containingType
						.get_fullyQualifiedName());
			} catch (Exception exception) {
			}
			if (externalClass != null) {
				if (externalClass.isEnum()) {
					for (Object currentEnumConstant : externalClass
							.getEnumConstants()) {
						if (currentEnumConstant.toString().equals(fieldName)) {
							accessedField = ResolverUtilities
									.createEnumConstant(currentEnumConstant,
											containingType, symbolTable);
						}
					}
				}
				if (accessedField == null) {
					try {
						java.lang.reflect.Field externalField = externalClass
								.getField(fieldName);
						accessedField = ResolverUtilities
								.createFieldDeclaration(externalField,
										containingType, mode, symbolTable);
					} catch (Exception exception) {
					}
				}
			}
		}
		if (accessedField != null) {
			return linkFieldAccessToDeclaration(fieldAccess, accessedField,
					scope, fieldName);
		}
		return finishUnresolvedFieldAccess(fieldAccess);
	}

	/**
	 * Creates the semantic edge between a field access and it's definition.
	 * Also assures that identical identifiers for other accesses to this field
	 * exist only once per file.
	 *
	 * @param fieldAccess
	 *            The vertex of the field access.
	 * @param declaration
	 *            The vertex of the field's declaration.
	 * @param scope
	 *            The scope of the field access vertex.
	 * @param fieldName
	 *            The name of the field.
	 * @return true (always; this is for code reduction whereever this function
	 *         is used, because if it has been executed, a field definitely has
	 *         been resolved).
	 */
	private boolean linkFieldAccessToDeclaration(FieldAccess fieldAccess,
			JavaVertex declaration, JavaVertex scope, String fieldName) {
		Type supremeTypeOfFieldAccess = ResolverUtilities
				.getSupremeTypeFromScope(scope, symbolTable);
		if (supremeTypeOfFieldAccess != null) {
			Type supremeTypeOfCurrentFieldAccess = null;
			FieldAccess currentFieldAccess = null;
			boolean foundIdenticalIdentifier = false;
			for (Edge edge : declaration.incidences(
					IsDeclarationOfAccessedField.EC, EdgeDirection.OUT)) {
				if (foundIdenticalIdentifier) {
					break;
				}
				currentFieldAccess = (FieldAccess) edge.getThat();
				if ((currentFieldAccess
						.getFirstIsFieldNameOfIncidence(EdgeDirection.IN) != null)
						&& (((Type) currentFieldAccess
								.getFirstIsFieldNameOfIncidence(
										EdgeDirection.IN).getAlpha())
								.get_name().equals(fieldName))) {
					supremeTypeOfCurrentFieldAccess = ResolverUtilities
							.getSupremeTypeFromScope(symbolTable
									.getScopeOfFieldAccess(currentFieldAccess),
									symbolTable);
					if ((supremeTypeOfCurrentFieldAccess != null)
							&& (supremeTypeOfCurrentFieldAccess == supremeTypeOfFieldAccess)) {
						IsFieldNameOf edgeToReattach = fieldAccess
								.getFirstIsFieldNameOfIncidence(EdgeDirection.IN);
						Vertex identifierToDelete = edgeToReattach.getAlpha();
						edgeToReattach.setAlpha(currentFieldAccess
								.getFirstIsFieldNameOfIncidence(
										EdgeDirection.IN).getAlpha());
						identifierToDelete.delete();
						foundIdenticalIdentifier = true;
					}
				}
			}
			// Iterable< EdgeVertexPair< ? extends IsDeclarationOfAccessedField,
			// ? extends Vertex > > isDeclarationOfAccessedFieldEdges = new
			// IncidenceIterable< IsDeclarationOfAccessedField, Vertex >(
			// declaration, IsDeclarationOfAccessedField.class,
			// EdgeDirection.OUT );
			// Iterator< EdgeVertexPair< ? extends IsDeclarationOfAccessedField,
			// ? extends Vertex > > edgeIterator =
			// isDeclarationOfAccessedFieldEdges.iterator();
			// while( edgeIterator.hasNext() && !foundIdenticalIdentifier ){
			// currentFieldAccess = ( FieldAccess
			// )edgeIterator.next().getVertex();
			// if( ( currentFieldAccess.getFirstIsFieldNameOf( EdgeDirection.IN
			// ) != null ) && ( ( ( Identifier
			// )currentFieldAccess.getFirstIsFieldNameOf( EdgeDirection.IN
			// ).getAlpha() ).getName().equals( fieldName ) ) ){
			// supremeTypeOfCurrentFieldAccess =
			// ResolverUtilities.getSupremeTypeFromScope(
			// symbolTable.getScopeOfFieldAccess( currentFieldAccess ),
			// symbolTable );
			// if( ( supremeTypeOfCurrentFieldAccess != null ) && (
			// supremeTypeOfCurrentFieldAccess == supremeTypeOfFieldAccess ) ){
			// IsFieldNameOf edgeToReattach = fieldAccess.getFirstIsFieldNameOf(
			// EdgeDirection.IN );
			// Vertex identifierToDelete = edgeToReattach.getAlpha();
			// edgeToReattach.setAlpha( ( Identifier
			// )currentFieldAccess.getFirstIsFieldNameOf( EdgeDirection.IN
			// ).getAlpha() );
			// identifierToDelete.delete();
			// foundIdenticalIdentifier = true;
			// }
			// }
			// }
		}
		/* IsDeclarationOfAccessedField linkingEdge = */symbolTable.getGraph()
				.createIsDeclarationOfAccessedField(declaration, fieldAccess);
		symbolTable.setFieldAccessProcessed(fieldAccess);
		symbolTable.increaseAmountOfFieldAccessesTreatedByResolver();
		if ((fieldProgressBar != null)
				&& ((symbolTable.getAmountOfFieldAccessesTreatedByResolver()
						% fieldProgressBar.getUpdateInterval()) == 0)) {
			fieldProgressBar.progress(1);
		}
		return true;
	}

	/**
	 * Triggers all required actions if a field access could not be resolved at
	 * all.
	 *
	 * @param fieldAccess
	 *            The vertex of the field access.
	 * @return false (always; this is for code reduction whereever this function
	 *         is used, because if it has been executed, a field definitely has
	 *         not been resolved).
	 */
	private boolean finishUnresolvedFieldAccess(FieldAccess fieldAccess) {
		symbolTable.setFieldAccessProcessed(fieldAccess);
		symbolTable.increaseAmountOfFieldAccessesTreatedByResolver();
		symbolTable.increaseAmountOfUnresolvedFieldAccesses();
		if ((fieldProgressBar != null)
				&& ((symbolTable.getAmountOfFieldAccessesTreatedByResolver()
						% fieldProgressBar.getUpdateInterval()) == 0)) {
			fieldProgressBar.progress(1);
		}
		return false;
	}
}
