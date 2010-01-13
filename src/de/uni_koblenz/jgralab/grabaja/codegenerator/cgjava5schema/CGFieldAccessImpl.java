package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArrayElementIndexOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsFieldContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.FieldAccessImpl;

public class CGFieldAccessImpl extends FieldAccessImpl implements CGExpression {

	public CGFieldAccessImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// First the container (0,1)
		IsFieldContainerOf ifco = getFirstIsFieldContainerOf(EdgeDirection.IN);
		if (ifco != null) {
			((CGExpression) ifco.getAlpha()).generateCode(jcg, bw, indentLevel);
			bw.append('.');
		}

		// now the field name (1,1)
		((CGIdentifierImpl) getFirstIsFieldNameOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		// now the array element indexes (0,*)
		for (IsArrayElementIndexOf iaeio : getIsArrayElementIndexOfIncidences(EdgeDirection.IN)) {
			bw.append('[');
			((CGExpression) iaeio.getAlpha())
					.generateCode(jcg, bw, indentLevel);
			bw.append(']');
		}

		return this;
	}

}
