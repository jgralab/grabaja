package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsContentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSizeOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayInitializerImpl;

public class CGArrayInitializerImpl extends ArrayInitializerImpl implements
		CGExpression {

	public CGArrayInitializerImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return this;
		}

		// this array creation uses a literal form: {{1,2,3}, {4,5,6}}
		boolean first = true;
		for (IsContentOf ico : getIsContentOfIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append('{');
				first = false;
			} else {
				bw.append(", ");
			}
			((CGExpression) ico.getAlpha()).generateCode(jcg, bw, indentLevel);
		}
		if (!first) {
			bw.append('}');
		}

		// this array creation has the form: new Foo[1][2][38]
		first = true;
		for (IsSizeOf ico : getIsSizeOfIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append('[');
				first = false;
			} else {
				bw.append(", ");
			}
			((CGExpression) ico.getAlpha()).generateCode(jcg, bw, indentLevel);
		}
		if (!first) {
			bw.append(']');
		}

		return this;
	}

}
