package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.SimpleArgumentImpl;

public class CGSimpleArgumentImpl extends SimpleArgumentImpl implements
		CodeGenerator {

	public CGSimpleArgumentImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// typespec (1,1)
		((CGTypeSpecification) getFirstIsTypeOfSimpleArgument(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
	}

}
