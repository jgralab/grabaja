package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.CatchImpl;

public class CGCatchImpl extends CatchImpl implements CGStatement {

	public CGCatchImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append(" catch (");

		// the caught exception (1,1)
		((CGParameterDeclaration) getFirstIsCaughtExceptionOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		bw.append(") ");

		// the catch body (1,1)
		((CGBlockImpl) getFirstIsBodyOfCatch(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}
