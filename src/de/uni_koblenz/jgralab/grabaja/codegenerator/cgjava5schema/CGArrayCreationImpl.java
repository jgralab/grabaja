package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDimensionInitializerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElementTypeOfCreatedArray;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ArrayCreationImpl;

public class CGArrayCreationImpl extends ArrayCreationImpl implements
		CGExpression {

	public CGArrayCreationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		Vertex last = this;

		// the type (0,1)
		IsElementTypeOfCreatedArray ietoca = getFirstIsElementTypeOfCreatedArray(EdgeDirection.IN);
		if (ietoca != null) {
			bw.append("new ");
			// this array creation has the form: new Foo[1][2][38]
			last = ((CGTypeSpecification) ietoca.getAlpha()).generateCode(jcg,
					bw, indentLevel);
		}
		// now the initializers (1,*)
		for (IsDimensionInitializerOf idio : getIsDimensionInitializerOfIncidences(EdgeDirection.IN)) {
			last = ((CGArrayInitializerImpl) idio.getAlpha()).generateCode(jcg,
					bw, indentLevel);
		}

		return last;
	}
}
