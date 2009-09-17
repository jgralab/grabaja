package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.CatchImpl;

public class CGCatchImpl extends CatchImpl implements CGStatement {

	public CGCatchImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append(" catch (");

		// the caught exception (1,1)
		((CGParameterDeclaration) getFirstIsCaughtExceptionOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		bw.append(") ");

		// the catch body (1,1)
		return ((CGBlockImpl) getFirstIsBodyOfCatch(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}

}
