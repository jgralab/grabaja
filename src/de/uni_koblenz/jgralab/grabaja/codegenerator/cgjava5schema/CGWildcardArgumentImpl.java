package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLowerBoundOfWildcardArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsUpperBoundOfWildcardArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.WildcardArgumentImpl;

public class CGWildcardArgumentImpl extends WildcardArgumentImpl implements
		CodeGenerator {

	public CGWildcardArgumentImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		bw.append("?");

		// lower bound (0,1)
		IsLowerBoundOfWildcardArgument ilbowa = getFirstIsLowerBoundOfWildcardArgument(EdgeDirection.IN);
		if (ilbowa != null) {
			bw.append(" super ");
			((CGTypeSpecification) ilbowa.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// upper bound (0,1)
		IsUpperBoundOfWildcardArgument iubowa = getFirstIsUpperBoundOfWildcardArgument(EdgeDirection.IN);
		if (iubowa != null) {
			bw.append(" extends ");
			((CGTypeSpecification) iubowa.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
	}

}
