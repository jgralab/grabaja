package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.WhileImpl;

public class CGWhileImpl extends WhileImpl implements CGStatement {

	public CGWhileImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		bw.append("while (");

		((CGExpression) getFirstIsConditionOfWhile(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(") ");

		((CGStatement) getFirstIsLoopBodyOfWhile(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
