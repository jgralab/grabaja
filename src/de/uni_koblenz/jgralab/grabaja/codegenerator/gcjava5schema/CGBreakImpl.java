package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBreakTargetOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BreakImpl;

public class CGBreakImpl extends BreakImpl implements CGStatement {

	public CGBreakImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("break");

		// the label (0,1)
		IsBreakTargetOf ibto = getFirstIsBreakTargetOf(EdgeDirection.IN);
		if (ibto != null) {
			bw.append(' ');
			((CGLabelImpl) ibto.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
