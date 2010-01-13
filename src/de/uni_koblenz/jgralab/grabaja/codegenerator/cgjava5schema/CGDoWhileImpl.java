package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.DoWhileImpl;

public class CGDoWhileImpl extends DoWhileImpl implements CGStatement {

	public CGDoWhileImpl(int id, Graph g) {
		super(id, g);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append("do ");

		// the block (1,1)
		((CGStatement) getFirstIsLoopBodyOfDoWhile(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(" while (");

		// the condition (1,1)
		((CGExpression) getFirstIsConditionOfDoWhile(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
		bw.append(")");

		return this;
	}

}
