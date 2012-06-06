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
import java.util.HashMap;
import java.util.Iterator;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.ExtractionMode;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayType;
import de.uni_koblenz.jgralab.grabaja.java5schema.BooleanConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInTypes;
import de.uni_koblenz.jgralab.grabaja.java5schema.CharConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoubleConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.FloatConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixOperators;
import de.uni_koblenz.jgralab.grabaja.java5schema.IntegerConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArgumentOfMethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArrayElementIndexOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDeclarationOfInvokedMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDimensionInitializerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsNameOfInvokedMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.LongConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Member;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.Null;
import de.uni_koblenz.jgralab.grabaja.java5schema.ObjectCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.PostfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.PrefixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.PrefixOperators;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.StringConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableLengthDeclaration;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;

/**
 * Resolves invocations of methods and constructors.
 *
 * @author: abaldauf@uni-koblenz.de
 */
public class MethodResolver extends Resolver {

	/**
	 * A reference to the instance of the field resolver used during global
	 * resolving.
	 */
	private FieldResolver fieldResolver = null;

	/**
	 * The progress bar to be shown during invocation resolving (but not if an
	 * invocation resolving is triggered during one of the other resolvers
	 * execution).
	 */
	private ConsoleProgressFunction methodProgressBar = null;

	/**
	 * Instantiates and initializes an instance.
	 *
	 * @param symbolTable
	 *            The symbol table to be used for resolving.
	 */
	public MethodResolver(SymbolTable symbolTable) {
		super.symbolTable = symbolTable;
	}

	/**
	 * Sets the reference to the field resolver instance.
	 *
	 * @param resolver
	 *            The instance of the field resolver.
	 */
	public void setFieldResolver(FieldResolver resolver) {
		this.fieldResolver = resolver;
	}

	/**
	 * Resolves all the invocations stored in the symbol table.
	 *
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if all of the invoked methods / constructors could be
	 *         resolved, false if at least one could not be resolved.
	 */
	public boolean resolveMethods(ExtractionMode mode) {
		boolean result = true;
		if ((symbolTable != null)
				&& (symbolTable.getMethodInvocations() != null)) {
			methodProgressBar = new ConsoleProgressFunction();
			methodProgressBar.init(symbolTable.amountOfMethodInvocations());
			methodProgressBar.progress(symbolTable
					.getAmountOfMethodInvocationsTreatedByResolver()
					/ methodProgressBar.getUpdateInterval());
			Iterator<MethodInvocation> methodInvocationIterator = symbolTable
					.getMethodInvocations().iterator();
			while (methodInvocationIterator.hasNext()) {
				MethodInvocation currentMethodInvocation = methodInvocationIterator
						.next();
				if (!resolveSingleMethod(mode, currentMethodInvocation)) {
					result = false;
				}
			}
			methodProgressBar.finished();
			methodProgressBar = null;
		}
		return result;
	}

