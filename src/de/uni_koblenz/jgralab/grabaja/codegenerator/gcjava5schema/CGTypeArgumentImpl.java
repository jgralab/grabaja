package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSimpleArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsWildcardArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.TypeArgumentImpl;

public class CGTypeArgumentImpl extends TypeArgumentImpl implements
		CGTypeSpecification {

	public CGTypeArgumentImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// The simple args (0,*)
		boolean first = true;
		for (IsSimpleArgumentOf isao : getIsSimpleArgumentOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGSimpleArgumentImpl) isao.getAlpha()).generateCode(bw,
					indentLevel);
		}

		// The wildcard args (0,*)
		first = true;
		for (IsWildcardArgumentOf iwao : getIsWildcardArgumentOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGWildcardArgumentImpl) iwao.getAlpha()).generateCode(bw,
					indentLevel);
		}
	}

}
