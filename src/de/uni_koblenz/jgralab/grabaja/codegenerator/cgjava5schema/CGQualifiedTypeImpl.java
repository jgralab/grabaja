package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeArgumentOfTypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.QualifiedTypeImpl;

public class CGQualifiedTypeImpl extends QualifiedTypeImpl implements
		CGExpression, CGQualifiedName {

	public CGQualifiedTypeImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append(fullyQualifiedName);

		// type args (0,*)
		boolean first = true;
		for (IsTypeArgumentOfTypeSpecification itaots : getIsTypeArgumentOfTypeSpecificationIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append('<');
				first = false;
			} else {
				bw.append(", ");
			}
			((CGTypeArgumentImpl) itaots.getAlpha()).generateCode(bw,
					indentLevel);
		}
		if (!first) {
			bw.append('>');
		}
	}

}
