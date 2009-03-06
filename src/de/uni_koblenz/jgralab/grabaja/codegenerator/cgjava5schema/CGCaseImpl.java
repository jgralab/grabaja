package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfCase;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.CaseImpl;

public class CGCaseImpl extends CaseImpl implements CGStatement {

	public CGCaseImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("case ");

		// the condition (1,1)
		((CGExpression) getFirstIsCaseConditionOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);

		bw.append(":\n");

		// then the statements (0,*)
		boolean first = true;
		for (IsStatementOfCase isoc : getIsStatementOfCaseIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append("\n");
			}
			JavaCodeGenerator.indent(bw, indentLevel);
			((CGStatement) isoc.getAlpha()).generateCode(bw, indentLevel);
			bw.append(";");
		}

	}

}
