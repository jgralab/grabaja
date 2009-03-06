package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDimensionInitializerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElementTypeOfCreatedArray;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayCreationImpl;

public class CGArrayCreationImpl extends ArrayCreationImpl implements
		CGExpression {

	public CGArrayCreationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// the type (0,1)
		IsElementTypeOfCreatedArray ietoca = getFirstIsElementTypeOfCreatedArray(EdgeDirection.IN);
		if (ietoca != null) {
			bw.append("new ");
			// this array creation has the form: new Foo[1][2][38]
			((CGTypeSpecification) ietoca.getAlpha()).generateCode(bw,
					indentLevel);
		}
		// now the initializers (1,*)
		for (IsDimensionInitializerOf idio : getIsDimensionInitializerOfIncidences(EdgeDirection.IN)) {
			((CGArrayInitializerImpl) idio.getAlpha()).generateCode(bw,
					indentLevel);
		}
	}
}
