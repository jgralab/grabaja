package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.AnnotationImpl;

public class CGAnnotationImpl extends AnnotationImpl implements CodeGenerator {

	public CGAnnotationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append('@');

		// the name (1,1)
		((CGQualifiedName) getFirstIsAnnotationNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		// the args (0,*)
		boolean first = true;
		for (IsAnnotationArgumentOf iaao : getIsAnnotationArgumentOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append('(');
			} else {
				bw.append(", ");
			}
			((CGExpression) iaao.getAlpha()).generateCode(jcg, bw, indentLevel);
		}
		if (!first) {
			bw.append(")");
		}

		bw.append('\n');
		JavaCodeGenerator.indent(bw, indentLevel);
	}
}
