package de.uni_koblenz.jgralab.grabaja.extractor;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.adapters.CommonASTAdapter;
import de.uni_koblenz.jgralab.grabaja.java5schema.AttributedEdge;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;

/**
 * A collection of static methods to used by the javaextractor.
 * 
 * @author: ultbreit@uni-koblenz.de
 */
public class Utilities {

	/**
	 * Converts a stack trace of an exception to a string. Used for logging.
	 */
	public static String stackTraceToString(Exception exception) {
		String stackTrace = "An exception occured:\n";
		StackTraceElement trace[] = exception.getStackTrace();
		for (StackTraceElement element : trace) {
			stackTrace += "@ " + element.toString() + "\n";
		}
		return stackTrace;
	}

	/**
	 * Copys the values of attributes from an attributed edge to another.
	 * 
	 * @param sourceEdge
	 *            The source edge.
	 * @param targetEdge
	 *            The target edge.
	 */
	public static void copyEdgeAttributes(AttributedEdge sourceEdge,
			AttributedEdge targetEdge) {
		if ((sourceEdge != null) && (targetEdge != null)) {
			targetEdge.set_offset(sourceEdge.get_offset());
			targetEdge.set_line(sourceEdge.get_line());
			targetEdge.set_column(sourceEdge.get_column());
			targetEdge.set_length(sourceEdge.get_length());
		}
	}

	/**
	 * Fills the attributes of an edge with the values from an AST element.
	 * 
	 * @param edgeWithAttributesToFill
	 *            The edge which's attributes should be set.
	 * @param sourceAST
	 *            The AST element to copy the attributes of.
	 */
	public static void fillEdgeAttributesFromAST(
			AttributedEdge edgeWithAttributesToFill, AST sourceAST) {
		if (sourceAST != null) {
			fillEdgeAttributes(edgeWithAttributesToFill,
					(CommonASTAdapter) sourceAST);
		}
	}

	/**
	 * Fills the attributes of an edge with the values from an AST element.
	 * 
	 * @param edgeWithAttributesToFill
	 *            The edge which's attributes should be set.
	 * @param sourceAST
	 *            The AST element to copy the attributes of.
	 */
	public static void fillEdgeAttributes(
			AttributedEdge edgeWithAttributesToFill, CommonASTAdapter sourceAST) {
		if ((edgeWithAttributesToFill != null) && (sourceAST != null)) {
			edgeWithAttributesToFill.set_offset(sourceAST.getOffset());
			edgeWithAttributesToFill.set_line(sourceAST.getLine());
			edgeWithAttributesToFill.set_column(sourceAST.getColumn());
			edgeWithAttributesToFill.set_length(sourceAST.getText().length());
		}
	}

	/**
	 * Fills the attributes of an edge with the calculated values of a beginning
	 * and end element.
	 * 
	 * @param edgeWithAttributesToFill
	 *            The edge which's attributes should be set.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            structure.
	 * @param endAST
	 *            The AST element representing the last elemeeent of the
	 *            structure.
	 */
	public static void fillEdgeAttributesFromASTDifference(
			AttributedEdge edgeWithAttributesToFill, AST beginAST, AST endAST) {
		if ((edgeWithAttributesToFill != null) && (beginAST != null)
				&& (endAST != null)) {
			CommonASTAdapter beginASTElement = (CommonASTAdapter) beginAST;
			CommonASTAdapter endASTElement = (CommonASTAdapter) endAST;
			edgeWithAttributesToFill.set_offset(beginASTElement.getOffset());
			edgeWithAttributesToFill.set_line(beginASTElement.getLine());
			edgeWithAttributesToFill.set_column(beginASTElement.getColumn());
			edgeWithAttributesToFill
					.set_length((endASTElement.getOffset() - beginASTElement
							.getOffset())
							+ endASTElement.getText().length());
		}
	}

	/**
	 * Fills the attributes of an edge with the given value. This is used only
	 * for edges created by reflection.
	 * 
	 * @param edgeWithAttributesToFill
	 *            The edge which's attributes should be set.
	 * @param value
	 *            The value to be set for all attributes.
	 */
	public static void fillEdgeAttributesWithGivenValue(
			AttributedEdge edgeWithAttributesToFill, int value) {
		edgeWithAttributesToFill.set_offset(value);
		edgeWithAttributesToFill.set_line(value);
		edgeWithAttributesToFill.set_column(value);
		edgeWithAttributesToFill.set_length(value);
	}

	/**
	 * Concatenates the next name element to a qualified name vertex.
	 * 
	 * @param vertex
	 *            The qualified name vertex.
	 * @param name
	 *            The AST element representing the next name element.
	 */
	public static void addToFullyQualifiedName(Vertex vertex, AST name) {
		if (vertex instanceof QualifiedType) {
			QualifiedType qualifiedTypeVertex = (QualifiedType) vertex;
			String fullyQualifiedName = qualifiedTypeVertex
					.get_fullyQualifiedName();
			if (fullyQualifiedName == null) {
				fullyQualifiedName = "";
			} else if (!fullyQualifiedName.isEmpty()) {
				fullyQualifiedName += ".";
			}
			qualifiedTypeVertex.set_fullyQualifiedName(fullyQualifiedName
					+ name.getText());
		}
	}

}