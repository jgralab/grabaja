package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfDefaultCase;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.DefaultImpl;

public class CGDefaultImpl extends DefaultImpl implements CGStatement {

	public CGDefaultImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("default:\n");

		// then the statements (0,*)
		boolean first = true;
		for (IsStatementOfDefaultCase isodc : getIsStatementOfDefaultCaseIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append("\n");
			}
			JavaCodeGenerator.indent(bw, indentLevel);
			((CGStatement) isodc.getAlpha()).generateCode(jcg, bw, indentLevel);
			bw.append(";");
		}

	}

}
