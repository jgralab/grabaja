package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.NullImpl;

public class CGNullImpl extends NullImpl implements CGExpression {

	public CGNullImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("null");
	}

}
