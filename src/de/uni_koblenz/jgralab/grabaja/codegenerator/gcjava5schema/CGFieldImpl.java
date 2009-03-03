package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.FieldImpl;

public class CGFieldImpl extends FieldImpl implements CGMember {

	public CGFieldImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// field creation var decl (1,1)
		((CGStatement) getFirstIsFieldCreationOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}
