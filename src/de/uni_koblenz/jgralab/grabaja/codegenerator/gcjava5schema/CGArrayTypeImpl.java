package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayTypeImpl;

public class CGArrayTypeImpl extends ArrayTypeImpl implements
		CGTypeSpecification {

	public CGArrayTypeImpl(int id, Graph g) {
		super(id, g);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// TODO Auto-generated method stub

	}

}
