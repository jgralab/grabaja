package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.EnumConstantImpl;

public class CGEnumConstantImpl extends EnumConstantImpl implements
		CGFieldDeclaration, CGMember {

	public CGEnumConstantImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// TODO annotiations and stuff missing... And what's the Block the
		// schema has at the IsEnumConstantBlockOf edge???

		// the name (1,1)
		((CGIdentifierImpl) getFirstIsEnumConstantNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
	}

}
