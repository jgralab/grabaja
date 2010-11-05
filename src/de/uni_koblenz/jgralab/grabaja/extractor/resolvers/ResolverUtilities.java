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

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.ExtractionMode;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.extractor.factories.ModifierFactory;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayType;
import de.uni_koblenz.jgralab.grabaja.java5schema.AttributedEdge;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInTypes;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Field;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.ImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArrayElementIndexOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsImportedTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPartOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeDefinitionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterDeclarationNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsUpperBoundOfTypeParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.Member;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifiers;
import de.uni_koblenz.jgralab.grabaja.java5schema.ObjectCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;

/**
 * Implements helper methods for resolvers.
 * 
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class ResolverUtilities {

	/**
	 * Returns vertex representing direct enclosing type definition of given
	 * vertex representing a scope.
	 * 
	 * @param scope
	 *            Vertex representing a scope.
	 * @param symbolTable
	 *            Symbol table to use.
	 * @return Vertex representing enclosing type; null if no vertex can be
	 *         found.
	 */
	public static Type getEnclosingTypeFromScope(Vertex scope,
			SymbolTable symbolTable) {
		if (scope != null) {
			Vertex tempScope = scope;
			while (!(tempScope instanceof ClassDefinition)
					&& !(tempScope instanceof InterfaceDefinition)
					&& !(tempScope instanceof EnumDefinition)
					&& !(tempScope instanceof AnnotationDefinition)
					&& (symbolTable.getParentScope(tempScope) != null)) {
				tempScope = symbolTable.getParentScope(tempScope);
			}
			if ((tempScope != null)
					&& ((tempScope instanceof ClassDefinition)
							|| (tempScope instanceof InterfaceDefinition)
							|| (tempScope instanceof EnumDefinition) || (tempScope instanceof AnnotationDefinition))) {
				return (Type) tempScope;
			}
			return null;
		}
		return null;
	}

	/**
	 * Returns vertex representing direct enclosing class definition of an given
	 * vertex representing a scope.
	 * 
	 * @param scope
	 *            Vertex representing a scope.
	 * @param symbolTable
	 *            Symbol table to use.
	 * @return Vertex representing enclosing class; null if no vertex can be
	 *         found.
	 */
	public static ClassDefinition getEnclosingClassFromScope(Vertex scope,
			SymbolTable symbolTable) {
		Vertex enclosingType = getEnclosingTypeFromScope(scope, symbolTable);
		if ((enclosingType != null)
				&& (enclosingType instanceof ClassDefinition)) {
			return (ClassDefinition) enclosingType;
		}
		return null;
	}

	/**
	 * Returns vertex representing definition of superclass of given vertex
	 * representing a class.
	 * 
	 * @param classDefinition
	 *            Vertex representing a class definition.
	 * @exception Exception
	 *                Is thrown if there is a superclass specified, but the
	 *                according type specification is unresolved.
	 * @return Vertex representing class definition superclass; null if there is
	 *         no superclass.
	 */
	// @TODO implement a more designative exception
	public static ClassDefinition getSuperClass(ClassDefinition classDefinition)
			throws Exception {
		if (classDefinition != null) {
			if (classDefinition.getFirstIsSuperClassOfClass(EdgeDirection.IN) == null) {
				return null;
			}
			TypeSpecification superTypeSpecificationVertex = (TypeSpecification) classDefinition
					.getFirstIsSuperClassOfClass(EdgeDirection.IN).getAlpha();
			IsTypeDefinitionOf isTypeDefinitionOfEdge = superTypeSpecificationVertex
					.getFirstIsTypeDefinitionOf(EdgeDirection.IN);
			if (isTypeDefinitionOfEdge != null) {
				Type superTypeDefinitionVertex = (Type) isTypeDefinitionOfEdge
						.getAlpha();
				if (superTypeDefinitionVertex instanceof ClassDefinition) {
					return (ClassDefinition) superTypeDefinitionVertex;
				}
			}
			throw new Exception();
			// old version of if clause:
			// if( ( isTypeDefinitionOfEdge == null ) || !(
			// isTypeDefinitionOfEdge.getAlpha() instanceof ClassDefinition ) )
			// throw new Exception();
			// return ( ClassDefinition
			// )superTypeSpec.getFirstIsTypeDefinitionOf( EdgeDirection.IN
			// ).getAlpha();
		}
		return null;
	}

	/**
	 * Returns vertex representing first type definition in same file (topmost
	 * enclosing type) given vertex represents a scope in.
	 * 
	 * @param scope
	 *            Vertex representing a scope.
	 * @param symbolTable
	 *            Symbol table to use.
	 * @return Vertex representing first type definition in same file; null if
	 *         no such element can be found.
	 */
	public static Type getSupremeTypeFromScope(Vertex scope,
			SymbolTable symbolTable) {
		if (scope != null) {
			Vertex tempScope = scope;
			while (symbolTable.getParentScope(tempScope) != null) {
				tempScope = symbolTable.getParentScope(tempScope);
			}
			if ((tempScope != null) && (tempScope instanceof Type)) {
				return (Type) tempScope;
			}
		}
		return null;
	}

	/**
	 * Returns fully qualified name of package the type represented by given
	 * vertex belongs to.
	 * 
	 * @param type
	 *            Vertex representing first type definition in a file.
	 * @return Fully qualified name of package; empty string if no package has
	 *         been defined in file or null if no such element can be found or
	 *         if type is not first in file.
	 */
	public static String getPackageNameFromSupremeType(Type type) {
		if (type != null) {
			if (type.getFirstIsExternalDeclarationIn(EdgeDirection.OUT) == null) {
				return null; // @TODO throw an exception instead
			}
			SourceUsage sourceUsageVertex = (SourceUsage) type
					.getFirstIsExternalDeclarationIn(EdgeDirection.OUT)
					.getOmega();
			TranslationUnit translationUnitVertex = (TranslationUnit) sourceUsageVertex
					.getFirstIsSourceUsageIn(EdgeDirection.OUT).getOmega();
			IsPartOf isPartOfEdge = translationUnitVertex
					.getFirstIsPartOf(EdgeDirection.OUT);
			if (isPartOfEdge != null) {
				JavaPackage javaPackageVertex = (JavaPackage) isPartOfEdge
						.getOmega();
				return javaPackageVertex.get_fullyQualifiedName();
			}
			return "";
		}
		return null;
	}

	/**
	 * Gets defined imports.
	 * 
	 * @param type
	 *            The topmost type in the file.
	 * @return The list of import definition vertices; empty list if no imports
	 *         have been defined; null if no such element can be found or if
	 *         type is not the topmost type.
	 */
	public static ArrayList<ImportDefinition> getImportsFromSupremeType(
			Type type) {
		if (type != null) {
			if (type.getFirstIsExternalDeclarationIn(EdgeDirection.OUT) == null) {
				return null; // @TODO throw an exception instead
			}
			ArrayList<ImportDefinition> result = new ArrayList<ImportDefinition>();
			SourceUsage sourceUsageVertex = (SourceUsage) type
					.getFirstIsExternalDeclarationIn(EdgeDirection.OUT)
					.getOmega();
			for (IsExternalDeclarationIn edge : sourceUsageVertex
					.getIsExternalDeclarationInIncidences(EdgeDirection.IN)) {
				Vertex vertex = edge.getThat();
				if (vertex instanceof ImportDefinition) {
					result.add((ImportDefinition) vertex);
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
			// if( vertex instanceof ImportDefinition ) result.add( (
			// ImportDefinition )vertex );
			// }
			return result;
		}
		return null;
	}

	/**
	 * Gets the type definition vertex of a field's / variable's / parameter's
	 * type.
	 * 
	 * @param fieldDeclaration
	 *            The declaration vertex of the field / variable / parameter.
	 * @param fieldAccess
	 *            The vertex of the access to the field / variable / parameter.
	 *            Required to check if the accessed type is the type of an
	 *            element or an array's dimension if the defined field is an
	 *            array.
	 * @return The vertex of the type definition; null if the specified type has
	 *         not been resolved, is a builtin type or an array type.
	 */
	public static Type getTypeFromFieldDeclaration(
			FieldDeclaration fieldDeclaration, FieldAccess fieldAccess) {
		TypeSpecification fieldTypeSpecification = null;
		if (fieldDeclaration instanceof ParameterDeclaration) {
			if (((ParameterDeclaration) fieldDeclaration)
					.getFirstIsTypeOfParameter(EdgeDirection.IN) == null) {
				return null;
			}
			fieldTypeSpecification = (TypeSpecification) ((ParameterDeclaration) fieldDeclaration)
					.getFirstIsTypeOfParameter(EdgeDirection.IN).getAlpha();
		} else if (fieldDeclaration instanceof VariableDeclaration) {
			if (((VariableDeclaration) fieldDeclaration)
					.getFirstIsTypeOfVariable(EdgeDirection.IN) == null) {
				return null;
			}
			fieldTypeSpecification = (TypeSpecification) ((VariableDeclaration) fieldDeclaration)
					.getFirstIsTypeOfVariable(EdgeDirection.IN).getAlpha();
		} else {
			// As this obviously is a enum constant declaration, there is
			// nothing to be resolved here as enum constants cannot contain
			// anything.
			// However, this usually should not happen. ;-)
			return null;
		}
		if (fieldTypeSpecification instanceof ArrayType) {
			if (!accessedDimensionsMatchDeclaredDimensions(fieldAccess,
					(ArrayType) fieldTypeSpecification)) {
				// this means the field access does not point to one of the
				// array's elements, but to the array itself (or to one of the
				// subarrays for
				// multidimensional arrays). In this case there is nothing to be
				// done here as fields of an array (like arr.length; not(!) an
				// element
				// of an array!) can be neither queried nor reflected (arrays
				// are a bit of "compiler magic")...
				return null;
			}
			if (((ArrayType) fieldTypeSpecification)
					.getFirstIsElementTypeOf(EdgeDirection.IN) != null) {
				fieldTypeSpecification = (TypeSpecification) ((ArrayType) fieldTypeSpecification)
						.getFirstIsElementTypeOf(EdgeDirection.IN).getAlpha();
			} else {
				return null; // unlikely but nevertheless for stability
				// reasons...
			}
		}
		if (fieldTypeSpecification instanceof BuiltInType) {
			return null;
		}
		if (fieldTypeSpecification.getFirstIsTypeDefinitionOf(EdgeDirection.IN) == null) {
			return null;
		}
		return (Type) fieldTypeSpecification.getFirstIsTypeDefinitionOf(
				EdgeDirection.IN).getAlpha();
	}

	/**
	 * Gets the type definition vertex of an invocated method's return type.
	 * 
	 * @param methodInvocation
	 *            The invocation of the method.
	 * @param mode
	 *            The extraction mode to use.
	 * @param methodResolver
	 *            The method resolver to use for resolving of the invocation.
	 * @return The vertex of the type definition; null if the invocated method
	 *         could not be resolved, returns an array or a builtin type, is a
	 *         constructor, or the specified return type could not be resolved.
	 */
	public static Type getReturnTypeFromMethodInvocation(
			MethodInvocation methodInvocation, ExtractionMode mode,
			MethodResolver methodResolver) {
		methodResolver.resolveSingleMethod(mode, methodInvocation);
		if (methodInvocation
				.getFirstIsDeclarationOfInvokedMethod(EdgeDirection.IN) == null) {
			return null;
		}
		Vertex methodInvocationDeclaration = methodInvocation
				.getFirstIsDeclarationOfInvokedMethod(EdgeDirection.IN)
				.getAlpha();
		if (methodInvocationDeclaration instanceof MethodDeclaration) {
			if (((MethodDeclaration) methodInvocationDeclaration)
					.getFirstIsReturnTypeOf(EdgeDirection.IN) == null) {
				return null;
			}
			TypeSpecification methodInvocationDeclarationReturnTypeSpec = (TypeSpecification) ((MethodDeclaration) methodInvocationDeclaration)
					.getFirstIsReturnTypeOf(EdgeDirection.IN).getAlpha();
			if ((methodInvocationDeclarationReturnTypeSpec instanceof ArrayType)
					|| (methodInvocationDeclarationReturnTypeSpec instanceof BuiltInType)) {
				return null;
			}
			if (methodInvocationDeclarationReturnTypeSpec
					.getFirstIsTypeDefinitionOf(EdgeDirection.IN) == null) {
				return null;
			}
			return (Type) methodInvocationDeclarationReturnTypeSpec
					.getFirstIsTypeDefinitionOf(EdgeDirection.IN).getAlpha();
		}
		return null;
	}

	/**
	 * Gets the type definition vertex of casted type.
	 * 
	 * @param classCast
	 *            The class cast vertex.
	 * @return The vertex of the casted type's definition; null if the specified
	 *         cast type is unresolved, an array or a builtin type.
	 */
	public static Type getCastedTypeFromClassCast(ClassCast classCast) {
		if (classCast == null) {
			return null;
		}
		if (classCast.getFirstIsCastedTypeOf(EdgeDirection.IN) == null) {
			return null;
		}
		TypeSpecification classCastTypeSpecification = (TypeSpecification) classCast
				.getFirstIsCastedTypeOf(EdgeDirection.IN).getAlpha();
		if ((classCastTypeSpecification instanceof ArrayType)
				|| (classCastTypeSpecification instanceof BuiltInType)) {
			return null;
		}
		if (classCastTypeSpecification
				.getFirstIsTypeDefinitionOf(EdgeDirection.IN) == null) {
			return null;
		}
		return (Type) classCastTypeSpecification.getFirstIsTypeDefinitionOf(
				EdgeDirection.IN).getAlpha();
	}

	/**
	 * Gets the type definition vertex of an instantiated object's type.
	 * 
	 * @param objectCreation
	 *            The object creation vertex.
	 * @return The vertex of the instantiated object's type definition; null if
	 *         the specified type is unresolved.
	 */
	public static Type getTypeFromObjectCreation(ObjectCreation objectCreation) {
		if (objectCreation == null) {
			return null;
		}
		if (objectCreation.getFirstIsTypeOfObject(EdgeDirection.IN) == null) {
			return null;
		}
		TypeSpecification objectCreationTypeSpecification = (TypeSpecification) objectCreation
				.getFirstIsTypeOfObject(EdgeDirection.IN).getAlpha();
		if (objectCreationTypeSpecification
				.getFirstIsTypeDefinitionOf(EdgeDirection.IN) == null) {
			return null;
		}
		return (Type) objectCreationTypeSpecification
				.getFirstIsTypeDefinitionOf(EdgeDirection.IN).getAlpha();
	}

	/**
	 * Checks if a field access points to an element of a specified array.
	 * 
	 * @param fieldAccess
	 *            The vertex representing the access to the array.
	 * @param declaration
	 *            The accessed array's type specification.
	 * @return true if the access points to an element of the array; false if it
	 *         points to a dimension of it.
	 */
	public static boolean accessedDimensionsMatchDeclaredDimensions(
			FieldAccess fieldAccess, ArrayType declaration) {
		int accessedDimensions = fieldAccess.getDegree(
				IsArrayElementIndexOf.class, EdgeDirection.IN);
		// Iterable< EdgeVertexPair< ? extends IsArrayElementIndexOf,? extends
		// Vertex > > arrayAccessDimensionEdges =
		// fieldAccess.getIsArrayElementIndexOfIncidences( EdgeDirection.IN );
		// Iterator< EdgeVertexPair< ? extends IsArrayElementIndexOf,? extends
		// Vertex > > edgeIterator = arrayAccessDimensionEdges.iterator();
		// while( edgeIterator.hasNext() ){
		// edgeIterator.next();
		// accessedDimensions++;
		// }
		if (accessedDimensions == declaration.get_dimensions()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns fully qualified name of an imported type or package.
	 * 
	 * @param importDefinitionVertex
	 *            Vertex representing import definition.
	 * @return Fully qualified name of imported type or package.
	 */
	public static String getFullyQualifiedName(
			ImportDefinition importDefinitionVertex) {
		IsImportedTypeOf isImportedTypeOfEdge = importDefinitionVertex
				.getFirstIsImportedTypeOf();
		if (isImportedTypeOfEdge != null) {
			QualifiedName qualifiedNameVertex = (QualifiedName) isImportedTypeOfEdge
					.getAlpha();
			return qualifiedNameVertex.get_fullyQualifiedName();
		}
		return "";
	}
	
	public static String getNameOfAccessedField(FieldAccess fieldAccessVertex){
		IsFieldNameOf isFieldNameOfEdge = fieldAccessVertex.getFirstIsFieldNameOf(EdgeDirection.IN);
		if(isFieldNameOfEdge != null){
			Identifier identifierVertex = (Identifier)isFieldNameOfEdge.getAlpha();
			return identifierVertex.get_name();
		}
		else
			return "";
	}
	
	public static void deleteWithIdentifier(FieldAccess fieldAccessVertex){
		IsFieldNameOf isFieldNameOfEdge = fieldAccessVertex.getFirstIsFieldNameOf(EdgeDirection.IN);
		if(isFieldNameOfEdge != null){
			Identifier identifierVertex = (Identifier)isFieldNameOfEdge.getAlpha();
			identifierVertex.delete();
		}
		fieldAccessVertex.delete();
	}

	/**
	 * Creates the modifiers of an element created by reflection
	 * 
	 * @param modifierMask
	 *            The appropriate modifiers reflected from the element.
	 * @param parentVertex
	 *            Vertex to which to attach the modifiers.
	 * @param symbolTable
	 *            Symbol table to be used.
	 */
	public static void createAndAttachModifiers(int modifierMask,
			Vertex parentVertex, SymbolTable symbolTable) {
		if (java.lang.reflect.Modifier.isPublic(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.PUBLIC, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isProtected(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.PROTECTED, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isPrivate(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.PRIVATE, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isAbstract(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.ABSTRACT, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isFinal(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.FINAL, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isVolatile(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.VOLATILE, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isNative(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.NATIVE, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isStatic(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.STATIC, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isStrict(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.STRICTFP, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isSynchronized(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.SYNCHRONIZED, symbolTable), parentVertex);
		}
		if (java.lang.reflect.Modifier.isTransient(modifierMask)) {
			ModifierFactory.attachModifier(ModifierFactory.createModifier(
					Modifiers.TRANSIENT, symbolTable), parentVertex);
		}
	}

	/**
	 * Gets the block of a type. If it doesn't exist, it is created first.
	 * 
	 * @param type
	 *            The vertex of the type.
	 * @param symbolTable
	 *            The symbol table to be used.
	 * @return The vertex of the block.
	 */
	public static Block getOrCreateTypeBlock(Type type, SymbolTable symbolTable) {
		if (type.getFirstIsBlockOf(EdgeDirection.IN) != null) {
			return (Block) type.getFirstIsBlockOf(EdgeDirection.IN).getAlpha();
		} else {
			Block typeBlock = symbolTable.getGraph().createBlock();
			symbolTable.addScopeInfo(typeBlock, type);
			if (type instanceof ClassDefinition) {
				Utilities.fillEdgeAttributesWithGivenValue(
						symbolTable.getGraph().createIsClassBlockOf(typeBlock,
								(ClassDefinition) type), -1);
			} else if (type instanceof InterfaceDefinition) {
				Utilities.fillEdgeAttributesWithGivenValue(
						symbolTable.getGraph().createIsInterfaceBlockOf(
								typeBlock, (InterfaceDefinition) type), -1);
			} else if (type instanceof EnumDefinition) {
				Utilities.fillEdgeAttributesWithGivenValue(
						symbolTable.getGraph().createIsEnumBlockOf(typeBlock,
								(EnumDefinition) type), -1);
			} else if (type instanceof AnnotationDefinition) {
				Utilities.fillEdgeAttributesWithGivenValue(
						symbolTable.getGraph().createIsAnnotationBlockOf(
								typeBlock, (AnnotationDefinition) type), -1);
			}
			return typeBlock;
		}
	}

	/**
	 * Creates a type specification of a reflected type.
	 * 
	 * @param type
	 *            The reflected type.
	 * @param symbolTable
	 *            The symbol table to be used.
	 * @return The appropriate type specification.
	 */
	public static TypeSpecification createTypeSpecification(Class<?> type,
			SymbolTable symbolTable, ExtractionMode mode) {
		if (type.isArray()) {
			ArrayType arrayType = symbolTable.getGraph().createArrayType();
			Class<?> currentComponentType = type.getComponentType();
			int arrayDimensions = 1;
			while (currentComponentType.isArray()) {
				currentComponentType = currentComponentType.getComponentType();
				arrayDimensions++;
			}
			arrayType.set_dimensions(arrayDimensions);
			TypeSpecification elementType = createTypeSpecification(
					currentComponentType, symbolTable, mode);
			Utilities.fillEdgeAttributesWithGivenValue(symbolTable.getGraph()
					.createIsElementTypeOf(elementType, arrayType), -1);
			return arrayType;
		} else if (type.isPrimitive()) {
			BuiltInTypes builtInType = null;
			if (type.getName().equals("void")) {
				builtInType = BuiltInTypes.VOID;
			} else if (type.getName().equals("boolean")) {
				builtInType = BuiltInTypes.BOOLEAN;
			} else if (type.getName().equals("byte")) {
				builtInType = BuiltInTypes.BYTE;
			} else if (type.getName().equals("char")) {
				builtInType = BuiltInTypes.CHAR;
			} else if (type.getName().equals("short")) {
				builtInType = BuiltInTypes.SHORT;
			} else if (type.getName().equals("int")) {
				builtInType = BuiltInTypes.INT;
			} else if (type.getName().equals("long")) {
				builtInType = BuiltInTypes.LONG;
			} else if (type.getName().equals("float")) {
				builtInType = BuiltInTypes.FLOAT;
			} else if (type.getName().equals("double")) {
				builtInType = BuiltInTypes.DOUBLE;
			}
			if (symbolTable.hasBuiltInType(builtInType)) {
				return symbolTable.getBuiltInType(builtInType);
			}
			BuiltInType builtInTypeVertex = symbolTable.getGraph()
					.createBuiltInType();
			builtInTypeVertex.set_type(builtInType);
			symbolTable.addBuiltInType(builtInType, builtInTypeVertex);
			return builtInTypeVertex;
		} else {
			if (symbolTable.hasResolvedTypeSpecification(type.getName())) {
				return symbolTable.getResolvedTypeSpecification(type.getName());
			}
			QualifiedType qualifiedTypeVertex = getQualifiedType(
					type.getName(), mode, symbolTable);
			return qualifiedTypeVertex;
		}
	}

	public static boolean createTypeUsingReflection(String fullyQualifiedName,
			ExtractionMode mode, SymbolTable symbolTable) {
		try {
			if (!symbolTable.hasTypeDefinition(fullyQualifiedName)) {
				Class<?> classNotInGraph = Class.forName(fullyQualifiedName);
				createType(classNotInGraph, mode, symbolTable);
			}
			return true;
		} catch (ClassNotFoundException exception) {
			return false;
		}
		// @TODO find out why Exception in thread "main"
		// java.lang.NoClassDefFoundError: de/uni_koblenz/jgralab/GRAPH (wrong
		// name: de/uni_koblenz/jgralab/Graph)
		catch (NoClassDefFoundError error) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static Type createType(
			@SuppressWarnings("rawtypes") java.lang.Class classNotInGraph,
			ExtractionMode mode, SymbolTable symbolTable) {
		Type typeVertex = null;
		Java5 programGraph = symbolTable.getGraph();
		String fullyQualifiedName = classNotInGraph.getName();
		if (classNotInGraph.isInterface()) {
			InterfaceDefinition interfaceDefinitionVertex = programGraph
					.createInterfaceDefinition();
			interfaceDefinitionVertex
					.set_fullyQualifiedName(fullyQualifiedName);
			// interfaceDefinitionVertex.setExternal( true );
			typeVertex = interfaceDefinitionVertex;
		} else if (classNotInGraph.isAnnotation()) {
			AnnotationDefinition annotationDefinitionVertex = programGraph
					.createAnnotationDefinition();
			annotationDefinitionVertex
					.set_fullyQualifiedName(fullyQualifiedName);
			// annotationDefinitionVertex.setExternal( true );
			typeVertex = annotationDefinitionVertex;
		} else if (classNotInGraph.isEnum()) {
			EnumDefinition enumDefinitionVertex = programGraph
					.createEnumDefinition();
			enumDefinitionVertex.set_fullyQualifiedName(fullyQualifiedName);
			// enumDefinitionVertex.setExternal( true );
			typeVertex = enumDefinitionVertex;
			if (mode == ExtractionMode.COMPLETE) {
				createAndAttachEnumConstants(
						classNotInGraph.getEnumConstants(), typeVertex,
						symbolTable);
			}
		} else { // is a class
			ClassDefinition classDefinitionVertex = programGraph
					.createClassDefinition();
			classDefinitionVertex.set_fullyQualifiedName(fullyQualifiedName);
			classDefinitionVertex.set_name(classNotInGraph.getSimpleName());
			// classDefinitionVertex.setExternal( true );
			typeVertex = classDefinitionVertex;
		}
		typeVertex.set_external(true);
		symbolTable.addTypeDefinition(fullyQualifiedName, typeVertex);

		if (mode == ExtractionMode.COMPLETE) {
			createAndAttachModifiers(classNotInGraph.getModifiers(),
					typeVertex, symbolTable);
			createAndAttachIdentifier(classNotInGraph.getSimpleName(),
					typeVertex, symbolTable);
			createAndAttachTypeParameters(classNotInGraph.getTypeParameters(),
					typeVertex, symbolTable);
			createAndAttachSuperType(classNotInGraph.getSuperclass(),
					typeVertex, symbolTable);
			createAndAttachInterfaces(classNotInGraph.getInterfaces(),
					typeVertex, symbolTable);
			// @TODO getPackages and create them
			if (!classNotInGraph.isEnum() && !classNotInGraph.isAnnotation()) {
				createAndAttachFields(classNotInGraph.getFields(), typeVertex,
						mode, symbolTable);
			}
			// a bit ugly, but as there is no "classNotInGraph.isClass()", this
			// is the only way...
			if (!classNotInGraph.isEnum() && !classNotInGraph.isAnnotation()
					&& !classNotInGraph.isInterface()) {
				createAndAttachMethods(classNotInGraph.getMethods(),
						typeVertex, mode, symbolTable);
				createAndAttachConstructors(classNotInGraph.getConstructors(),
						typeVertex, mode, symbolTable);
			}
		}
		return typeVertex;
	}

	/**
	 * Reflection
	 */
	private static void createAndAttachIdentifier(String name,
			Type parentVertex, SymbolTable symbolTable) {
		AttributedEdge edgeWithAttributesToFill = null;
		Java5 programGraph = symbolTable.getGraph();
		Identifier identifierVertex = programGraph.createIdentifier();
		identifierVertex.set_name(name);
		if (parentVertex instanceof ClassDefinition) {
			edgeWithAttributesToFill = programGraph.createIsClassNameOf(
					identifierVertex, (ClassDefinition) parentVertex);
		} else if (parentVertex instanceof InterfaceDefinition) {
			edgeWithAttributesToFill = programGraph.createIsInterfaceNameOf(
					identifierVertex, (InterfaceDefinition) parentVertex);
		} else if (parentVertex instanceof EnumDefinition) {
			edgeWithAttributesToFill = programGraph.createIsEnumNameOf(
					identifierVertex, (EnumDefinition) parentVertex);
		} else if (parentVertex instanceof AnnotationDefinition) {
			edgeWithAttributesToFill = programGraph
					.createIsAnnotationDefinitionNameOf(identifierVertex,
							(AnnotationDefinition) parentVertex);
		}
		if (edgeWithAttributesToFill != null) {
			Utilities.fillEdgeAttributesWithGivenValue(
					edgeWithAttributesToFill, -1);
		}
	}

	private static void createAndAttachIdentifier(String name,
			Member parentVertex, SymbolTable symbolTable) {
		AttributedEdge edgeWithAttributesToFill = null;
		Java5 programGraph = symbolTable.getGraph();
		Identifier identifierVertex = programGraph.createIdentifier();
		identifierVertex.set_name(name);
		if (parentVertex instanceof ConstructorDefinition) {
			edgeWithAttributesToFill = programGraph.createIsNameOfConstructor(
					identifierVertex, (ConstructorDefinition) parentVertex);
		} else if (parentVertex instanceof MethodDeclaration) {
			edgeWithAttributesToFill = programGraph.createIsNameOfMethod(
					identifierVertex, (MethodDeclaration) parentVertex);
		} else if (parentVertex instanceof EnumConstant) {
			edgeWithAttributesToFill = programGraph.createIsEnumConstantNameOf(
					identifierVertex, (EnumConstant) parentVertex);
		}
		if (edgeWithAttributesToFill != null) {
			Utilities.fillEdgeAttributesWithGivenValue(
					edgeWithAttributesToFill, -1);
		}
	}

	private static QualifiedType getQualifiedType(String fullyQualifiedName,
			ExtractionMode mode, SymbolTable symbolTable) {
		if (symbolTable.hasResolvedTypeSpecification(fullyQualifiedName)) {
			return symbolTable.getResolvedTypeSpecification(fullyQualifiedName);
		}
		QualifiedType qualifiedTypeVertex = symbolTable.getGraph()
				.createQualifiedType();
		qualifiedTypeVertex.set_fullyQualifiedName(fullyQualifiedName);
		if (symbolTable.hasTypeDefinition(fullyQualifiedName)
				|| createTypeUsingReflection(fullyQualifiedName, mode,
						symbolTable)) {
			Type typeVertex = symbolTable.getTypeDefinition(fullyQualifiedName);
			symbolTable.getGraph().createIsTypeDefinitionOf(typeVertex,
					qualifiedTypeVertex);
			symbolTable.addResolvedTypeSpecification(qualifiedTypeVertex);
		} else {
			symbolTable.addUnresolvedTypeSpecificationAfterParsing(null,
					qualifiedTypeVertex);
		}
		return qualifiedTypeVertex;
	}

	private static void createAndAttachTypeParameters(
			TypeVariable<Class<?>>[] typeParameters, Type typeVertex,
			SymbolTable symbolTable) {
		if (typeParameters.length < 0) {
			return;
		}
		Java5 programGraph = symbolTable.getGraph();
		for (TypeVariable<Class<?>> typeParameter : typeParameters) {
			// @TODO make sure this type paramter is used if a field in this
			// class has to be resolved -> for this symbol table has to be
			// extended
			TypeParameterDeclaration typeParameterDeclarationVertex = programGraph
					.createTypeParameterDeclaration();
			java.lang.reflect.Type[] upperBounds = typeParameter.getBounds();
			for (java.lang.reflect.Type upperBound : upperBounds) {
				QualifiedType qualifiedTypeVertex = getQualifiedType(upperBound
						.getClass().getName(), ExtractionMode.COMPLETE,
						symbolTable);
				IsUpperBoundOfTypeParameter isUpperBoundOfTypeParameterEdge = programGraph
						.createIsUpperBoundOfTypeParameter(qualifiedTypeVertex,
								typeParameterDeclarationVertex);
				Utilities.fillEdgeAttributesWithGivenValue(
						isUpperBoundOfTypeParameterEdge, -1);
			}
			Identifier identifierVertex = programGraph.createIdentifier();
			identifierVertex.set_name(typeParameter.getName());
			typeParameterDeclarationVertex.set_fullyQualifiedName(typeParameter
					.getName());
			typeParameterDeclarationVertex.set_name(typeParameter.getName());
			typeParameterDeclarationVertex.set_external(true);
			IsTypeParameterDeclarationNameOf isTypeParameterDeclarationNameOfEdge = programGraph
					.createIsTypeParameterDeclarationNameOf(identifierVertex,
							typeParameterDeclarationVertex);
			Utilities.fillEdgeAttributesWithGivenValue(
					isTypeParameterDeclarationNameOfEdge, -1);
			if (typeVertex instanceof ClassDefinition) {
				IsTypeParameterOfClass isTypeParameterOfClassEdge = programGraph
						.createIsTypeParameterOfClass(
								typeParameterDeclarationVertex,
								(ClassDefinition) typeVertex);
				Utilities.fillEdgeAttributesWithGivenValue(
						isTypeParameterOfClassEdge, -1);
			} else if (typeVertex instanceof InterfaceDefinition) {
				IsTypeParameterOfInterface isTypeParameterOfInterfaceEdge = programGraph
						.createIsTypeParameterOfInterface(
								typeParameterDeclarationVertex,
								(InterfaceDefinition) typeVertex);
				Utilities.fillEdgeAttributesWithGivenValue(
						isTypeParameterOfInterfaceEdge, -1);
			}
		}
	}

	private static void createAndAttachInterfaces(
			java.lang.Class<?>[] interfaces, Type typeVertex,
			SymbolTable symbolTable) {
		if (interfaces.length < 1) {
			return;
		}
		for (Class<?> interface1 : interfaces) {
			QualifiedType qualifiedTypeVertex = getQualifiedType(
					interface1.getName(), ExtractionMode.COMPLETE, symbolTable);
			AttributedEdge edgeWithAttributesToFill = null;
			if (typeVertex instanceof ClassDefinition) {
				edgeWithAttributesToFill = symbolTable.getGraph()
						.createIsInterfaceOfClass(qualifiedTypeVertex,
								(ClassDefinition) typeVertex);
			} else if (typeVertex instanceof InterfaceDefinition) {
				edgeWithAttributesToFill = symbolTable.getGraph()
						.createIsSuperClassOfInterface(qualifiedTypeVertex,
								(InterfaceDefinition) typeVertex);
			} else if (typeVertex instanceof EnumDefinition) {
				edgeWithAttributesToFill = symbolTable.getGraph()
						.createIsInterfaceOfEnum(qualifiedTypeVertex,
								(EnumDefinition) typeVertex);
			}
			if (edgeWithAttributesToFill != null) {
				Utilities.fillEdgeAttributesWithGivenValue(
						edgeWithAttributesToFill, -1);
			}
		}
	}

	private static void createAndAttachSuperType(java.lang.Class<?> superType,
			Type typeVertex, SymbolTable symbolTable) {
		if ((superType == null) || (typeVertex instanceof InterfaceDefinition)) {
			return;
		}
		QualifiedType qualifiedTypeVertex = getQualifiedType(
				superType.getName(), ExtractionMode.COMPLETE, symbolTable);
		IsSuperClassOf isSuperClassOfEdge = null;
		if (typeVertex instanceof ClassDefinition) {
			isSuperClassOfEdge = symbolTable.getGraph()
					.createIsSuperClassOfClass(qualifiedTypeVertex,
							(ClassDefinition) typeVertex);
		}
		if (isSuperClassOfEdge != null) {
			Utilities.fillEdgeAttributesWithGivenValue(isSuperClassOfEdge, -1);
		}
	}

	/**
	 * Creates a graph representation for all the constructors of a reflected
	 * type.
	 * 
	 * @param constructors
	 *            The reflected constructors.
	 * @param typeVertex
	 *            The vertex of the type.
	 * @param mode
	 *            The extraction mode to use.
	 */
	private static void createAndAttachConstructors(
			java.lang.reflect.Constructor<?>[] constructors, Type typeVertex,
			ExtractionMode mode, SymbolTable symbolTable) {
		if ((constructors != null) && (typeVertex != null)) {
			for (java.lang.reflect.Constructor<?> currentConstructor : constructors) {
				createConstructorDefinition(currentConstructor, typeVertex,
						mode, symbolTable);
			}
		}
	}

	/**
	 * Creates a graph representation for a constructor of a reflected type.
	 * 
	 * @param constructor
	 *            The reflected constructor.
	 * @param containingType
	 *            The vertex of the type.
	 * @param mode
	 *            The extraction mode to use.
	 * @return The created vertex.
	 */
	protected static ConstructorDefinition createConstructorDefinition(
			java.lang.reflect.Constructor<?> constructor, Type containingType,
			ExtractionMode mode, SymbolTable symbolTable) {
		Block typeBlock = getOrCreateTypeBlock(containingType, symbolTable);
		Java5 programGraph = symbolTable.getGraph();
		ConstructorDefinition constructorDefinitionVertex = programGraph
				.createConstructorDefinition();
		Utilities.fillEdgeAttributesWithGivenValue(programGraph
				.createIsMemberOf(constructorDefinitionVertex, typeBlock), -1);
		createAndAttachIdentifier(constructor.getName(),
				constructorDefinitionVertex, symbolTable);
		createAndAttachModifiers(constructor.getModifiers(),
				constructorDefinitionVertex, symbolTable);
		TypeSpecification currentExceptionType = null;
		for (Class<?> currentException : constructor.getExceptionTypes()) {
			currentExceptionType = createTypeSpecification(currentException,
					symbolTable, mode);
			Utilities.fillEdgeAttributesWithGivenValue(programGraph
					.createIsExceptionThrownByConstructor(currentExceptionType,
							constructorDefinitionVertex), -1);
		}
		TypeSpecification currentParameterType = null;
		ParameterDeclaration currentParameterDeclaration = null;
		Class<?>[] parameters = constructor.getParameterTypes();
		for (int counter = 0; counter < parameters.length; counter++) {
			if (((counter + 1) == parameters.length) && constructor.isVarArgs()) {
				currentParameterDeclaration = programGraph
						.createVariableLengthDeclaration();
			} else {
				currentParameterDeclaration = programGraph
						.createParameterDeclaration();
			}
			currentParameterType = createTypeSpecification(parameters[counter],
					symbolTable, mode);
			Utilities.fillEdgeAttributesWithGivenValue(programGraph
					.createIsTypeOfParameter(currentParameterType,
							currentParameterDeclaration), -1);
			createAndAttachModifiers(parameters[counter].getModifiers(),
					currentParameterDeclaration, symbolTable);
			Utilities.fillEdgeAttributesWithGivenValue(programGraph
					.createIsParameterOfConstructor(
							currentParameterDeclaration,
							constructorDefinitionVertex), -1);
		}
		// @TODO: Annotations, TypeParameters
		symbolTable.addConstructorDefinition(
				containingType.get_fullyQualifiedName(),
				constructorDefinitionVertex);
		return constructorDefinitionVertex;
	}

	/**
	 * Creates a graph representation for all the methods of a reflected type.
	 * 
	 * @param methods
	 *            The reflected methods.
	 * @param typeVertex
	 *            The vertex of the type.
	 * @param mode
	 *            The extraction mode to use.
	 */
	public static void createAndAttachMethods(
			java.lang.reflect.Method[] methods, Type typeVertex,
			ExtractionMode mode, SymbolTable symbolTable) {
		if ((methods != null) && (typeVertex != null)) {
			for (java.lang.reflect.Method currentMethod : methods) {
				createMethodDeclarationOrDefinition(currentMethod, typeVertex,
						mode, symbolTable);
			}
		}
	}

	/**
	 * Creates a graph representation for a method of a reflected type.
	 * 
	 * @param method
	 *            The reflected method.
	 * @param containingType
	 *            The vertex of the type.
	 * @param mode
	 *            The extraction mode to use.
	 * @return The created vertex.
	 */
	protected static MethodDeclaration createMethodDeclarationOrDefinition(
			java.lang.reflect.Method method, Type containingType,
			ExtractionMode mode, SymbolTable symbolTable) {
		Block typeBlock = getOrCreateTypeBlock(containingType, symbolTable);
		MethodDeclaration methodDeclarationVertex = null;
		Java5 programGraph = symbolTable.getGraph();
		if ((containingType instanceof InterfaceDefinition)
				|| (java.lang.reflect.Modifier
						.isAbstract(method.getModifiers()))) {
			methodDeclarationVertex = programGraph.createMethodDeclaration();
		} else {
			methodDeclarationVertex = programGraph.createMethodDefinition();
		}
		Utilities.fillEdgeAttributesWithGivenValue(programGraph
				.createIsMemberOf(methodDeclarationVertex, typeBlock), -1);
		createAndAttachIdentifier(method.getName(), methodDeclarationVertex,
				symbolTable);
		createAndAttachModifiers(method.getModifiers(),
				methodDeclarationVertex, symbolTable);
		TypeSpecification returnType = createTypeSpecification(
				method.getReturnType(), symbolTable, mode);
		Utilities.fillEdgeAttributesWithGivenValue(programGraph
				.createIsReturnTypeOf(returnType, methodDeclarationVertex), -1);
		TypeSpecification currentExceptionType = null;
		for (Class<?> currentException : method.getExceptionTypes()) {
			currentExceptionType = createTypeSpecification(currentException,
					symbolTable, mode);
			Utilities.fillEdgeAttributesWithGivenValue(programGraph
					.createIsExceptionThrownByMethod(currentExceptionType,
							methodDeclarationVertex), -1);
		}
		TypeSpecification currentParameterType = null;
		ParameterDeclaration currentParameterDeclaration = null;
		Class<?>[] parameters = method.getParameterTypes();
		for (int counter = 0; counter < parameters.length; counter++) {
			if (((counter + 1) == parameters.length) && method.isVarArgs()) {
				currentParameterDeclaration = programGraph
						.createVariableLengthDeclaration();
			} else {
				currentParameterDeclaration = programGraph
						.createParameterDeclaration();
			}
			currentParameterType = createTypeSpecification(parameters[counter],
					symbolTable, mode);
			Utilities.fillEdgeAttributesWithGivenValue(programGraph
					.createIsTypeOfParameter(currentParameterType,
							currentParameterDeclaration), -1);
			createAndAttachModifiers(parameters[counter].getModifiers(),
					currentParameterDeclaration, symbolTable);
			Utilities.fillEdgeAttributesWithGivenValue(programGraph
					.createIsParameterOfMethod(currentParameterDeclaration,
							methodDeclarationVertex), -1);
		}
		// @TODO: Annotations, TypeParameters
		symbolTable.addMethodDeclaration(
				containingType.get_fullyQualifiedName(),
				methodDeclarationVertex);
		return methodDeclarationVertex;
	}

	/**
	 * Creates a graph representation for all the enum constants of a reflected
	 * type.
	 * 
	 * @param enumConstants
	 *            The reflected enum constants.
	 * @param typeVertex
	 *            The vertex of the type.
	 */
	public static void createAndAttachEnumConstants(Object[] enumConstants,
			Type typeVertex, SymbolTable symbolTable) {
		if ((enumConstants != null) && (typeVertex != null)) {
			for (Object currentEnumConstant : enumConstants) {
				createEnumConstant(currentEnumConstant, typeVertex, symbolTable);
			}
		}
	}

	/**
	 * Creates a graph representation for an enum constant of a reflected type.
	 * 
	 * @param enumConstant
	 *            The reflected enum constant.
	 * @param containingType
	 *            The vertex of the type.
	 * @return The created vertex.
	 */
	protected static EnumConstant createEnumConstant(Object enumConstant,
			Type containingType, SymbolTable symbolTable) {
		Block typeBlock = getOrCreateTypeBlock(containingType, symbolTable);
		Java5 programGraph = symbolTable.getGraph();
		EnumConstant enumConstantVertex = programGraph.createEnumConstant();
		createAndAttachIdentifier(enumConstant.toString(), enumConstantVertex,
				symbolTable);
		Utilities.fillEdgeAttributesWithGivenValue(
				programGraph.createIsMemberOf(enumConstantVertex, typeBlock),
				-1);
		symbolTable.addEnumConstant(containingType.get_fullyQualifiedName(),
				enumConstantVertex);
		return enumConstantVertex;
	}

	/**
	 * Creates a graph representation for all the fields of a reflected type.
	 * 
	 * @param fields
	 *            The reflected fields.
	 * @param typeVertex
	 *            The vertex of the type.
	 * @param mode
	 *            The extraction mode to use.
	 */
	public static void createAndAttachFields(java.lang.reflect.Field[] fields,
			Type typeVertex, ExtractionMode mode, SymbolTable symbolTable) {
		if ((fields != null) && (typeVertex != null)) {
			for (java.lang.reflect.Field currentField : fields) {
				createFieldDeclaration(currentField, typeVertex, mode,
						symbolTable);
			}
		}
	}

	/**
	 * Creates a graph representation for a field of a reflected type.
	 * 
	 * @param field
	 *            The reflected field.
	 * @param containingType
	 *            The vertex of the type.
	 * @param mode
	 *            The extraction mode to use.
	 * @return The created vertex.
	 */
	protected static FieldDeclaration createFieldDeclaration(
			java.lang.reflect.Field field, Type containingType,
			ExtractionMode mode, SymbolTable symbolTable) {
		Block typeBlock = getOrCreateTypeBlock(containingType, symbolTable);
		Java5 programGraph = symbolTable.getGraph();
		Field fieldDeclarationVertex = programGraph.createField();
		Utilities.fillEdgeAttributesWithGivenValue(programGraph
				.createIsMemberOf(fieldDeclarationVertex, typeBlock), -1);
		VariableDeclaration variableDeclarationVertex = programGraph
				.createVariableDeclaration();
		Utilities.fillEdgeAttributesWithGivenValue(programGraph
				.createIsFieldCreationOf(variableDeclarationVertex,
						fieldDeclarationVertex), -1);
		Identifier identifierVertex = programGraph.createIdentifier();
		identifierVertex.set_name(field.getName());
		Utilities.fillEdgeAttributesWithGivenValue(programGraph
				.createIsVariableNameOf(identifierVertex,
						variableDeclarationVertex), -1);
		createAndAttachModifiers(field.getModifiers(),
				variableDeclarationVertex, symbolTable);
		TypeSpecification variableType = createTypeSpecification(
				field.getType(), symbolTable, mode);
		Utilities.fillEdgeAttributesWithGivenValue(
				programGraph.createIsTypeOfVariable(variableType,
						variableDeclarationVertex), -1);
		// @TODO: Annotations
		symbolTable.addVariableDeclaration(
				containingType.get_fullyQualifiedName(),
				variableDeclarationVertex);
		return variableDeclarationVertex;
	}

}