	/**
	 * Resolves a singe invocation.
	 *
	 * @param mode
	 *            The extraction mode to use.
	 * @param methodInvocation
	 *            The invocation to be resolved.
	 * @return true if the method / constructor could be resolved, false if not.
	 */
	protected boolean resolveSingleMethod(ExtractionMode mode,
			MethodInvocation methodInvocation) {
		if (methodInvocation == null) {
			return false;
		}
		if (symbolTable.isProcessedMethodInvocation(methodInvocation)
				.booleanValue()) {
			return true;
		}
		ArrayList<Expression> methodInvocationArguments = new ArrayList<Expression>();
		for (IsArgumentOfMethodInvocation edge : methodInvocation
				.getIsArgumentOfMethodInvocationIncidences(EdgeDirection.IN)) {
			Expression currentInvocationArgument = (Expression) edge.getThat();
			methodInvocationArguments.add(currentInvocationArgument);
			if (currentInvocationArgument instanceof FieldAccess) {
				fieldResolver.resolveSingleField(mode,
						(FieldAccess) currentInvocationArgument);
			} else if (currentInvocationArgument instanceof MethodInvocation) {
				resolveSingleMethod(mode,
						(MethodInvocation) currentInvocationArgument);
			}
		}

		Vertex scope = symbolTable.getScopeOfMethodInvocation(methodInvocation);
		String methodName = null;
		if (methodInvocation
				.getFirstIsNameOfInvokedMethodIncidence(EdgeDirection.IN) == null) {
			if (methodInvocation
					.getFirstIsConstructorInvocationOfIncidence(EdgeDirection.OUT) == null) {
				return finishUnresolvedMethodInvocation(methodInvocation);
			}
			ObjectCreation containingCreation = (ObjectCreation) methodInvocation
					.getFirstIsConstructorInvocationOfIncidence(
							EdgeDirection.OUT).getOmega();
			if (containingCreation
					.getFirstIsTypeOfObjectIncidence(EdgeDirection.IN) == null) {
				return finishUnresolvedMethodInvocation(methodInvocation);
			}
			TypeSpecification containingCreationSpecification = (TypeSpecification) containingCreation
					.getFirstIsTypeOfObjectIncidence(EdgeDirection.IN)
					.getAlpha();
			if (containingCreationSpecification
					.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN) == null) {
				return finishUnresolvedMethodInvocation(methodInvocation);
			}
			Type containingCreationType = (Type) containingCreationSpecification
					.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN)
					.getAlpha();
			methodName = containingCreationType.get_name();
			Member methodDeclarationVertex = findMatchingDeclaration(
					methodInvocation, methodInvocationArguments,
					containingCreationType.get_fullyQualifiedName(),
					methodName, InvocationSearchMode.constructorsOnly, mode);
			if ((methodDeclarationVertex == null)
					&& (mode == ExtractionMode.EAGER)
					&& containingCreationType.is_external()) {
				methodDeclarationVertex = findMatchingDeclarationByReflection(
						methodInvocation, methodInvocationArguments,
						containingCreationType, methodName,
						InvocationSearchMode.constructorsOnly, mode);
			}
			if (methodDeclarationVertex == null) {
				return finishUnresolvedMethodInvocation(methodInvocation);
			}
			return linkMethodInvocationToDeclaration(methodInvocation,
					methodDeclarationVertex, scope, methodName);
		}
		methodName = ((Type) methodInvocation
				.getFirstIsNameOfInvokedMethodIncidence(EdgeDirection.IN)
				.getAlpha()).get_name();
		if (methodInvocation
				.getFirstIsMethodContainerOfIncidence(EdgeDirection.IN) == null) {
			if ((methodName != null) && methodName.equals("this")) {
				ClassDefinition enclosingClass = ResolverUtilities
						.getEnclosingClassFromScope(scope, symbolTable);
				if (enclosingClass == null) {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
				Member methodDeclarationVertex = findMatchingDeclaration(
						methodInvocation, methodInvocationArguments,
						enclosingClass.get_fullyQualifiedName(),
						enclosingClass.get_name(),
						InvocationSearchMode.constructorsOnly, mode);
				if (methodDeclarationVertex == null) {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
				return linkMethodInvocationToDeclaration(methodInvocation,
						methodDeclarationVertex, scope, methodName);
			} else if ((methodName != null) && methodName.equals("super")) {
				ClassDefinition enclosingClass = ResolverUtilities
						.getEnclosingClassFromScope(scope, symbolTable);
				if (enclosingClass == null) {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
				ClassDefinition superClass = null;
				try {
					superClass = ResolverUtilities
							.getSuperClass(enclosingClass);
				} catch (Exception e) {
					return finishUnresolvedMethodInvocation(methodInvocation);
				} // a superclass has been defined, but could not be resolved,
					// there is nothing left to be done here!
				if (superClass == null) {
					// There is no explicit superclass, so set java.lang.Object
					// as superClass by reflection (or existing vertex)...
					if (symbolTable.hasTypeDefinition("java.lang.Object")) {
						superClass = (ClassDefinition) symbolTable
								.getTypeDefinition("java.lang.Object");
					} else if (((mode == ExtractionMode.EAGER) || (mode == ExtractionMode.COMPLETE))
							&& (ResolverUtilities.createTypeUsingReflection(
									"java.lang.Object", mode, symbolTable))) {
						superClass = (ClassDefinition) symbolTable
								.getTypeDefinition("java.lang.Object");
					}
				}
				if (superClass != null) {
					Member methodDeclarationVertex = findMatchingDeclaration(
							methodInvocation, methodInvocationArguments,
							superClass.get_fullyQualifiedName(),
							superClass.get_name(),
							InvocationSearchMode.constructorsOnly, mode);
					if ((methodDeclarationVertex == null)
							&& (mode == ExtractionMode.EAGER)
							&& superClass.is_external()) {
						methodDeclarationVertex = findMatchingDeclarationByReflection(
								methodInvocation, methodInvocationArguments,
								superClass, superClass.get_name(),
								InvocationSearchMode.constructorsOnly, mode);
					}
					if (methodDeclarationVertex == null) {
						return finishUnresolvedMethodInvocation(methodInvocation);
					}
					return linkMethodInvocationToDeclaration(methodInvocation,
							methodDeclarationVertex, scope, methodName);
				} else {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
			} else {
				Vertex currentScope = scope;
				Type currentEnclosingType = null;
				Member methodDeclarationVertex = null;
				do {
					currentEnclosingType = ResolverUtilities
							.getEnclosingTypeFromScope(currentScope,
									symbolTable);
					currentScope = symbolTable
							.getParentScope(currentEnclosingType);
					if (currentEnclosingType == null) {
						return finishUnresolvedMethodInvocation(methodInvocation);
					}
					methodDeclarationVertex = findMatchingDeclaration(
							methodInvocation, methodInvocationArguments,
							currentEnclosingType.get_fullyQualifiedName(),
							methodName, InvocationSearchMode.methodsOnly, mode);
					if (methodDeclarationVertex != null) {
						return linkMethodInvocationToDeclaration(
								methodInvocation, methodDeclarationVertex,
								scope, methodName);
					}
					if (currentEnclosingType instanceof ClassDefinition) {
						ClassDefinition currentSuperClass = (ClassDefinition) currentEnclosingType;
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
								methodDeclarationVertex = findMatchingDeclaration(
										methodInvocation,
										methodInvocationArguments,
										currentSuperClass
												.get_fullyQualifiedName(),
										methodName,
										InvocationSearchMode.methodsOnly, mode);
								if ((methodDeclarationVertex == null)
										&& (mode == ExtractionMode.EAGER)
										&& currentSuperClass.is_external()) {
									methodDeclarationVertex = findMatchingDeclarationByReflection(
											methodInvocation,
											methodInvocationArguments,
											currentSuperClass, methodName,
											InvocationSearchMode.methodsOnly,
											mode);
								}
							}
						} while ((methodDeclarationVertex == null)
								&& (currentSuperClass != null));
					}
					if (methodDeclarationVertex != null) {
						return linkMethodInvocationToDeclaration(
								methodInvocation, methodDeclarationVertex,
								scope, methodName);
					}
				} while (true);
			}
		} else {
			Expression methodContainer = (Expression) methodInvocation
					.getFirstIsMethodContainerOfIncidence(EdgeDirection.IN)
					.getAlpha();
			if (methodContainer instanceof FieldAccess) {
				FieldAccess containerAccess = (FieldAccess) methodContainer;
				// try to resolve the container field first, as it might not
				// have been processed (If it has been already, the method
				// returns at once so there are no infinite loops).
				fieldResolver.resolveSingleField(mode, containerAccess);
				if ((containerAccess
						.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) != null)
						&& (containerAccess
								.getFirstIsDeclarationOfAccessedFieldIncidence(
										EdgeDirection.IN).getAlpha() instanceof Type)) {
					Type containerType = (Type) containerAccess
							.getFirstIsDeclarationOfAccessedFieldIncidence(
									EdgeDirection.IN).getAlpha();
					return resolveInvokedMethodFromContainingType(
							containerType, methodName, methodInvocation,
							methodInvocationArguments, mode, scope);
				} else if ((containerAccess
						.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) != null)
						&& (containerAccess
								.getFirstIsDeclarationOfAccessedFieldIncidence(
										EdgeDirection.IN).getAlpha() instanceof FieldDeclaration)) {
					Type containerType = ResolverUtilities
							.getTypeFromFieldDeclaration(
									(FieldDeclaration) containerAccess
											.getFirstIsDeclarationOfAccessedFieldIncidence(
													EdgeDirection.IN)
											.getAlpha(), containerAccess);
					if (containerType != null) {
						return resolveInvokedMethodFromContainingType(
								containerType, methodName, methodInvocation,
								methodInvocationArguments, mode, scope);
					} else {
						return finishUnresolvedMethodInvocation(methodInvocation);
					}
				} else {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
			} else if (methodContainer instanceof MethodInvocation) {
				Type containerInvocationReturnType = ResolverUtilities
						.getReturnTypeFromMethodInvocation(
								(MethodInvocation) methodContainer, mode, this);
				if (containerInvocationReturnType != null) {
					return resolveInvokedMethodFromContainingType(
							containerInvocationReturnType, methodName,
							methodInvocation, methodInvocationArguments, mode,
							scope);
				} else {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
			} else if (methodContainer instanceof ClassCast) {
				Type containerCastType = ResolverUtilities
						.getCastedTypeFromClassCast((ClassCast) methodContainer);
				if (containerCastType != null) {
					return resolveInvokedMethodFromContainingType(
							containerCastType, methodName, methodInvocation,
							methodInvocationArguments, mode, scope);
				} else {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
			} else if (methodContainer instanceof ObjectCreation) {
				Type containerCreationType = ResolverUtilities
						.getTypeFromObjectCreation((ObjectCreation) methodContainer);
				if (containerCreationType != null) {
					return resolveInvokedMethodFromContainingType(
							containerCreationType, methodName,
							methodInvocation, methodInvocationArguments, mode,
							scope);
				} else {
					return finishUnresolvedMethodInvocation(methodInvocation);
				}
			} else if (methodContainer instanceof ArrayCreation) {
				// Nothing to do here for arrays, as methods of an array (like
				// arr.length; not(!) an element of an array!) can be neither
				// queried nor reflected (arrays are a bit of
				// "compiler magic")...
				return finishUnresolvedMethodInvocation(methodInvocation);
			}
		}
		return finishUnresolvedMethodInvocation(methodInvocation);
	}

	/**
	 * Resolves an invocation of a method / constructor (with a known containing
	 * type).
	 *
	 * @param containingType
	 *            The vertex of the type in which the field is assumed.
	 * @param methodName
	 *            The name of the method.
	 * @param methodInvocation
	 *            The vertex of the invocation.
	 * @param methodInvocationArguments
	 *            The list of arguments of the invocation.
	 * @param mode
	 *            The extraction mode to use.
	 * @param scope
	 *            The scope of the invocation vertex.
	 * @return true if the method / constructor could be resolved, false if not.
	 */
	private boolean resolveInvokedMethodFromContainingType(Type containingType,
			String methodName, MethodInvocation methodInvocation,
			ArrayList<Expression> methodInvocationArguments,
			ExtractionMode mode, Vertex scope) {
		Member invokedMethod = findMatchingDeclaration(methodInvocation,
				methodInvocationArguments,
				containingType.get_fullyQualifiedName(), methodName,
				InvocationSearchMode.methodsOnly, mode);
		if ((invokedMethod == null)
				&& (containingType instanceof ClassDefinition)) {
			ClassDefinition currentContainerTypeSuperClass = (ClassDefinition) containingType;
			do {
				try {
					currentContainerTypeSuperClass = ResolverUtilities
							.getSuperClass(currentContainerTypeSuperClass);
				} catch (Exception e) {
					return finishUnresolvedMethodInvocation(methodInvocation);
				} // a superclass has been defined, but could not be resolved,
					// there is nothing left to be done here!
				if (currentContainerTypeSuperClass != null) {
					invokedMethod = findMatchingDeclaration(methodInvocation,
							methodInvocationArguments,
							currentContainerTypeSuperClass
									.get_fullyQualifiedName(), methodName,
							InvocationSearchMode.methodsOnly, mode);
				}
				if ((invokedMethod == null)
						&& (currentContainerTypeSuperClass != null)
						&& currentContainerTypeSuperClass.is_external()
						&& (mode == ExtractionMode.EAGER)) {
					invokedMethod = findMatchingDeclarationByReflection(
							methodInvocation, methodInvocationArguments,
							currentContainerTypeSuperClass, methodName,
							InvocationSearchMode.methodsOnly, mode);
				}
			} while ((invokedMethod == null)
					&& (currentContainerTypeSuperClass != null)
					&& !currentContainerTypeSuperClass.is_external());
		}
		if ((invokedMethod == null) && containingType.is_external()
				&& (mode == ExtractionMode.EAGER)) {
			invokedMethod = findMatchingDeclarationByReflection(
					methodInvocation, methodInvocationArguments,
					containingType, methodName,
					InvocationSearchMode.methodsOnly, mode);
		}
		if (invokedMethod != null) {
			return linkMethodInvocationToDeclaration(methodInvocation,
					invokedMethod, scope, methodName);
		}
		return finishUnresolvedMethodInvocation(methodInvocation);
	}

	/**
	 * Defines the different modes what to search for (constructors, methods, or
	 * both).
	 */
	private enum InvocationSearchMode {
		constructorsOnly, methodsOnly, all
	}

	/**
	 * Gets the method / constructor definition that matches invocation.
	 * Performs argument typechecking which is required due to overloading.
	 *
	 * @param methodInvocation
	 *            The vertex of the invocation.
	 * @param methodInvocationArguments
	 *            The list of arguments of the invocation.
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified name of the type in which the method /
	 *            constructor is assumed.
	 * @param methodName
	 *            The name of the method.
	 * @param searchMode
	 *            Defines what to search for.
	 * @param mode
	 *            The extraction mode to use.
	 * @return The vertex representing the method's / constructor's definition;
	 *         null if no matching definition could be found.
	 */
	private Member findMatchingDeclaration(MethodInvocation methodInvocation,
			ArrayList<Expression> methodInvocationArguments,
			String fullyQualifiedNameOfContainingType, String methodName,
			InvocationSearchMode searchMode, ExtractionMode mode) {
		if ((searchMode != InvocationSearchMode.methodsOnly) /*
															 * ||
															 * fullyQualifiedNameOfContainingType
															 * .equals(
															 * methodName ) ||
															 * fullyQualifiedNameOfContainingType
															 * .endsWith( "." +
															 * methodName )
															 */) {
			ArrayList<ConstructorDefinition> constructorsInType = symbolTable
					.getConstructorDefinitions(fullyQualifiedNameOfContainingType);
			if ((constructorsInType == null)
					|| (constructorsInType.size() == 0)) {
				return null;
			}
			for (ConstructorDefinition currentConstructorDefinition : constructorsInType) {
				ArrayList<ParameterDeclaration> currentConstructorDefinitionParameters = getParametersFromDeclaration(currentConstructorDefinition);
				if (currentConstructorDefinitionParameters.size() == methodInvocationArguments
						.size()) {
					int counter = 0;
					boolean isMatch = true;
					while (isMatch
							&& (counter < currentConstructorDefinitionParameters
									.size())) {
						isMatch = argumentIsValidParameter(
								methodInvocationArguments.get(counter),
								currentConstructorDefinitionParameters
										.get(counter), mode);
						counter++;
					}
					if (isMatch) {
						return currentConstructorDefinition;
					}
				} else if ((currentConstructorDefinitionParameters.size() > 0)
						&& (currentConstructorDefinitionParameters.size() < methodInvocationArguments
								.size())
						&& (currentConstructorDefinitionParameters
								.get(currentConstructorDefinitionParameters
										.size() - 1) instanceof VariableLengthDeclaration)) {
					int counter = 0;
					boolean isMatch = true;
					ParameterDeclaration currentParameter = null;
					while (isMatch
							&& (counter < methodInvocationArguments.size())) {
						if (counter < currentConstructorDefinitionParameters
								.size()) {
							currentParameter = currentConstructorDefinitionParameters
									.get(counter);
						}
						isMatch = argumentIsValidParameter(
								methodInvocationArguments.get(counter),
								currentParameter, mode);
						counter++;
					}
					if (isMatch) {
						return currentConstructorDefinition;
					}
				}
			}
		}
		if (searchMode != InvocationSearchMode.constructorsOnly) {
			ArrayList<MethodDeclaration> methodsInType = symbolTable
					.getMethodDeclarations(fullyQualifiedNameOfContainingType,
							methodName);
			if ((methodsInType == null) || (methodsInType.size() == 0)) {
				return null;
			}
			for (MethodDeclaration currentMethodDeclaration : methodsInType) {
				ArrayList<ParameterDeclaration> currentMethodDeclarationParameters = getParametersFromDeclaration(currentMethodDeclaration);
				if (currentMethodDeclarationParameters.size() == methodInvocationArguments
						.size()) {
					int counter = 0;
					boolean isMatch = true;
					while (isMatch
							&& (counter < currentMethodDeclarationParameters
									.size())) {
						isMatch = argumentIsValidParameter(
								methodInvocationArguments.get(counter),
								currentMethodDeclarationParameters.get(counter),
								mode);
						counter++;
					}
					if (isMatch) {
						return currentMethodDeclaration;
					}
				} else if ((currentMethodDeclarationParameters.size() > 0)
						&& (currentMethodDeclarationParameters.size() < methodInvocationArguments
								.size())
						&& (currentMethodDeclarationParameters
								.get(currentMethodDeclarationParameters.size() - 1) instanceof VariableLengthDeclaration)) {
					int counter = 0;
					boolean isMatch = true;
					ParameterDeclaration currentParameter = null;
					while (isMatch
							&& (counter < methodInvocationArguments.size())) {
						if (counter < currentMethodDeclarationParameters.size()) {
							currentParameter = currentMethodDeclarationParameters
									.get(counter);
						}
						isMatch = argumentIsValidParameter(
								methodInvocationArguments.get(counter),
								currentParameter, mode);
						counter++;
					}
					if (isMatch) {
						return currentMethodDeclaration;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the method / constructor definition that matches invocation by
	 * reflection. Performs argument typechecking which is required due to
	 * overloading.
	 *
	 * @param methodInvocation
	 *            The vertex of the invocation.
	 * @param methodInvocationArguments
	 *            The list of arguments of the invocation.
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified name of the type in which the method /
	 *            constructor is assumed.
	 * @param methodName
	 *            The name of the method.
	 * @param searchMode
	 *            Defines what to search for.
	 * @param mode
	 *            The extraction mode to use.
	 * @return The vertex representing the method's / constructor's definition;
	 *         null if no matching definition could be found.
	 */
	private Member findMatchingDeclarationByReflection(
			MethodInvocation methodInvocation,
			ArrayList<Expression> methodInvocationArguments,
			Type containingType, String methodName,
			InvocationSearchMode searchMode, ExtractionMode mode) {
		if ((searchMode != InvocationSearchMode.methodsOnly) /*
															 * ||
															 * containingType.
															 * getFullyQualifiedName
															 * ().equals(
															 * methodName ) ||
															 * containingType.
															 * getFullyQualifiedName
															 * ().endsWith( "."
															 * + methodName )
															 */) {
			Class<?> externalClass = null;
			try {
				externalClass = Class.forName(containingType
						.get_fullyQualifiedName());
			} catch (Exception exception) {
			}
			if (externalClass != null) {
				ArrayList<ConstructorDefinition> constructorsInType = new ArrayList<ConstructorDefinition>();
				for (java.lang.reflect.Constructor<?> currentConstructor : externalClass
						.getConstructors()) {
					constructorsInType.add(ResolverUtilities
							.createConstructorDefinition(currentConstructor,
									containingType, mode, symbolTable));
				}
				if ((constructorsInType == null)
						|| (constructorsInType.size() == 0)) {
					return null;
				}
				for (ConstructorDefinition currentConstructorDefinition : constructorsInType) {
					ArrayList<ParameterDeclaration> currentConstructorDefinitionParameters = getParametersFromDeclaration(currentConstructorDefinition);
					if (currentConstructorDefinitionParameters.size() == methodInvocationArguments
							.size()) {
						int counter = 0;
						boolean isMatch = true;
						while (isMatch
								&& (counter < currentConstructorDefinitionParameters
										.size())) {
							isMatch = argumentIsValidParameter(
									methodInvocationArguments.get(counter),
									currentConstructorDefinitionParameters
											.get(counter), mode);
							counter++;
						}
						if (isMatch) {
							return currentConstructorDefinition;
						}
					} else if ((currentConstructorDefinitionParameters.size() > 0)
							&& (currentConstructorDefinitionParameters.size() < methodInvocationArguments
									.size())
							&& (currentConstructorDefinitionParameters
									.get(currentConstructorDefinitionParameters
											.size() - 1) instanceof VariableLengthDeclaration)) {
						int counter = 0;
						boolean isMatch = true;
						ParameterDeclaration currentParameter = null;
						while (isMatch
								&& (counter < methodInvocationArguments.size())) {
							if (counter < currentConstructorDefinitionParameters
									.size()) {
								currentParameter = currentConstructorDefinitionParameters
										.get(counter);
							}
							isMatch = argumentIsValidParameter(
									methodInvocationArguments.get(counter),
									currentParameter, mode);
							counter++;
						}
						if (isMatch) {
							return currentConstructorDefinition;
						}
					}
				}
			}
		}
		if (searchMode != InvocationSearchMode.constructorsOnly) {
			Class<?> externalClass = null;
			try {
				externalClass = Class.forName(containingType
						.get_fullyQualifiedName());
			} catch (Exception exception) {
			}
			if (externalClass != null) {
				ArrayList<MethodDeclaration> methodsInType = new ArrayList<MethodDeclaration>();
				for (java.lang.reflect.Method currentMethod : externalClass
						.getMethods()) {
					if (currentMethod.getName().equals(methodName)) {
						methodsInType.add(ResolverUtilities
								.createMethodDeclarationOrDefinition(
										currentMethod, containingType, mode,
										symbolTable));
					}
				}
				if ((methodsInType == null) || (methodsInType.size() == 0)) {
					return null;
				}
				for (MethodDeclaration currentMethodDeclaration : methodsInType) {
					ArrayList<ParameterDeclaration> currentMethodDeclarationParameters = getParametersFromDeclaration(currentMethodDeclaration);
					if (currentMethodDeclarationParameters.size() == methodInvocationArguments
							.size()) {
						int counter = 0;
						boolean isMatch = true;
						while (isMatch
								&& (counter < currentMethodDeclarationParameters
										.size())) {
							isMatch = argumentIsValidParameter(
									methodInvocationArguments.get(counter),
									currentMethodDeclarationParameters
											.get(counter), mode);
							counter++;
						}
						if (isMatch) {
							return currentMethodDeclaration;
						}
					} else if ((currentMethodDeclarationParameters.size() > 0)
							&& (currentMethodDeclarationParameters.size() < methodInvocationArguments
									.size())
							&& (currentMethodDeclarationParameters
									.get(currentMethodDeclarationParameters
											.size() - 1) instanceof VariableLengthDeclaration)) {
						int counter = 0;
						boolean isMatch = true;
						ParameterDeclaration currentParameter = null;
						while (isMatch
								&& (counter < methodInvocationArguments.size())) {
							if (counter < currentMethodDeclarationParameters
									.size()) {
								currentParameter = currentMethodDeclarationParameters
										.get(counter);
							}
							isMatch = argumentIsValidParameter(
									methodInvocationArguments.get(counter),
									currentParameter, mode);
							counter++;
						}
						if (isMatch) {
							return currentMethodDeclaration;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the list of parameters of a constructor.
	 *
	 * @param declaration
	 *            The vertex of the constructor defintion.
	 * @return The list of parameter declarations.
	 */
	private ArrayList<ParameterDeclaration> getParametersFromDeclaration(
			ConstructorDefinition declaration) {
		ArrayList<ParameterDeclaration> result = new ArrayList<ParameterDeclaration>();
		for (IsParameterOfConstructor edge : declaration
				.getIsParameterOfConstructorIncidences(EdgeDirection.IN)) {
			result.add((ParameterDeclaration) edge.getThat());
		}
		// Iterable< EdgeVertexPair< ? extends IsParameterOfConstructor, ?
		// extends Vertex > > isParameterOfConstructorEdges =
		// declaration.getIsParameterOfConstructorIncidences( EdgeDirection.IN
		// );
		// Iterator< EdgeVertexPair< ? extends IsParameterOfConstructor, ?
		// extends Vertex > > edgeIterator =
		// isParameterOfConstructorEdges.iterator();
		// while( edgeIterator.hasNext() )
		// result.add( ( ParameterDeclaration )edgeIterator.next().getVertex()
		// );
		return result;
	}

	/**
	 * Gets the list of parameters of a method.
	 *
	 * @param declaration
	 *            The vertex of the method defintion.
	 * @return The list of parameter declarations.
	 */
	private ArrayList<ParameterDeclaration> getParametersFromDeclaration(
			MethodDeclaration declaration) {
		ArrayList<ParameterDeclaration> result = new ArrayList<ParameterDeclaration>();
		for (IsParameterOfMethod edge : declaration
				.getIsParameterOfMethodIncidences(EdgeDirection.IN)) {
			result.add((ParameterDeclaration) edge.getThat());
		}
		// Iterable< EdgeVertexPair< ? extends IsParameterOfMethod, ? extends
		// Vertex > > isParameterOfMethodEdges =
		// declaration.getIsParameterOfMethodIncidences( EdgeDirection.IN );
		// Iterator< EdgeVertexPair< ? extends IsParameterOfMethod, ? extends
		// Vertex > > edgeIterator = isParameterOfMethodEdges.iterator();
		// while( edgeIterator.hasNext() )
		// result.add( ( ParameterDeclaration )edgeIterator.next().getVertex()
		// );
		return result;
	}

	/**
	 * Checks if an argument is valid for the defined parameter.
	 *
	 * @param argument
	 *            The vertex of the argument.
	 * @param parameter
	 *            The vertex of the parameter declaration.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the argument is valid, false if not.
	 */
	private boolean argumentIsValidParameter(Expression argument,
			ParameterDeclaration parameter, ExtractionMode mode) {
		if (parameter.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN) == null) {
			return false;
		}
		TypeSpecification parameterType = (TypeSpecification) parameter
				.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN)
				.getAlpha();
		if (parameterType instanceof BuiltInType) {
			return isCompatibleToBuiltInType(argument,
					((BuiltInType) parameterType).get_type(), mode);
		} else if (parameterType instanceof QualifiedType) {
			return isCompatibleToQualifiedType(argument,
					(QualifiedType) parameterType, mode);
		} else if (parameterType instanceof ArrayType) {
			return isCompatibleToArrayType(argument, (ArrayType) parameterType,
					mode);
		} else if (parameterType instanceof TypeParameterUsage) {
			return isCompatibleToTypeParameterUsage(argument,
					(TypeParameterUsage) parameterType, mode);
		}
		return false;
	}

	/**
	 * Checks if an expression is compatible to a specified array type.
	 *
	 * @param expression
	 *            The vertex of the expression.
	 * @param typeToMatch
	 *            The vertex of the array type specification.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the expression is compatible, false if not.
	 */
	private boolean isCompatibleToArrayType(Expression expression,
			ArrayType typeToMatch, ExtractionMode mode) {
		if (typeToMatch.getFirstIsElementTypeOfIncidence() == null) {
			return false;
		}
		TypeSpecification elementTypeToMatch = (TypeSpecification) typeToMatch
				.getFirstIsElementTypeOfIncidence().getAlpha();
		if (expression instanceof Null) {
			return true;
		}
		if (expression instanceof FieldAccess) {
			fieldResolver.resolveSingleField(mode, (FieldAccess) expression);
			if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) == null) {
				return false;
			}
			TypeSpecification fieldType = null;
			if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof VariableDeclaration) {
				if (((VariableDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN) == null) {
					return false;
				}
				fieldType = (TypeSpecification) ((VariableDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN)
						.getAlpha();
			} else if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof ParameterDeclaration) {
				if (((ParameterDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN) == null) {
					return false;
				}
				fieldType = (TypeSpecification) ((ParameterDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN)
						.getAlpha();
			} else {
				return false;
			}
			if (fieldType instanceof ArrayType) {
				int accessedDimensions = expression.getDegree(
						IsArrayElementIndexOf.EC, EdgeDirection.IN);
				return isCompatibleArrayType((ArrayType) fieldType,
						typeToMatch, accessedDimensions);
			} else {
				return false;
			}
		}
		if (expression instanceof MethodInvocation) {
			resolveSingleMethod(mode, (MethodInvocation) expression);
			if ((((MethodInvocation) expression)
					.getFirstIsDeclarationOfInvokedMethodIncidence(EdgeDirection.IN) != null)
					&& (((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha() instanceof MethodDeclaration)
					&& (((MethodDeclaration) ((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha())
							.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN) != null)
					&& (((MethodDeclaration) ((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha())
							.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN)
							.getAlpha() instanceof ArrayType)) {
				return isCompatibleArrayType(
						(ArrayType) ((MethodDeclaration) ((MethodInvocation) expression)
								.getFirstIsDeclarationOfInvokedMethodIncidence(
										EdgeDirection.IN).getAlpha())
								.getFirstIsReturnTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			} else {
				return false;
			}
		}
		if (expression instanceof ClassCast) {
			if ((((ClassCast) expression)
					.getFirstIsCastedTypeOfIncidence(EdgeDirection.IN) != null)
					&& (((ClassCast) expression)
							.getFirstIsCastedTypeOfIncidence(EdgeDirection.IN)
							.getAlpha() instanceof ArrayType)) {
				return isCompatibleArrayType(
						(ArrayType) ((ClassCast) expression)
								.getFirstIsCastedTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			} else {
				return false;
			}
		}
		if (expression instanceof ArrayCreation) {
			if (((ArrayCreation) expression)
					.getFirstIsElementTypeOfCreatedArrayIncidence(EdgeDirection.IN) == null) {
				return false;
			}
			int arrayCreationDimensions = expression.getDegree(
					IsDimensionInitializerOf.EC, EdgeDirection.IN);
			if (arrayCreationDimensions != typeToMatch.get_dimensions()) {
				return false;
			}
			if ((elementTypeToMatch instanceof BuiltInType)
					&& (((ArrayCreation) expression)
							.getFirstIsElementTypeOfCreatedArrayIncidence(
									EdgeDirection.IN).getAlpha() instanceof BuiltInType)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((ArrayCreation) expression)
								.getFirstIsElementTypeOfCreatedArrayIncidence(
										EdgeDirection.IN).getAlpha(),
						((BuiltInType) elementTypeToMatch).get_type());
			} else if ((elementTypeToMatch instanceof QualifiedType)
					&& (((ArrayCreation) expression)
							.getFirstIsElementTypeOfCreatedArrayIncidence(
									EdgeDirection.IN).getAlpha() instanceof QualifiedType)) {
				return isCompatibleQualifiedType(
						(QualifiedType) ((ArrayCreation) expression)
								.getFirstIsElementTypeOfCreatedArrayIncidence(
										EdgeDirection.IN).getAlpha(),
						(QualifiedType) elementTypeToMatch);
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if an array type is compatible to another specified array type.
	 *
	 * @param arrayType
	 *            The vertex of the array type.
	 * @param typeToMatch
	 *            The vertex of the array type specification.
	 * @return true if the array types are compatible, false if not.
	 */
	private boolean isCompatibleArrayType(ArrayType arrayType,
			ArrayType typeToMatch) {
		return isCompatibleArrayType(arrayType, typeToMatch, 0);
	}

	/**
	 * Checks if an array type is compatible to another specified array type.
	 *
	 * @param arrayType
	 *            The vertex of the array type.
	 * @param typeToMatch
	 *            The vertex of the array type specification.
	 * @param dimensionsSurplus
	 *            The amount of dimensions that the first array type differs
	 *            from the second
	 * @return true if the array types are compatible, false if not.
	 */
	private boolean isCompatibleArrayType(ArrayType arrayType,
			ArrayType typeToMatch, int dimensionsSurplus) {
		if (arrayType.get_dimensions() != (typeToMatch.get_dimensions() + dimensionsSurplus)) {
			return false;
		}
		if ((arrayType.getFirstIsElementTypeOfIncidence(EdgeDirection.IN) != null)
				&& (typeToMatch
						.getFirstIsElementTypeOfIncidence(EdgeDirection.IN) != null)) {
			if ((typeToMatch.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
					.getAlpha() instanceof BuiltInType)
					&& (arrayType.getFirstIsElementTypeOfIncidence(
							EdgeDirection.IN).getAlpha() instanceof BuiltInType)) {
				return isCompatibleBuiltInType((BuiltInType) arrayType
						.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
						.getAlpha(), ((BuiltInType) typeToMatch
						.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
						.getAlpha()).get_type());
			} else if ((typeToMatch.getFirstIsElementTypeOfIncidence(
					EdgeDirection.IN).getAlpha() instanceof QualifiedType)
					&& (arrayType.getFirstIsElementTypeOfIncidence(
							EdgeDirection.IN).getAlpha() instanceof QualifiedType)) {
				return isCompatibleQualifiedType((QualifiedType) arrayType
						.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
						.getAlpha(), (QualifiedType) typeToMatch
						.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
						.getAlpha());
			}
			return false;
		}
		return false;
	}

	/**
	 * Checks if an expression is compatible to a specified type parameter.
	 *
	 * @param expression
	 *            The vertex of the expression.
	 * @param typeToMatch
	 *            The vertex of the type parameter usage.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the expression is compatible, false if not.
	 */
	private boolean isCompatibleToTypeParameterUsage(Expression expression,
			TypeParameterUsage typeParameterUsage, ExtractionMode mode) {
		if (expression instanceof Null) {
			return true;
		}
		if (expression instanceof StringConstant) {
			return true;
		}
		if (expression instanceof FieldAccess) {
			fieldResolver.resolveSingleField(mode, (FieldAccess) expression);
			if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) == null) {
				return false;
			}
			TypeSpecification fieldType = null;
			if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof VariableDeclaration) {
				if (((VariableDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN) == null) {
					return false;
				}
				fieldType = (TypeSpecification) ((VariableDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN)
						.getAlpha();
			} else if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof ParameterDeclaration) {
				if (((ParameterDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN) == null) {
					return false;
				}
				fieldType = (TypeSpecification) ((ParameterDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN)
						.getAlpha();
			} else {
				return false;
			}
			if (fieldType instanceof ArrayType) {
				return true;
			}
			if (fieldType instanceof QualifiedType) {
				return true;
			}
			if (fieldType instanceof TypeParameterUsage) {
				return true;
			} else {
				return false;
			}
		}
		if (expression instanceof MethodInvocation) {
			resolveSingleMethod(mode, (MethodInvocation) expression);
			if ((((MethodInvocation) expression)
					.getFirstIsDeclarationOfInvokedMethodIncidence(EdgeDirection.IN) != null)
					&& (((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha() instanceof MethodDeclaration)
					&& (((MethodDeclaration) ((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha())
							.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN) != null)
					&& (((MethodDeclaration) ((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha())
							.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN)
							.getAlpha() instanceof QualifiedType)) {
				return true;
			} else {
				return false;
			}
		}
		if (expression instanceof ClassCast) {
			if ((((ClassCast) expression)
					.getFirstIsCastedTypeOfIncidence(EdgeDirection.IN) != null)
					&& (((ClassCast) expression)
							.getFirstIsCastedTypeOfIncidence(EdgeDirection.IN)
							.getAlpha() instanceof QualifiedType)) {
				return true;
			} else {
				return false;
			}
		}
		if (expression instanceof ObjectCreation) {
			if ((((ObjectCreation) expression)
					.getFirstIsTypeOfObjectIncidence(EdgeDirection.IN) != null)
					&& (((ObjectCreation) expression)
							.getFirstIsTypeOfObjectIncidence(EdgeDirection.IN)
							.getAlpha() instanceof QualifiedType)) {
				return true;
			} else {
				return false;
			}
		}
		if (expression instanceof InfixExpression) {
			if ((((InfixExpression) expression).get_operator() == InfixOperators.PLUS)
					&& (((InfixExpression) expression)
							.getFirstIsLeftHandSideOfInfixExpressionIncidence() != null)
					&& (((InfixExpression) expression)
							.getFirstIsRightHandSideOfInfixExpressionIncidence() != null)
					&& isCompatibleToTypeParameterUsage(
							(Expression) ((InfixExpression) expression)
									.getFirstIsLeftHandSideOfInfixExpressionIncidence()
									.getAlpha(), typeParameterUsage, mode)
					&& isCompatibleToTypeParameterUsage(
							(Expression) ((InfixExpression) expression)
									.getFirstIsRightHandSideOfInfixExpressionIncidence()
									.getAlpha(), typeParameterUsage, mode)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if an expression is compatible to a specified qualified type.
	 *
	 * @param expression
	 *            The vertex of the expression.
	 * @param typeToMatch
	 *            The vertex of the qualified type specification.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the expression is compatible, false if not.
	 */
	private boolean isCompatibleToQualifiedType(Expression expression,
			QualifiedType typeToMatch, ExtractionMode mode) {
		if (expression instanceof Null) {
			return true;
		}
		if (expression instanceof StringConstant) {
			if (isCompatibleToStringType(typeToMatch)) {
				return true;
			} else {
				return false;
			}
		}
		if (expression instanceof FieldAccess) {
			fieldResolver.resolveSingleField(mode, (FieldAccess) expression);
			if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) == null) {
				return false;
			}
			TypeSpecification fieldType = null;
			if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof VariableDeclaration) {
				if (((VariableDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN) == null) {
					return false;
				}
				fieldType = (TypeSpecification) ((VariableDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN)
						.getAlpha();
			} else if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof ParameterDeclaration) {
				if (((ParameterDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN) == null) {
					return false;
				}
				fieldType = (TypeSpecification) ((ParameterDeclaration) ((FieldAccess) expression)
						.getFirstIsDeclarationOfAccessedFieldIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN)
						.getAlpha();
			} else if (((FieldAccess) expression)
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha() instanceof Type) {
				return isCompatibleType(
						(Type) ((FieldAccess) expression)
								.getFirstIsDeclarationOfAccessedFieldIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			} else {
				return false;
			}
			if (fieldType instanceof ArrayType) {
				if (ResolverUtilities
						.accessedDimensionsMatchDeclaredDimensions(
								((FieldAccess) expression),
								(ArrayType) fieldType)
						&& (((ArrayType) fieldType)
								.getFirstIsElementTypeOfIncidence(EdgeDirection.IN) != null)) {
					fieldType = (TypeSpecification) ((ArrayType) fieldType)
							.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
							.getAlpha();
				} else {
					return false;
				}
			}
			if (fieldType instanceof QualifiedType) {
				return isCompatibleQualifiedType((QualifiedType) fieldType,
						typeToMatch);
			}
			if (fieldType instanceof TypeParameterUsage) {
				return true;
			} else {
				return false;
			}
		}
		if (expression instanceof MethodInvocation) {
			resolveSingleMethod(mode, (MethodInvocation) expression);
			if ((((MethodInvocation) expression)
					.getFirstIsDeclarationOfInvokedMethodIncidence(EdgeDirection.IN) != null)
					&& (((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha() instanceof MethodDeclaration)
					&& (((MethodDeclaration) ((MethodInvocation) expression)
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha())
							.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN) != null)) {
				TypeSpecification returnType = (TypeSpecification) ((MethodDeclaration) ((MethodInvocation) expression)
						.getFirstIsDeclarationOfInvokedMethodIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN)
						.getAlpha();
				if (returnType instanceof QualifiedType) {
					return isCompatibleQualifiedType(
							(QualifiedType) returnType, typeToMatch);
				}
				if (returnType instanceof TypeParameterUsage) {
					return true;
				}
				return false;
			} else {
				return false;
			}
		}
		if (expression instanceof ClassCast) {
			if (((ClassCast) expression)
					.getFirstIsCastedTypeOfIncidence(EdgeDirection.IN) != null) {
				TypeSpecification castType = (TypeSpecification) ((ClassCast) expression)
						.getFirstIsCastedTypeOfIncidence(EdgeDirection.IN)
						.getAlpha();
				if (castType instanceof QualifiedType) {
					return isCompatibleQualifiedType((QualifiedType) castType,
							typeToMatch);
				}
				if (castType instanceof TypeParameterUsage) {
					return true;
				}
				return false;
			} else {
				return false;
			}
		}
		if (expression instanceof ObjectCreation) {
			if (((ObjectCreation) expression)
					.getFirstIsTypeOfObjectIncidence(EdgeDirection.IN) != null) {
				TypeSpecification objectType = (TypeSpecification) ((ObjectCreation) expression)
						.getFirstIsTypeOfObjectIncidence(EdgeDirection.IN)
						.getAlpha();
				if (objectType instanceof QualifiedType) {
					return isCompatibleQualifiedType(
							(QualifiedType) objectType, typeToMatch);
				}
				if (objectType instanceof TypeParameterUsage) {
					return true;
				}
				return false;
			} else {
				return false;
			}
		}
		if (expression instanceof InfixExpression) {
			if ((((InfixExpression) expression).get_operator() == InfixOperators.PLUS)
					&& isStringType(typeToMatch)
					&& (((InfixExpression) expression)
							.getFirstIsLeftHandSideOfInfixExpressionIncidence() != null)
					&& (((InfixExpression) expression)
							.getFirstIsRightHandSideOfInfixExpressionIncidence() != null)
					&& isCompatibleToQualifiedType(
							(Expression) ((InfixExpression) expression)
									.getFirstIsLeftHandSideOfInfixExpressionIncidence()
									.getAlpha(), typeToMatch, mode)
					&& isCompatibleToQualifiedType(
							(Expression) ((InfixExpression) expression)
									.getFirstIsRightHandSideOfInfixExpressionIncidence()
									.getAlpha(), typeToMatch, mode)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if qualified type is compatible to another qualified type.
	 *
	 * @param arrayType
	 *            The vertex of the first qualified type.
	 * @param typeToMatch
	 *            The vertex of the second qualified type.
	 * @return true if the qualified types are compatible, false if not.
	 */
	private boolean isCompatibleQualifiedType(QualifiedType qualifiedType,
			QualifiedType typeToMatch) {
		if (qualifiedType.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN) == null) {
			return false;
		}
		Type qualifiedTypeDefinition = (Type) qualifiedType
				.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN)
				.getAlpha();
		return isCompatibleType(qualifiedTypeDefinition, typeToMatch);
	}

	/**
	 * Checks if qualified type is compatible to another qualified type.
	 *
	 * @param arrayType
	 *            The vertex of the first qualified type.
	 * @param typeToMatch
	 *            The vertex of the second qualified type.
	 * @return true if the qualified types are compatible, false if not.
	 */
	private boolean isCompatibleType(Type qualifiedTypeDefinition,
			QualifiedType typeToMatch) {
		if (typeToMatch.get_fullyQualifiedName().equals("java.lang.Object")) {
			return true;
		}
		if (typeToMatch.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN) == null) {
			return false;
		}
		Type typeDefinitionToMatch = (Type) typeToMatch
				.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN)
				.getAlpha();
		if (typeDefinitionToMatch.get_fullyQualifiedName().equals(
				"java.lang.Object")) {
			return true;
		}
		if ((qualifiedTypeDefinition == typeDefinitionToMatch)
				|| qualifiedTypeDefinition.get_fullyQualifiedName().equals(
						typeDefinitionToMatch.get_fullyQualifiedName())) {
			return true;
		}
		if (qualifiedTypeDefinition instanceof ClassDefinition) {
			ClassDefinition qualifiedTypeClassDefinition = (ClassDefinition) qualifiedTypeDefinition;
			HashMap<InterfaceDefinition, Object> possibleInterfaceDefinitions = new HashMap<InterfaceDefinition, Object>();
			if (typeDefinitionToMatch instanceof InterfaceDefinition) {
				possibleInterfaceDefinitions.put(
						(InterfaceDefinition) typeDefinitionToMatch, null);
				getPossibleSubInterfaces(
						(InterfaceDefinition) typeDefinitionToMatch,
						possibleInterfaceDefinitions);
				if (implementsInterface(qualifiedTypeClassDefinition,
						possibleInterfaceDefinitions)) {
					return true;
				}
			}
			if ((typeDefinitionToMatch instanceof ClassDefinition)
					|| (typeDefinitionToMatch instanceof InterfaceDefinition)) {
				ClassDefinition currentSuperClass = qualifiedTypeClassDefinition;
				do {
					try {
						currentSuperClass = ResolverUtilities
								.getSuperClass(currentSuperClass);
					} catch (Exception e) {
						return false;
					} // a superclass has been defined, but could not be
						// resolved, there is nothing left to be done here!
					if (currentSuperClass != null) {
						if (currentSuperClass == typeDefinitionToMatch) {
							return true;
						}
						if ((typeDefinitionToMatch instanceof InterfaceDefinition)
								&& implementsInterface(currentSuperClass,
										possibleInterfaceDefinitions)) {
							return true;
						}
					}
				} while (currentSuperClass != null);
			}
		}
		return false;
	}

	/**
	 * Checks if a class implements any of the given interfaces (or one of the
	 * interfaces' extensions).
	 *
	 * @param classDefinition
	 *            The vertex of the class.
	 * @param interfaceDefinitions
	 *            The vertices of the interfaces.
	 * @return true if one of the interfaces is implemented, false if not.
	 */
	private boolean implementsInterface(ClassDefinition classDefinition,
			HashMap<InterfaceDefinition, Object> interfaceDefinitions) {
		for (IsInterfaceOfClass edge : classDefinition
				.getIsInterfaceOfClassIncidences(EdgeDirection.IN)) {
			TypeSpecification currentInterfaceOfClassSpecification = (TypeSpecification) edge
					.getThat();
			;
			if ((currentInterfaceOfClassSpecification
					.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN) != null)
					&& (currentInterfaceOfClassSpecification
							.getFirstIsTypeDefinitionOfIncidence(
									EdgeDirection.IN).getAlpha() instanceof InterfaceDefinition)) {
				InterfaceDefinition currentInterfaceOfClass = (InterfaceDefinition) currentInterfaceOfClassSpecification
						.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN)
						.getAlpha();
				for (InterfaceDefinition def : interfaceDefinitions.keySet()) {
					if (def == currentInterfaceOfClass) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Adds the interfaces that extend a given interface to a given list.
	 *
	 * @param interfaceDefinition
	 *            The vertex of the interface that is extended.
	 * @param interfacesList
	 *            The list of interfaces.
	 */
	private void getPossibleSubInterfaces(
			InterfaceDefinition interfaceDefinition,
			HashMap<InterfaceDefinition, Object> interfacesList) {

	}

	/**
	 * Checks if a qualified type allows a string.
	 *
	 * @param typeToMatch
	 *            The qualified type to check.
	 * @return true if the type allows a string, false if not.
	 */
	private boolean isCompatibleToStringType(QualifiedType typeToMatch) {
		if (typeToMatch.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN) == null) {
			return false;
		}
		String fullyQualifiedName = ((Type) typeToMatch
				.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN)
				.getAlpha()).get_fullyQualifiedName();
		if (fullyQualifiedName.equals("java.lang.String")
				|| fullyQualifiedName.equals("java.lang.Object")
				|| fullyQualifiedName.equals("java.io.Serializable")
				|| fullyQualifiedName.equals("java.lang.CharSequence")
				|| fullyQualifiedName.equals("java.lang.Comparable")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a qualified type is a string.
	 *
	 * @param typeToMatch
	 *            The qualified type to check.
	 * @return true if the type is a string, false if not.
	 */
	private boolean isStringType(QualifiedType typeToMatch) {
		if (typeToMatch.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN) == null) {
			return false;
		}
		String fullyQualifiedName = ((Type) typeToMatch
				.getFirstIsTypeDefinitionOfIncidence(EdgeDirection.IN)
				.getAlpha()).get_fullyQualifiedName();
		if (fullyQualifiedName.equals("java.lang.String")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if an expression is compatible to a specified builtin type.
	 *
	 * @param expression
	 *            The vertex of the expression.
	 * @param typeToMatch
	 *            The vertex of the builtin type specification.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the expression is compatible, false if not.
	 */
	private boolean isCompatibleToBuiltInType(Expression expression,
			BuiltInTypes typeToMatch, ExtractionMode mode) {
		if (typeToMatch == BuiltInTypes.BOOLEAN) {
			if (expression instanceof BooleanConstant) {
				return true;
			}
			if ((expression instanceof BuiltInCast)
					&& (((BuiltInCast) expression)
							.getFirstIsCastedBuiltInTypeOfIncidence(EdgeDirection.IN) != null)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((BuiltInCast) expression)
								.getFirstIsCastedBuiltInTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			}
			if (expression instanceof MethodInvocation) {
				return isCompatibleBuiltInReturnType(
						(MethodInvocation) expression, typeToMatch, mode);
			}
			if (expression instanceof InfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(InfixExpression) expression, typeToMatch);
			}
			if (expression instanceof PrefixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PrefixExpression) expression, typeToMatch);
			}
			if (expression instanceof FieldAccess) {
				return isCompatibleFieldToBuiltInType((FieldAccess) expression,
						typeToMatch, mode);
			}
		} else if ((typeToMatch == BuiltInTypes.BYTE)
				|| (typeToMatch == BuiltInTypes.SHORT)) {
			if (expression instanceof IntegerConstant) {
				return true;
			}
			if ((expression instanceof BuiltInCast)
					&& (((BuiltInCast) expression)
							.getFirstIsCastedBuiltInTypeOfIncidence(EdgeDirection.IN) != null)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((BuiltInCast) expression)
								.getFirstIsCastedBuiltInTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			}
			if (expression instanceof MethodInvocation) {
				return isCompatibleBuiltInReturnType(
						(MethodInvocation) expression, typeToMatch, mode);
			}
			if (expression instanceof InfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(InfixExpression) expression, typeToMatch)
						&& (((InfixExpression) expression)
								.getFirstIsLeftHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsLeftHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode)
						&& (((InfixExpression) expression)
								.getFirstIsRightHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsRightHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PrefixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PrefixExpression) expression, typeToMatch)
						&& (((PrefixExpression) expression)
								.getFirstIsRightHandSideOfPrefixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PrefixExpression) expression)
										.getFirstIsRightHandSideOfPrefixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PostfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PostfixExpression) expression, typeToMatch)
						&& (((PostfixExpression) expression)
								.getFirstIsLeftHandSideOfPostfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PostfixExpression) expression)
										.getFirstIsLeftHandSideOfPostfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof FieldAccess) {
				return isCompatibleFieldToBuiltInType((FieldAccess) expression,
						typeToMatch, mode);
			}
		} else if ((typeToMatch == BuiltInTypes.CHAR)
				|| (typeToMatch == BuiltInTypes.INT)) {
			if ((expression instanceof IntegerConstant)
					|| (expression instanceof CharConstant)) {
				return true;
			}
			if ((expression instanceof BuiltInCast)
					&& (((BuiltInCast) expression)
							.getFirstIsCastedBuiltInTypeOfIncidence(EdgeDirection.IN) != null)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((BuiltInCast) expression)
								.getFirstIsCastedBuiltInTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			}
			if (expression instanceof MethodInvocation) {
				return isCompatibleBuiltInReturnType(
						(MethodInvocation) expression, typeToMatch, mode);
			}
			if (expression instanceof InfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(InfixExpression) expression, typeToMatch)
						&& (((InfixExpression) expression)
								.getFirstIsLeftHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsLeftHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode)
						&& (((InfixExpression) expression)
								.getFirstIsRightHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsRightHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PrefixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PrefixExpression) expression, typeToMatch)
						&& (((PrefixExpression) expression)
								.getFirstIsRightHandSideOfPrefixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PrefixExpression) expression)
										.getFirstIsRightHandSideOfPrefixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PostfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PostfixExpression) expression, typeToMatch)
						&& (((PostfixExpression) expression)
								.getFirstIsLeftHandSideOfPostfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PostfixExpression) expression)
										.getFirstIsLeftHandSideOfPostfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof FieldAccess) {
				return isCompatibleFieldToBuiltInType((FieldAccess) expression,
						typeToMatch, mode);
			}
		} else if (typeToMatch == BuiltInTypes.LONG) {
			if ((expression instanceof IntegerConstant)
					|| (expression instanceof CharConstant)
					|| (expression instanceof LongConstant)) {
				return true;
			}
			if ((expression instanceof BuiltInCast)
					&& (((BuiltInCast) expression)
							.getFirstIsCastedBuiltInTypeOfIncidence(EdgeDirection.IN) != null)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((BuiltInCast) expression)
								.getFirstIsCastedBuiltInTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			}
			if (expression instanceof MethodInvocation) {
				return isCompatibleBuiltInReturnType(
						(MethodInvocation) expression, typeToMatch, mode);
			}
			if (expression instanceof InfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(InfixExpression) expression, typeToMatch)
						&& (((InfixExpression) expression)
								.getFirstIsLeftHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsLeftHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode)
						&& (((InfixExpression) expression)
								.getFirstIsRightHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsRightHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PrefixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PrefixExpression) expression, typeToMatch)
						&& (((PrefixExpression) expression)
								.getFirstIsRightHandSideOfPrefixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PrefixExpression) expression)
										.getFirstIsRightHandSideOfPrefixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PostfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PostfixExpression) expression, typeToMatch)
						&& (((PostfixExpression) expression)
								.getFirstIsLeftHandSideOfPostfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PostfixExpression) expression)
										.getFirstIsLeftHandSideOfPostfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof FieldAccess) {
				return isCompatibleFieldToBuiltInType((FieldAccess) expression,
						typeToMatch, mode);
			}
		} else if (typeToMatch == BuiltInTypes.FLOAT) {
			if ((expression instanceof IntegerConstant)
					|| (expression instanceof CharConstant)
					|| (expression instanceof LongConstant)
					|| (expression instanceof FloatConstant)) {
				return true;
			}
			if ((expression instanceof BuiltInCast)
					&& (((BuiltInCast) expression)
							.getFirstIsCastedBuiltInTypeOfIncidence(EdgeDirection.IN) != null)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((BuiltInCast) expression)
								.getFirstIsCastedBuiltInTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			}
			if (expression instanceof MethodInvocation) {
				return isCompatibleBuiltInReturnType(
						(MethodInvocation) expression, typeToMatch, mode);
			}
			if (expression instanceof InfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(InfixExpression) expression, typeToMatch)
						&& (((InfixExpression) expression)
								.getFirstIsLeftHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsLeftHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode)
						&& (((InfixExpression) expression)
								.getFirstIsRightHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsRightHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PrefixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PrefixExpression) expression, typeToMatch)
						&& (((PrefixExpression) expression)
								.getFirstIsRightHandSideOfPrefixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PrefixExpression) expression)
										.getFirstIsRightHandSideOfPrefixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PostfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PostfixExpression) expression, typeToMatch)
						&& (((PostfixExpression) expression)
								.getFirstIsLeftHandSideOfPostfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PostfixExpression) expression)
										.getFirstIsLeftHandSideOfPostfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof FieldAccess) {
				return isCompatibleFieldToBuiltInType((FieldAccess) expression,
						typeToMatch, mode);
			}
		} else if (typeToMatch == BuiltInTypes.DOUBLE) {
			if ((expression instanceof IntegerConstant)
					|| (expression instanceof CharConstant)
					|| (expression instanceof LongConstant)
					|| (expression instanceof FloatConstant)
					|| (expression instanceof DoubleConstant)) {
				return true;
			}
			if ((expression instanceof BuiltInCast)
					&& (((BuiltInCast) expression)
							.getFirstIsCastedBuiltInTypeOfIncidence(EdgeDirection.IN) != null)) {
				return isCompatibleBuiltInType(
						(BuiltInType) ((BuiltInCast) expression)
								.getFirstIsCastedBuiltInTypeOfIncidence(
										EdgeDirection.IN).getAlpha(),
						typeToMatch);
			}
			if (expression instanceof MethodInvocation) {
				return isCompatibleBuiltInReturnType(
						(MethodInvocation) expression, typeToMatch, mode);
			}
			if (expression instanceof InfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(InfixExpression) expression, typeToMatch)
						&& (((InfixExpression) expression)
								.getFirstIsLeftHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsLeftHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode)
						&& (((InfixExpression) expression)
								.getFirstIsRightHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((InfixExpression) expression)
										.getFirstIsRightHandSideOfInfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PrefixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PrefixExpression) expression, typeToMatch)
						&& (((PrefixExpression) expression)
								.getFirstIsRightHandSideOfPrefixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PrefixExpression) expression)
										.getFirstIsRightHandSideOfPrefixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof PostfixExpression) {
				return isCompatibleOperatorToBuiltInType(
						(PostfixExpression) expression, typeToMatch)
						&& (((PostfixExpression) expression)
								.getFirstIsLeftHandSideOfPostfixExpressionIncidence(EdgeDirection.IN) != null)
						&& isCompatibleToBuiltInType(
								(Expression) ((PostfixExpression) expression)
										.getFirstIsLeftHandSideOfPostfixExpressionIncidence(
												EdgeDirection.IN).getAlpha(),
								typeToMatch, mode);
			}
			if (expression instanceof FieldAccess) {
				return isCompatibleFieldToBuiltInType((FieldAccess) expression,
						typeToMatch, mode);
			}
		}
		return false;
	}

	/**
	 * Checks if an accessed field is compatible to a specified builtin type.
	 *
	 * @param fieldAccess
	 *            The field access.
	 * @param typeToMatch
	 *            The vertex of the builtin type specification.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the field's type is compatible, false if not.
	 */
	private boolean isCompatibleFieldToBuiltInType(FieldAccess fieldAccess,
			BuiltInTypes typeToMatch, ExtractionMode mode) {
		fieldResolver.resolveSingleField(mode, fieldAccess);
		if (fieldAccess
				.getFirstIsDeclarationOfAccessedFieldIncidence(EdgeDirection.IN) == null) {
			return false;
		}
		TypeSpecification fieldType = null;
		if (fieldAccess.getFirstIsDeclarationOfAccessedFieldIncidence(
				EdgeDirection.IN).getAlpha() instanceof VariableDeclaration) {
			if (((VariableDeclaration) fieldAccess
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha())
					.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN) == null) {
				return false;
			}
			fieldType = (TypeSpecification) ((VariableDeclaration) fieldAccess
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha())
					.getFirstIsTypeOfVariableIncidence(EdgeDirection.IN)
					.getAlpha();
		} else if (fieldAccess.getFirstIsDeclarationOfAccessedFieldIncidence(
				EdgeDirection.IN).getAlpha() instanceof ParameterDeclaration) {
			if (((ParameterDeclaration) fieldAccess
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha())
					.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN) == null) {
				return false;
			}
			fieldType = (TypeSpecification) ((ParameterDeclaration) fieldAccess
					.getFirstIsDeclarationOfAccessedFieldIncidence(
							EdgeDirection.IN).getAlpha())
					.getFirstIsTypeOfParameterIncidence(EdgeDirection.IN)
					.getAlpha();
		} else {
			return false;
		}
		if (fieldType instanceof ArrayType) {
			if (ResolverUtilities.accessedDimensionsMatchDeclaredDimensions(
					fieldAccess, (ArrayType) fieldType)
					&& (((ArrayType) fieldType)
							.getFirstIsElementTypeOfIncidence(EdgeDirection.IN) != null)) {
				fieldType = (TypeSpecification) ((ArrayType) fieldType)
						.getFirstIsElementTypeOfIncidence(EdgeDirection.IN)
						.getAlpha();
			} else {
				return false;
			}
		}
		if (fieldType instanceof BuiltInType) {
			return isCompatibleBuiltInType((BuiltInType) fieldType, typeToMatch);
		}
		return false;
	}

	/**
	 * Checks if a method's return type is compatible to a specified builtin
	 * type.
	 *
	 * @param methodInvocation
	 *            The method invocation.
	 * @param typeToMatch
	 *            The vertex of the builtin type specification.
	 * @param mode
	 *            The extraction mode to use.
	 * @return true if the return type is compatible, false if not.
	 */
	private boolean isCompatibleBuiltInReturnType(
			MethodInvocation methodInvocation, BuiltInTypes typeToMatch,
			ExtractionMode mode) {
		resolveSingleMethod(mode, methodInvocation);
		if ((methodInvocation
				.getFirstIsDeclarationOfInvokedMethodIncidence(EdgeDirection.IN) != null)
				&& (methodInvocation
						.getFirstIsDeclarationOfInvokedMethodIncidence(
								EdgeDirection.IN).getAlpha() instanceof MethodDeclaration)
				&& (((MethodDeclaration) methodInvocation
						.getFirstIsDeclarationOfInvokedMethodIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN) != null)
				&& (((MethodDeclaration) methodInvocation
						.getFirstIsDeclarationOfInvokedMethodIncidence(
								EdgeDirection.IN).getAlpha())
						.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN)
						.getAlpha() instanceof BuiltInType)) {
			return isCompatibleBuiltInType(
					(BuiltInType) ((MethodDeclaration) methodInvocation
							.getFirstIsDeclarationOfInvokedMethodIncidence(
									EdgeDirection.IN).getAlpha())
							.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN)
							.getAlpha(), typeToMatch);
		}
		return false;
	}

	/**
	 * Checks if a builtin type is compatible to another specified builtin type.
	 *
	 * @param type
	 *            The first builtin type.
	 * @param typeToMatch
	 *            The second builtin type.
	 * @return true if the types are compatible, false if not.
	 */
	private boolean isCompatibleBuiltInType(BuiltInType type,
			BuiltInTypes typeToMatch) {
		if (typeToMatch == BuiltInTypes.BOOLEAN) {
			return type.get_type() == BuiltInTypes.BOOLEAN;
		} else if (typeToMatch == BuiltInTypes.BYTE) {
			return type.get_type() == BuiltInTypes.BYTE;
		} else if (typeToMatch == BuiltInTypes.CHAR) {
			return (type.get_type() == BuiltInTypes.CHAR)
					|| (type.get_type() == BuiltInTypes.BYTE);
		} else if (typeToMatch == BuiltInTypes.SHORT) {
			return (type.get_type() == BuiltInTypes.SHORT)
					|| (type.get_type() == BuiltInTypes.BYTE);
		} else if (typeToMatch == BuiltInTypes.INT) {
			return (type.get_type() == BuiltInTypes.INT)
					|| (type.get_type() == BuiltInTypes.BYTE)
					|| (type.get_type() == BuiltInTypes.CHAR)
					|| (type.get_type() == BuiltInTypes.SHORT);
		} else if (typeToMatch == BuiltInTypes.LONG) {
			return (type.get_type() == BuiltInTypes.LONG)
					|| (type.get_type() == BuiltInTypes.BYTE)
					|| (type.get_type() == BuiltInTypes.CHAR)
					|| (type.get_type() == BuiltInTypes.SHORT)
					|| (type.get_type() == BuiltInTypes.INT);
		} else if (typeToMatch == BuiltInTypes.FLOAT) {
			return (type.get_type() == BuiltInTypes.FLOAT)
					|| (type.get_type() == BuiltInTypes.BYTE)
					|| (type.get_type() == BuiltInTypes.CHAR)
					|| (type.get_type() == BuiltInTypes.SHORT)
					|| (type.get_type() == BuiltInTypes.INT)
					|| (type.get_type() == BuiltInTypes.LONG);
		} else if (typeToMatch == BuiltInTypes.DOUBLE) {
			return (type.get_type() == BuiltInTypes.DOUBLE)
					|| (type.get_type() == BuiltInTypes.BYTE)
					|| (type.get_type() == BuiltInTypes.CHAR)
					|| (type.get_type() == BuiltInTypes.SHORT)
					|| (type.get_type() == BuiltInTypes.INT)
					|| (type.get_type() == BuiltInTypes.LONG)
					|| (type.get_type() == BuiltInTypes.FLOAT);
		}
		return false;
	}

	/**
	 * Checks if an infix expression's operator is compatible to a specified
	 * builtin type.
	 *
	 * @param infixExpression
	 *            The infix expression.
	 * @param typeToMatch
	 *            The vertex of the builtin type specification.
	 * @return true if the operator is compatible, false if not.
	 */
	private boolean isCompatibleOperatorToBuiltInType(
			InfixExpression infixExpression, BuiltInTypes typeToMatch) {
		if (typeToMatch == BuiltInTypes.BOOLEAN) {
			return (infixExpression.get_operator() == InfixOperators.EQUALS)
					|| (infixExpression.get_operator() == InfixOperators.AND)
					|| (infixExpression.get_operator() == InfixOperators.SHORTCIRCUITAND)
					|| (infixExpression.get_operator() == InfixOperators.OR)
					|| (infixExpression.get_operator() == InfixOperators.SHORTCIRCUITOR)
					|| (infixExpression.get_operator() == InfixOperators.UNEQUALS)
					|| (infixExpression.get_operator() == InfixOperators.XOR)
					|| (infixExpression.get_operator() == InfixOperators.GREATER)
					|| (infixExpression.get_operator() == InfixOperators.GREATEREQUALS)
					|| (infixExpression.get_operator() == InfixOperators.LESS)
					|| (infixExpression.get_operator() == InfixOperators.LESSEQUALS)
					|| (infixExpression.get_operator() == InfixOperators.INSTANCEOF);
		} else if ((typeToMatch == BuiltInTypes.BYTE)
				|| (typeToMatch == BuiltInTypes.CHAR)
				|| (typeToMatch == BuiltInTypes.SHORT)
				|| (typeToMatch == BuiltInTypes.INT)
				|| (typeToMatch == BuiltInTypes.LONG)) {
			return (infixExpression.get_operator() == InfixOperators.PLUS)
					|| (infixExpression.get_operator() == InfixOperators.MINUS)
					|| (infixExpression.get_operator() == InfixOperators.MULTIPLICATION)
					|| (infixExpression.get_operator() == InfixOperators.DIVISION)
					|| (infixExpression.get_operator() == InfixOperators.AND)
					|| (infixExpression.get_operator() == InfixOperators.OR)
					|| (infixExpression.get_operator() == InfixOperators.XOR)
					|| (infixExpression.get_operator() == InfixOperators.MODULO)
					|| (infixExpression.get_operator() == InfixOperators.LEFTSHIFT)
					|| (infixExpression.get_operator() == InfixOperators.RIGHTSHIFT)
					|| (infixExpression.get_operator() == InfixOperators.UNSIGNEDRIGHTSHIFT);
		} else if ((typeToMatch == BuiltInTypes.FLOAT)
				|| (typeToMatch == BuiltInTypes.DOUBLE)) {
			return (infixExpression.get_operator() == InfixOperators.PLUS)
					|| (infixExpression.get_operator() == InfixOperators.MINUS)
					|| (infixExpression.get_operator() == InfixOperators.MULTIPLICATION)
					|| (infixExpression.get_operator() == InfixOperators.DIVISION)
					|| (infixExpression.get_operator() == InfixOperators.MODULO);
		}
		return false;
	}

	/**
	 * Checks if a prefix expression's operator is compatible to a specified
	 * builtin type.
	 *
	 * @param prefixExpression
	 *            The prefix expression.
	 * @param typeToMatch
	 *            The vertex of the builtin type specification.
	 * @return true if the operator is compatible, false if not.
	 */
	private boolean isCompatibleOperatorToBuiltInType(
			PrefixExpression prefixExpression, BuiltInTypes typeToMatch) {
		if (typeToMatch == BuiltInTypes.BOOLEAN) {
			return (prefixExpression.get_operator() == PrefixOperators.NOT);
		} else if ((typeToMatch == BuiltInTypes.BYTE)
				|| (typeToMatch == BuiltInTypes.CHAR)
				|| (typeToMatch == BuiltInTypes.SHORT)
				|| (typeToMatch == BuiltInTypes.INT)
				|| (typeToMatch == BuiltInTypes.LONG)) {
			return (prefixExpression.get_operator() == PrefixOperators.PLUS)
					|| (prefixExpression.get_operator() == PrefixOperators.MINUS)
					|| (prefixExpression.get_operator() == PrefixOperators.BITWISECOMPLEMENT)
					|| (prefixExpression.get_operator() == PrefixOperators.INCREMENT)
					|| (prefixExpression.get_operator() == PrefixOperators.DECREMENT);
		} else if ((typeToMatch == BuiltInTypes.FLOAT)
				|| (typeToMatch == BuiltInTypes.DOUBLE)) {
			return (prefixExpression.get_operator() == PrefixOperators.PLUS)
					|| (prefixExpression.get_operator() == PrefixOperators.MINUS)
					|| (prefixExpression.get_operator() == PrefixOperators.INCREMENT)
					|| (prefixExpression.get_operator() == PrefixOperators.DECREMENT);
		}
		return false;
	}

	/**
	 * Checks if a postfix expression's operator is compatible to a specified
	 * builtin type.
	 *
	 * @param postfixExpression
	 *            The postfix expression.
	 * @param typeToMatch
	 *            The vertex of the builtin type specification.
	 * @return true if the operator is compatible, false if not.
	 */
	private boolean isCompatibleOperatorToBuiltInType(
			PostfixExpression postfixExpression, BuiltInTypes typeToMatch) {
		if ((typeToMatch == BuiltInTypes.BYTE)
				|| (typeToMatch == BuiltInTypes.CHAR)
				|| (typeToMatch == BuiltInTypes.SHORT)
				|| (typeToMatch == BuiltInTypes.INT)
				|| (typeToMatch == BuiltInTypes.LONG)
				|| (typeToMatch == BuiltInTypes.FLOAT)
				|| (typeToMatch == BuiltInTypes.DOUBLE)) {
			return true;
		}
		return false;
	}

	/**
	 * Creates the semantic edge between an invocation and it's definition. Also
	 * assures that identical identifiers for other accesses to this field exist
	 * only once per file.
	 *
	 * @param methodInvocation
	 *            The vertex of the invocation.
	 * @param declaration
	 *            The vertex of the method's / constructor's definition.
	 * @param scope
	 *            The scope of the invocation vertex.
	 * @param methodName
	 *            The name of the method / constructor.
	 * @return true (always; this is for code reduction whereever this function
	 *         is used, because if it has been executed, an invocation
	 *         definitely has been resolved).
	 */
	private boolean linkMethodInvocationToDeclaration(
			MethodInvocation methodInvocation, Member declaration,
			Vertex scope, String methodName) {
		Type supremeTypeOfMethodInvocation = ResolverUtilities
				.getSupremeTypeFromScope(scope, symbolTable);
		if ((supremeTypeOfMethodInvocation != null)
				&& (methodInvocation
						.getFirstIsNameOfInvokedMethodIncidence(EdgeDirection.IN) != null)) {
			Type supremeTypeOfCurrentMethodInvocation = null;
			MethodInvocation currentMethodInvocation = null;
			boolean foundIdenticalIdentifier = false;
			for (Edge edge : declaration.incidences(
					IsDeclarationOfInvokedMethod.EC, EdgeDirection.OUT)) {
				if (foundIdenticalIdentifier) {
					break;
				}
				currentMethodInvocation = (MethodInvocation) edge.getThat();
				if ((currentMethodInvocation
						.getFirstIsNameOfInvokedMethodIncidence(EdgeDirection.IN) != null)
						&& (((Type) currentMethodInvocation
								.getFirstIsNameOfInvokedMethodIncidence(
										EdgeDirection.IN).getAlpha()).get_name()
								.equals(methodName))) {
					supremeTypeOfCurrentMethodInvocation = ResolverUtilities
							.getSupremeTypeFromScope(
									symbolTable
											.getScopeOfMethodInvocation(currentMethodInvocation),
									symbolTable);
					if ((supremeTypeOfCurrentMethodInvocation != null)
							&& (supremeTypeOfCurrentMethodInvocation == supremeTypeOfMethodInvocation)) {
						IsNameOfInvokedMethod edgeToReattach = methodInvocation
								.getFirstIsNameOfInvokedMethodIncidence(EdgeDirection.IN);
						Vertex identifierToDelete = edgeToReattach.getAlpha();
						edgeToReattach.setAlpha(currentMethodInvocation
								.getFirstIsNameOfInvokedMethodIncidence(
										EdgeDirection.IN).getAlpha());
						identifierToDelete.delete();
						foundIdenticalIdentifier = true;
					}
				}
			}
			// Iterable< EdgeVertexPair< ? extends IsDeclarationOfInvokedMethod,
			// ? extends Vertex > > isDeclarationOfInvokedMethodEdges = new
			// IncidenceIterable< IsDeclarationOfInvokedMethod, Vertex >(
			// declaration, IsDeclarationOfInvokedMethod.class,
			// EdgeDirection.OUT );
			// Iterator< EdgeVertexPair< ? extends IsDeclarationOfInvokedMethod,
			// ? extends Vertex > > edgeIterator =
			// isDeclarationOfInvokedMethodEdges.iterator();
			// while( edgeIterator.hasNext() && !foundIdenticalIdentifier ){
			// currentMethodInvocation = ( MethodInvocation
			// )edgeIterator.next().getVertex();
			// if( ( currentMethodInvocation.getFirstIsNameOfInvokedMethod(
			// EdgeDirection.IN ) != null ) && ( ( ( Identifier
			// )currentMethodInvocation.getFirstIsNameOfInvokedMethod(
			// EdgeDirection.IN ).getAlpha() ).getName().equals( methodName ) )
			// ){
			// supremeTypeOfCurrentMethodInvocation =
			// ResolverUtilities.getSupremeTypeFromScope(
			// symbolTable.getScopeOfMethodInvocation( currentMethodInvocation
			// ), symbolTable );
			// if( ( supremeTypeOfCurrentMethodInvocation != null ) && (
			// supremeTypeOfCurrentMethodInvocation ==
			// supremeTypeOfMethodInvocation ) ){
			// IsNameOfInvokedMethod edgeToReattach =
			// methodInvocation.getFirstIsNameOfInvokedMethod( EdgeDirection.IN
			// );
			// Vertex identifierToDelete = edgeToReattach.getAlpha();
			// edgeToReattach.setAlpha( ( Identifier
			// )currentMethodInvocation.getFirstIsNameOfInvokedMethod(
			// EdgeDirection.IN ).getAlpha() );
			// identifierToDelete.delete();
			// foundIdenticalIdentifier = true;
			// }
			// }
			// }
		}
		/* IsDeclarationOfInvokedMethod linkingEdge = */symbolTable.getGraph()
				.createIsDeclarationOfInvokedMethod(declaration,
						methodInvocation);
		symbolTable.setMethodInvocationProcessed(methodInvocation);
		symbolTable.increaseAmountOfMethodInvocationsTreatedByResolver();
		if ((methodProgressBar != null)
				&& ((symbolTable
						.getAmountOfMethodInvocationsTreatedByResolver() % methodProgressBar
						.getUpdateInterval()) == 0)) {
			methodProgressBar.progress(1);
		}
		return true;
	}

	/**
	 * Triggers all required actions if an invocation could not be resolved at
	 * all.
	 *
	 * @param methodInvocation
	 *            The vertex of the invocation.
	 * @return false (always; this is for code reduction whereever this function
	 *         is used, because if it has been executed, an invocation
	 *         definitely has not been resolved).
	 */
	private boolean finishUnresolvedMethodInvocation(
			MethodInvocation methodInvocation) {
		symbolTable.setMethodInvocationProcessed(methodInvocation);
		symbolTable.increaseAmountOfMethodInvocationsTreatedByResolver();
		symbolTable.increaseAmountOfUnresolvedMethodInvocations();
		if ((methodProgressBar != null)
				&& ((symbolTable
						.getAmountOfMethodInvocationsTreatedByResolver() % methodProgressBar
						.getUpdateInterval()) == 0)) {
			methodProgressBar.progress(1);
		}
		return false;
	}
}
