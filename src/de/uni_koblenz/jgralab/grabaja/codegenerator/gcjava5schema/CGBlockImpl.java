package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMemberOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfBody;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BlockImpl;

public class CGBlockImpl extends BlockImpl implements CGStatement {

	public CGBlockImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("{\n");
		for (IsMemberOf imo : getIsMemberOfIncidences(EdgeDirection.IN)) {
			((CGMember) imo.getAlpha()).generateCode(bw, indentLevel + 1);
			bw.append('\n');
		}

		for (IsStatementOfBody isob : getIsStatementOfBodyIncidences(EdgeDirection.IN)) {
			CGStatement s =	(CGStatement) isob.getAlpha();
			s.generateCode(bw, indentLevel + 1);
			// TODO: Add ";" only for non-block constructs
			bw.append(";");
		}
		bw.append("\n");

		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append("}");
	}
}
