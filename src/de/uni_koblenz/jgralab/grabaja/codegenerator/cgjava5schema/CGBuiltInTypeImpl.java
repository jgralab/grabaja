package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeArgumentOfTypeSpecification;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BuiltInTypeImpl;

public class CGBuiltInTypeImpl extends BuiltInTypeImpl implements
		CGTypeSpecification {

	public CGBuiltInTypeImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// code has to be generated unconditionally, when it's used...

		bw.append(type.toString().toLowerCase());

		// type args (0,*)
		boolean first = true;
		for (IsTypeArgumentOfTypeSpecification itaots : getIsTypeArgumentOfTypeSpecificationIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append('<');
				first = false;
			} else {
				bw.append(", ");
			}
			((CGTypeArgumentImpl) itaots.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
		if (!first) {
			bw.append('>');
		}

		return this;
	}

}
