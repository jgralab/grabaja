package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.VariableLengthDeclarationImpl;

public class CGVariableLengthDeclarationImpl extends
		VariableLengthDeclarationImpl implements CGParameterDeclaration {

	public CGVariableLengthDeclarationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// first the modifier (0,1)
		IsModifierOfParameter imop = getFirstIsModifierOfParameter(EdgeDirection.IN);
		if (imop != null) {
			((CGModifierImpl) imop.getAlpha()).generateCode(bw, indentLevel);
			bw.append(' ');
		}

		// then the type spec (1,1)
		((CGTypeSpecification) getFirstIsTypeOfParameter(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
		bw.append("... ");

		// then the name (1,1)
		((CGIdentifierImpl) getFirstIsParameterNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
	}

}
