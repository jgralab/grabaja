package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.StaticInitializerDefinitionImpl;

public class CGStaticInitializerDefinitionImpl extends
		StaticInitializerDefinitionImpl implements CGMember {

	public CGStaticInitializerDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("static ");

		// the block (1,1)
		((CGBlockImpl) getFirstIsBodyOfStaticInitializer(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
	}

}