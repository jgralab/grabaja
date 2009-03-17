package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ForImpl;

public class CGForImpl extends ForImpl implements CGStatement {

	public CGForImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		bw.append("for (");

		// now comes the head (exactly one)
		((CGForHead) getFirstIsHeadOfFor(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(") ");

		// now the body (exactly one)
		((CGBlockImpl) getFirstIsLoopBodyOfFor(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
