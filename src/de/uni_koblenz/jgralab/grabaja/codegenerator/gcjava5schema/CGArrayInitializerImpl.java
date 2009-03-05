package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSizeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayInitializerImpl;

public class CGArrayInitializerImpl extends ArrayInitializerImpl implements
		CGExpression {

	public CGArrayInitializerImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// TODO: Implement the IsContentOf Expressions for literal array creations...

		bw.append('[');
		boolean first = true;
		for (IsSizeOf ico : getIsSizeOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGExpression) ico.getAlpha()).generateCode(bw, indentLevel);
		}

		bw.append(']');
	}

}
