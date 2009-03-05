package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDimensionInitializerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayCreationImpl;

public class CGArrayCreationImpl extends ArrayCreationImpl implements
		CGExpression {

	public CGArrayCreationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append("new ");

		// the type (1,1), TODO: it's (0,1) in the schema
		((CGTypeSpecification) getFirstIsElementTypeOfCreatedArray(
				EdgeDirection.IN).getAlpha()).generateCode(bw, indentLevel);

		// now the initializers (1,*)
		for (IsDimensionInitializerOf idio : getIsDimensionInitializerOfIncidences(EdgeDirection.IN)) {
			((CGArrayInitializerImpl) idio.getAlpha()).generateCode(bw,
					indentLevel);
		}
	}

}
