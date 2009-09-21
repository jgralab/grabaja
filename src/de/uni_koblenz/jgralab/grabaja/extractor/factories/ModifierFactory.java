package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.AttributedEdge;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfAnnotation;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfAnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfEnum;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifiers;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;

/**
 * Provides functionality for creating modifier elements in graph.
 * 
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class ModifierFactory extends SubgraphFactory {

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 * @param symbolTable
	 *            The symbol table to be used.
	 */
	public ModifierFactory(Java5 pg, SymbolTable symbolTable) {
		programGraph = pg;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates a vertex for a modifier.
	 * 
	 * @param modifierType
	 *            The modifier's type.
	 * @param symbolTable
	 *            The symbol table to be used.
	 * @return The created vertex.
	 */
	public static Modifier createModifier(Modifiers modifierType,
			SymbolTable symbolTable) {
		if (symbolTable.hasModifier(modifierType)) {
			return symbolTable.getModifier(modifierType);
		}
		Modifier modifier = programGraph.createModifier();
		modifier.set_type(modifierType);
		symbolTable.addModifier(modifierType, modifier);
		return modifier;
	}

	/**
	 * Creates an edge between a modifier and it's according element.
	 * 
	 * @param modifierVertex
	 *            The modifier.
	 * @param parentVertex
	 *            The element the modifier belongs to.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            modifier.
	 * @param endAST
	 *            The AST element representing the last element of the modifier.
	 */
	public void attachModifier(Modifier modifierVertex, Vertex parentVertex,
			AST beginAST, AST endAST) {
		if (parentVertex instanceof ClassDefinition) {
			IsModifierOfClass isModifierOfClassEdge = programGraph
					.createIsModifierOfClass(modifierVertex,
							(ClassDefinition) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfClassEdge, beginAST, endAST);
		} else if (parentVertex instanceof InterfaceDefinition) {
			IsModifierOfInterface isModifierOfInterfaceEdge = programGraph
					.createIsModifierOfInterface(modifierVertex,
							(InterfaceDefinition) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfInterfaceEdge, beginAST, endAST);
		} else if (parentVertex instanceof EnumDefinition) {
			IsModifierOfEnum isModifierOfEnumEdge = programGraph
					.createIsModifierOfEnum(modifierVertex,
							(EnumDefinition) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(isModifierOfEnumEdge,
					beginAST, endAST);
		} else if (parentVertex instanceof AnnotationDefinition) {
			IsModifierOfAnnotation isModifierOfAnnotationEdge = programGraph
					.createIsModifierOfAnnotation(modifierVertex,
							(AnnotationDefinition) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfAnnotationEdge, beginAST, endAST);
		} else if (parentVertex instanceof ConstructorDefinition) {
			IsModifierOfConstructor isModifierOfConstructorEdge = programGraph
					.createIsModifierOfConstructor(modifierVertex,
							(ConstructorDefinition) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfConstructorEdge, beginAST, endAST);
		} else if (parentVertex instanceof MethodDeclaration) {
			IsModifierOfMethod isModifierOfMethodEdge = programGraph
					.createIsModifierOfMethod(modifierVertex,
							(MethodDeclaration) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfMethodEdge, beginAST, endAST);
		} else if (parentVertex instanceof VariableDeclaration) {
			IsModifierOfVariable isModifierOfVariableEdge = programGraph
					.createIsModifierOfVariable(modifierVertex,
							(VariableDeclaration) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfVariableEdge, beginAST, endAST);
		} else if (parentVertex instanceof ParameterDeclaration) {
			IsModifierOfParameter isModifierOfParameterEdge = programGraph
					.createIsModifierOfParameter(modifierVertex,
							(ParameterDeclaration) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfParameterEdge, beginAST, endAST);
		} else if (parentVertex instanceof AnnotationField) {
			IsModifierOfAnnotationField isModifierOfAnnotationFieldEdge = programGraph
					.createIsModifierOfAnnotationField(modifierVertex,
							(AnnotationField) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isModifierOfAnnotationFieldEdge, beginAST, endAST);
		}
	}

	/**
	 * Creates an edge between a modifier and it's according element. Fills the
	 * edge's position informations with values denoting it as created by
	 * reflection.
	 * 
	 * @param modifierVertex
	 *            The modifier.
	 * @param parentVertex
	 *            The element the modifier belongs to.
	 */
	public static void attachModifier(Modifier modifierVertex,
			Vertex parentVertex) {
		AttributedEdge edgeWithAttributesToFill = null;
		if (parentVertex instanceof ClassDefinition) {
			edgeWithAttributesToFill = programGraph.createIsModifierOfClass(
					modifierVertex, (ClassDefinition) parentVertex);
		} else if (parentVertex instanceof InterfaceDefinition) {
			edgeWithAttributesToFill = programGraph
					.createIsModifierOfInterface(modifierVertex,
							(InterfaceDefinition) parentVertex);
		} else if (parentVertex instanceof EnumDefinition) {
			edgeWithAttributesToFill = programGraph.createIsModifierOfEnum(
					modifierVertex, (EnumDefinition) parentVertex);
		} else if (parentVertex instanceof AnnotationDefinition) {
			edgeWithAttributesToFill = programGraph
					.createIsModifierOfAnnotation(modifierVertex,
							(AnnotationDefinition) parentVertex);
		} else if (parentVertex instanceof ConstructorDefinition) {
			edgeWithAttributesToFill = programGraph
					.createIsModifierOfConstructor(modifierVertex,
							(ConstructorDefinition) parentVertex);
		} else if (parentVertex instanceof MethodDeclaration) {
			edgeWithAttributesToFill = programGraph.createIsModifierOfMethod(
					modifierVertex, (MethodDeclaration) parentVertex);
		} else if (parentVertex instanceof VariableDeclaration) {
			edgeWithAttributesToFill = programGraph.createIsModifierOfVariable(
					modifierVertex, (VariableDeclaration) parentVertex);
		} else if (parentVertex instanceof ParameterDeclaration) {
			edgeWithAttributesToFill = programGraph
					.createIsModifierOfParameter(modifierVertex,
							(ParameterDeclaration) parentVertex);
		} else if (parentVertex instanceof AnnotationField) {
			edgeWithAttributesToFill = programGraph
					.createIsModifierOfAnnotationField(modifierVertex,
							(AnnotationField) parentVertex);
		}
		if (edgeWithAttributesToFill != null) {
			Utilities.fillEdgeAttributesWithGivenValue(
					edgeWithAttributesToFill, -1);
		}
	}

}