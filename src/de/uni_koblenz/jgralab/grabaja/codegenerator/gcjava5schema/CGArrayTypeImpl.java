package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayTypeImpl;

public class CGArrayTypeImpl extends ArrayTypeImpl implements
		CGTypeSpecification {

	public CGArrayTypeImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// the type spec (1,1)
		((CGTypeSpecification) getFirstIsElementTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// the brackets for the dimensions
		for (int i = 1; i <= dimensions; i++) {
			bw.append("[]");
		}
	}

}
