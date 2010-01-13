package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.VariableInitializerImpl;

public class CGVariableInitializerImpl extends VariableInitializerImpl
		implements CGExpression {

	public CGVariableInitializerImpl(int id, Graph g) {
		super(id, g);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		try {
			return ((CGExpression) getFirstIsInitializerOf(EdgeDirection.IN)
					.getAlpha()).generateCode(jcg, bw, indentLevel);
		} catch (NullPointerException e) {
			System.err
					.println("Found VariableInitializer without Expression vertex!");
		}
		return this;
	}

}
