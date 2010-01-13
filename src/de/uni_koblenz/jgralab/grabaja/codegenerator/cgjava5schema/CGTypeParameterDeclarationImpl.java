package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.TypeParameterDeclarationImpl;

public class CGTypeParameterDeclarationImpl extends
		TypeParameterDeclarationImpl implements CGType {

	public CGTypeParameterDeclarationImpl(int id, Graph g) {
		super(id, g);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// the identifier (1,1)
		return ((CGIdentifierImpl) getFirstIsTypeParameterDeclarationNameOf(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
