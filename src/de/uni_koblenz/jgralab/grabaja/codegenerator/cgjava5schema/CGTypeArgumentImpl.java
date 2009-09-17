package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSimpleArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsWildcardArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.TypeArgumentImpl;

public class CGTypeArgumentImpl extends TypeArgumentImpl implements
		CGTypeSpecification {

	public CGTypeArgumentImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		Vertex last = this;

		// The simple args (0,*)
		boolean first = true;
		for (IsSimpleArgumentOf isao : getIsSimpleArgumentOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			last = ((CGSimpleArgumentImpl) isao.getAlpha()).generateCode(jcg,
					bw, indentLevel);
		}

		// The wildcard args (0,*)
		first = true;
		for (IsWildcardArgumentOf iwao : getIsWildcardArgumentOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			last = ((CGWildcardArgumentImpl) iwao.getAlpha()).generateCode(jcg,
					bw, indentLevel);
		}

		return last;
	}

}
