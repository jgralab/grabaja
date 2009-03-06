package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.VariableInitializerImpl;

public class CGVariableInitializerImpl extends VariableInitializerImpl
		implements CGExpression {

	public CGVariableInitializerImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		try {
			((CGExpression) getFirstIsInitializerOf(EdgeDirection.IN)
					.getAlpha()).generateCode(bw, indentLevel);
		} catch (NullPointerException e) {
			System.err
					.println("Found VariableInitializer without Expression vertex!");
		}
	}

}
