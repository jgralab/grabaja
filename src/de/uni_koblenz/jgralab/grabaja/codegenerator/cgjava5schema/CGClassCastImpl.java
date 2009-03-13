package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ClassCastImpl;

public class CGClassCastImpl extends ClassCastImpl implements CGExpression {

	public CGClassCastImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// the type spec (1,1)
		bw.append('(');
		((CGTypeSpecification) getFirstIsCastedTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
		bw.append(") ");

		// the object (1,1)
		((CGExpression) getFirstIsCastedObjectOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}