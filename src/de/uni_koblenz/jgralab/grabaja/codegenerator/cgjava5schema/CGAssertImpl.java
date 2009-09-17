package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMessageOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.AssertImpl;

public class CGAssertImpl extends AssertImpl implements CGStatement {

	public CGAssertImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append("assert ");

		// the condition (1,1)
		Vertex last = ((CGExpression) getFirstIsConditionOfAssert(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		// the message (0,1)
		IsMessageOf imo = getFirstIsMessageOf(EdgeDirection.IN);
		if (imo != null) {
			bw.append(" : ");
			last = ((CGExpression) imo.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		return last;
	}

}
