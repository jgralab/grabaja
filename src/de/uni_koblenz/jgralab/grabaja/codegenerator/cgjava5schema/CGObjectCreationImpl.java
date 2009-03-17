package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ObjectCreationImpl;

public class CGObjectCreationImpl extends ObjectCreationImpl implements
		CGExpression {

	public CGObjectCreationImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		bw.append("new ");

		// the type (1,1)
		((CGTypeSpecification) getFirstIsTypeOfObject(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		// the constructor method invocation (1,1)
		((CGMethodInvocationImpl) getFirstIsConstructorInvocationOf(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
