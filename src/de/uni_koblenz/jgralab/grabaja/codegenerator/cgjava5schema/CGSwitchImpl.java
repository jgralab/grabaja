package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCaseOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDefaultCaseOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.SwitchImpl;

public class CGSwitchImpl extends SwitchImpl implements CGStatement {

	public CGSwitchImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("switch (");

		// the argument condition (1,1)
		((CGExpression) getFirstIsSwitchArgumentOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);

		bw.append(") {\n");

		// the cases (0,*)
		for (IsCaseOf c : getIsCaseOfIncidences(EdgeDirection.IN)) {
			JavaCodeGenerator.indent(bw, indentLevel);
			((CGCaseImpl) c.getAlpha()).generateCode(bw, indentLevel + 1);
			bw.append('\n');
		}

		// the default case (0,1)
		IsDefaultCaseOf idco = getFirstIsDefaultCaseOf(EdgeDirection.IN);
		if (idco != null) {
			JavaCodeGenerator.indent(bw, indentLevel);
			((CGDefaultImpl) idco.getAlpha()).generateCode(bw, indentLevel + 1);
			bw.append('\n');
		}

		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append('}');
	}

}
