package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.PrefixExpressionImpl;

public class CGPrefixExpressionImpl extends PrefixExpressionImpl implements
		CGExpression {

	public CGPrefixExpressionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		boolean isNested = JavaCodeGenerator.isNestedExpression(this);
		if (isNested) {
			bw.append('(');
		}

		switch (operator) {
		case BITWISECOMPLEMENT:
			bw.append('~');
			break;
		case DECREMENT:
			bw.append("--");
			break;
		case INCREMENT:
			bw.append("++");
			break;
		case MINUS:
			bw.append('-');
			break;
		case NOT:
			bw.append('!');
			break;
		case PLUS:
			bw.append('+');
			break;
		default:
			throw new RuntimeException("Unknown prefix operator " + operator
					+ ".");
		}

		// the RHS (1,1)
		((CGExpression) getFirstIsRightHandSideOfPrefixExpression(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		if (isNested) {
			bw.append(')');
		}

		return this;
	}

}
