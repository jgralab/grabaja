package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMessageOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.AssertImpl;

public class CGAssertImpl extends AssertImpl implements CGStatement {

	public CGAssertImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("assert ");

		// the condition (1,1)
		((CGExpression) getFirstIsConditionOfAssert(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// the message (0,1)
		IsMessageOf imo = getFirstIsMessageOf(EdgeDirection.IN);
		if (imo != null) {
			bw.append(" : ");
			((CGExpression) imo.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
