package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfEnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArgumentOfEnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsEnumConstantBlockOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.EnumConstantImpl;

public class CGEnumConstantImpl extends EnumConstantImpl implements
		CGFieldDeclaration, CGMember {

	public CGEnumConstantImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// first the annotations (0,*)
		for (IsAnnotationOfEnumConstant iaot : getIsAnnotationOfEnumConstantIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) iaot.getAlpha()).generateCode(bw, indentLevel);
		}

		// the name (1,1)
		((CGIdentifierImpl) getFirstIsEnumConstantNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// the args (0,*)
		boolean first = true;
		for (IsArgumentOfEnumConstant iaoec : getIsArgumentOfEnumConstantIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append('(');
			} else {
				bw.append(", ");
			}
			((CGExpression) iaoec.getAlpha()).generateCode(bw, indentLevel);
		}
		if (!first) {
			bw.append(')');
		}

		// the block (0,1)
		IsEnumConstantBlockOf iecbo = getFirstIsEnumConstantBlockOf(EdgeDirection.IN);
		if (iecbo != null) {
			bw.append(' ');
			((CGBlockImpl) iecbo.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
