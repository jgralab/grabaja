package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayTypeImpl;

public class CGArrayTypeImpl extends ArrayTypeImpl implements
		CGTypeSpecification {

	public CGArrayTypeImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// the type spec (1,1)
		if (getFirstIsElementTypeOf(EdgeDirection.IN)!=null) {
			((CGTypeSpecification) getFirstIsElementTypeOf(EdgeDirection.IN)
					.getAlpha()).generateCode(jcg, bw, indentLevel);
		} else {
			bw.append("/* Missing Type */ Object");
		}

		// the brackets for the dimensions
		for (int i = 1; i <= dimensions; i++) {
			bw.append("[]");
		}

		return this;
	}

}
