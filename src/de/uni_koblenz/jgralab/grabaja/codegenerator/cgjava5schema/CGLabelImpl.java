package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.LabelImpl;

public class CGLabelImpl extends LabelImpl implements CGStatement {

	public CGLabelImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// the name (1,1)
		((CGIdentifierImpl) getFirstIsLabelNameOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(": ");

		// the statement (1,1)
		((CGStatement) getFirstIsAttachedTo(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
