package de.uni_koblenz.jgralab.grabaja.extractor.resolvers;

import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCastedTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElementTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElementTypeOfCreatedArray;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsImportedTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfEnum;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLowerBoundOfWildcardArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsReturnTypeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfAnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfObject;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfSimpleArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsUpperBoundOfTypeParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsUpperBoundOfWildcardArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeSpecification;

/**
 * Abstract super class for type specification resolvers.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public abstract class TypeSpecificationResolver extends Resolver {

	/**
	 * Attaches vertex representing type specification to vertex representing
	 * it's type definition. Determines if type specification was resolved
	 * before.
	 * 
	 * @param qualifiedTypeVertex
	 *            A vertex representing a type specification.
	 * @param typeVertex
	 *            A vertex representing a type definition.
	 */
	protected void attachToType(QualifiedType qualifiedTypeVertex,
			Type typeVertex) {
		String fullyQualifiedName = typeVertex.get_fullyQualifiedName();
		if (symbolTable.hasResolvedTypeSpecification(fullyQualifiedName)) {
			replaceByResolvedOne(fullyQualifiedName, qualifiedTypeVertex);
		} else {
			attach(qualifiedTypeVertex, typeVertex);
			symbolTable.addResolvedTypeSpecification(qualifiedTypeVertex);
		}
	}

	/**
	 * Attaches vertex representing type specification to vertex representing
	 * it's type definition. Creates structure: Type --- [IsTypeDefinitionOf]
	 * ---> QualifiedType and sets the edges attributes.
	 * 
	 * @param typeVertex
	 *            A vertex representing a type definition.
	 * @param qualifiedTypeVertex
	 *            A vertex representing a type specification.
	 */
	private void attach(QualifiedType qualifiedTypeVertex, Type typeVertex) {
		/* IsTypeDefinitionOf isTypeDefinitionOfEdge = */symbolTable.getGraph()
				.createIsTypeDefinitionOf(typeVertex, qualifiedTypeVertex);
		qualifiedTypeVertex.set_fullyQualifiedName(typeVertex
				.get_fullyQualifiedName());
		/* IsExternalDeclarationIn isExternalDeclarationInEdge = */typeVertex
				.getFirstIsExternalDeclarationIn();
	}

	/**
	 * Replaces a vertex representing type specification by another one which
	 * was resolved before.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of type definition.
	 * @param qualifiedTypeVertex
	 *            Vertex to replace representing type specification.
	 * 
	 */
	protected void replaceByResolvedOne(String fullyQualifiedName,
			QualifiedType qualifiedTypeVertex) {
		if (symbolTable.hasResolvedTypeSpecification(fullyQualifiedName)) {
			QualifiedType resolvedQualifiedTypeVertex = symbolTable
					.getResolvedTypeSpecification(fullyQualifiedName);
			replaceBy(qualifiedTypeVertex, resolvedQualifiedTypeVertex);
			qualifiedTypeVertex.delete();
		}
	}

	/**
	 * Replaces vertex representing a fully qualified type specification in
	 * graph by another vertex representing a type specification.
	 * 
	 * @param qualifiedTypeVertex
	 *            Vertex to replace.
	 * @param typeSpecificationVertex
	 *            Vertex to take the place of the other one.
	 */
	protected void replaceBy(QualifiedType qualifiedTypeVertex,
			TypeSpecification typeSpecificationVertex) {
		IsTypeOfVariable isTypeOfVariableEdge = qualifiedTypeVertex
				.getFirstIsTypeOfVariable();
		if (isTypeOfVariableEdge != null) {
			isTypeOfVariableEdge.setAlpha(typeSpecificationVertex);
		}
		IsCastedTypeOf isCastedTypeOfEdge = qualifiedTypeVertex
				.getFirstIsCastedTypeOf();
		if (isCastedTypeOfEdge != null) {
			isCastedTypeOfEdge.setAlpha(typeSpecificationVertex);
		}
		IsReturnTypeOf isReturnTypeOfEdge = qualifiedTypeVertex
				.getFirstIsReturnTypeOf();
		if (isReturnTypeOfEdge != null) {
			isReturnTypeOfEdge.setAlpha(typeSpecificationVertex);
		}
		IsImportedTypeOf isImportedTypeOfEdge = qualifiedTypeVertex
				.getFirstIsImportedTypeOf();
		if (isImportedTypeOfEdge != null) {
			isImportedTypeOfEdge.setAlpha(typeSpecificationVertex);
		}
		IsAnnotationNameOf isAnnotationNameOfEdge = qualifiedTypeVertex
				.getFirstIsAnnotationNameOf();
		if (isAnnotationNameOfEdge != null) {
			isAnnotationNameOfEdge.setAlpha(typeSpecificationVertex);
		}
		IsTypeOfSimpleArgument isTypeOfSimpleArgumentEdge = qualifiedTypeVertex
				.getFirstIsTypeOfSimpleArgument();
		if (isTypeOfSimpleArgumentEdge != null) {
			isTypeOfSimpleArgumentEdge.setAlpha(typeSpecificationVertex);
		}
		IsLowerBoundOfWildcardArgument isLowerBoundOfWildcardArgumentEdge = qualifiedTypeVertex
				.getFirstIsLowerBoundOfWildcardArgument();
		if (isLowerBoundOfWildcardArgumentEdge != null) {
			isLowerBoundOfWildcardArgumentEdge
					.setAlpha(typeSpecificationVertex);
		}
		IsUpperBoundOfWildcardArgument isUpperBoundOfWildcardArgumentEdge = qualifiedTypeVertex
				.getFirstIsUpperBoundOfWildcardArgument();
		if (isUpperBoundOfWildcardArgumentEdge != null) {
			isUpperBoundOfWildcardArgumentEdge
					.setAlpha(typeSpecificationVertex);
		}
		IsElementTypeOf isElementTypeOfEdge = qualifiedTypeVertex
				.getFirstIsElementTypeOf();
		if (isElementTypeOfEdge != null) {
			isElementTypeOfEdge.setAlpha(typeSpecificationVertex);
		}
		IsUpperBoundOfTypeParameter isUpperBoundOfTypeParameterEdge = qualifiedTypeVertex
				.getFirstIsUpperBoundOfTypeParameter();
		if (isUpperBoundOfTypeParameterEdge != null) {
			isUpperBoundOfTypeParameterEdge.setAlpha(typeSpecificationVertex);
		}
		IsSuperClassOfClass isSuperClassOfClassEdge = qualifiedTypeVertex
				.getFirstIsSuperClassOfClass();
		if (isSuperClassOfClassEdge != null) {
			isSuperClassOfClassEdge.setAlpha(typeSpecificationVertex);
		}
		IsSuperClassOfInterface isSuperClassOfInterfaceEdge = qualifiedTypeVertex
				.getFirstIsSuperClassOfInterface();
		if (isSuperClassOfInterfaceEdge != null) {
			isSuperClassOfInterfaceEdge.setAlpha(typeSpecificationVertex);
		}
		IsInterfaceOfClass isInterfaceOfClassEdge = qualifiedTypeVertex
				.getFirstIsInterfaceOfClass();
		if (isInterfaceOfClassEdge != null) {
			isInterfaceOfClassEdge.setAlpha(typeSpecificationVertex);
		}
		IsInterfaceOfEnum isInterfaceOfEnumEdge = qualifiedTypeVertex
				.getFirstIsInterfaceOfEnum();
		if (isInterfaceOfEnumEdge != null) {
			isInterfaceOfEnumEdge.setAlpha(typeSpecificationVertex);
		}
		IsElementTypeOfCreatedArray isElementTypeOfCreatedArrayEdge = qualifiedTypeVertex
				.getFirstIsElementTypeOfCreatedArray();
		if (isElementTypeOfCreatedArrayEdge != null) {
			isElementTypeOfCreatedArrayEdge.setAlpha(typeSpecificationVertex);
		}
		IsTypeOfObject isTypeOfObjectEdge = qualifiedTypeVertex
				.getFirstIsTypeOfObject();
		if (isTypeOfObjectEdge != null) {
			isTypeOfObjectEdge.setAlpha(typeSpecificationVertex);
		}
		IsTypeOfAnnotationField isTypeOfAnnotationFieldEdge = qualifiedTypeVertex
				.getFirstIsTypeOfAnnotationField();
		if (isTypeOfAnnotationFieldEdge != null) {
			isTypeOfAnnotationFieldEdge.setAlpha(typeSpecificationVertex);
		}
		IsExceptionThrownByConstructor isExceptionThrownByConstructorEdge = qualifiedTypeVertex
				.getFirstIsExceptionThrownByConstructor();
		if (isExceptionThrownByConstructorEdge != null) {
			isExceptionThrownByConstructorEdge
					.setAlpha(typeSpecificationVertex);
		}
		IsExceptionThrownByMethod isExceptionThrownByMethodEdge = qualifiedTypeVertex
				.getFirstIsExceptionThrownByMethod();
		if (isExceptionThrownByMethodEdge != null) {
			isExceptionThrownByMethodEdge.setAlpha(typeSpecificationVertex);
		}
		IsTypeOfParameter isTypeOfParameterEdge = qualifiedTypeVertex
				.getFirstIsTypeOfParameter();
		if (isTypeOfParameterEdge != null) {
			isTypeOfParameterEdge.setAlpha(typeSpecificationVertex);
		}
	}
}