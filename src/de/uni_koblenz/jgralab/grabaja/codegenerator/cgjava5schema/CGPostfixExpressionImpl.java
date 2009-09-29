package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.PostfixExpressionImpl;

public class CGPostfixExpressionImpl extends PostfixExpressionImpl implements
		CGExpression {

	public CGPostfixExpressionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		boolean isNested = JavaCodeGenerator.isNestedExpression(this);
		if (isNested) {
			bw.append('(');
		}

		// the LHS (1,1)
		((CGExpression) getFirstIsLeftHandSideOfPostfixExpression(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		switch (_operator) {
		case DECREMENT:
			bw.append("--");
			break;
		case INCREMENT:
			bw.append("++");
			break;
		default:
			throw new RuntimeException("Unknown operator " + _operator + "!!!");
		}

		if (isNested) {
			bw.append(')');
		}

		return this;
	}
}
