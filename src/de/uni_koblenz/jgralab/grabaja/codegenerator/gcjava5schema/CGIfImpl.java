package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElseOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.IfImpl;

public class CGIfImpl extends IfImpl implements CGStatement {

	public CGIfImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		JavaCodeGenerator.indent(bw, indentLevel);

		bw.append("if (");

		// here comes the condition (exactly one)
		((CGExpression) getFirstIsConditionOfIf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);

		bw.append(") ");

		// now the then (exactly one)
		((CGStatement) getFirstIsThenOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);

		// now the else (0 or 1)
		IsElseOf ieo = getFirstIsElseOf(EdgeDirection.IN);
		if (ieo != null) {
			bw.append(" else ");
			((CGStatement) ieo.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
