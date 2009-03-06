package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ClassImportDefinitionImpl;

public class CGClassImportDefinitionImpl extends ClassImportDefinitionImpl
		implements CGImportDefinition {

	public CGClassImportDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("import ");

		// the qualified name (1,1)
		((CGQualifiedName) getFirstIsImportedTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
		bw.append(";\n");
	}

}
