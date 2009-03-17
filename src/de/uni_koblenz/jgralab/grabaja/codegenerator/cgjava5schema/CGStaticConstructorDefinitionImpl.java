package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.StaticConstructorDefinitionImpl;

public class CGStaticConstructorDefinitionImpl extends
		StaticConstructorDefinitionImpl implements CGMember {

	public CGStaticConstructorDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// the block (1,1)
		return ((CGBlockImpl) getFirstIsBodyOfStaticConstructor(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

	}

}
