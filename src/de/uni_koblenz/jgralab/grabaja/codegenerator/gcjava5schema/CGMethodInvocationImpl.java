package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArgumentOfMethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMethodContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.MethodInvocationImpl;

public class CGMethodInvocationImpl extends MethodInvocationImpl implements
		CGExpression {

	public CGMethodInvocationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// first the container (0,1)
		IsMethodContainerOf imco = getFirstIsMethodContainerOf(EdgeDirection.IN);
		if (imco != null) {
			((CGExpression) imco.getAlpha()).generateCode(bw, indentLevel);
			bw.append('.');
		}

		// then the method name (1,1)
		((CGIdentifierImpl) getFirstIsNameOfInvokedMethod(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// TODO: Do I need to do something with the `type' attribute?

		bw.append('(');

		// now the parameters (0.*)
		boolean first = true;
		for (IsArgumentOfMethodInvocation iaomi : getIsArgumentOfMethodInvocationIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGExpression) iaomi.getAlpha()).generateCode(bw, indentLevel);
		}
		bw.append(')');
	}

}
