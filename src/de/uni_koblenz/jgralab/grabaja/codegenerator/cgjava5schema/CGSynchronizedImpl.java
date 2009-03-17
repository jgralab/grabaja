package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.SynchronizedImpl;

public class CGSynchronizedImpl extends SynchronizedImpl implements CGStatement {

	public CGSynchronizedImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return this;
		}

		bw.append("synchronized (");
		// the monitor (1,1)
		((CGExpression) getFirstIsMonitorOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
		bw.append(") ");

		// the block (1,1)
		return ((CGBlockImpl) getFirstIsSynchronizedBodyOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}
}
