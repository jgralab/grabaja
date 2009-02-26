package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.PostfixExpressionImpl;

public class CGPostfixExpressionImpl extends PostfixExpressionImpl implements
		CGExpression {

	public CGPostfixExpressionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// the LHS (1,1)
		((CGExpression) getFirstIsLeftHandSideOfPostfixExpression(
				EdgeDirection.IN).getAlpha()).generateCode(bw, indentLevel);

		switch (operator) {
		case DECREMENT:
			bw.append("--");
			break;
		case INCREMENT:
			bw.append("++");
			break;
		default:
			throw new RuntimeException("Unknown operator " + operator + "!!!");
		}
	}

}
