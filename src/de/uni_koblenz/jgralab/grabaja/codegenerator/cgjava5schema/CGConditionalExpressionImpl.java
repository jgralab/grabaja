package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.ConditionalExpressionImpl;

public class CGConditionalExpressionImpl extends ConditionalExpressionImpl
		implements CGExpression {

	public CGConditionalExpressionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		boolean isNested = JavaCodeGenerator.isNestedExpression(this);
		if (isNested) {
			bw.append('(');
		}
		bw.append("(");

		// new the condition (1,1)
		((CGExpression) getFirstIsConditionOfExpression(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		bw.append(") ? ");

		// now the match (1,1)
		((CGExpression) getFirstIsMatchOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(" : ");

		// now the mismatch (1,1)
		((CGExpression) getFirstIsMismatchOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		if (isNested) {
			bw.append(')');
		}

		return this;
	}

}
