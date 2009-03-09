package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfFinally;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsHandlerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.TryImpl;

public class CGTryImpl extends TryImpl implements CGStatement {

	public CGTryImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("try ");

		// the body (1,1)
		((CGBlockImpl) getFirstIsBodyOfTry(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);

		// the handlers (0,*)
		for (IsHandlerOf iho : getIsHandlerOfIncidences(EdgeDirection.IN)) {
			((CGCatchImpl) iho.getAlpha()).generateCode(bw, indentLevel);
		}

		// the finally block (0,1)
		IsBodyOfFinally ibof = getFirstIsBodyOfFinally(EdgeDirection.IN);
		if (ibof != null) {
			bw.append(" finally ");
			((CGBlockImpl) ibof.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
