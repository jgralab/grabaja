package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.TypeParameterDeclarationImpl;

public class CGTypeParameterDeclarationImpl extends
		TypeParameterDeclarationImpl implements CGType {

	public CGTypeParameterDeclarationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// the identifier (1,1)
		((CGIdentifierImpl) getFirstIsTypeParameterDeclarationNameOf(
				EdgeDirection.IN).getAlpha()).generateCode(bw, indentLevel);
	}

}
