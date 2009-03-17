package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsArgumentOfMethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMethodContainerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsNameOfInvokedMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.MethodInvocationImpl;

public class CGMethodInvocationImpl extends MethodInvocationImpl implements
		CGExpression {

	public CGMethodInvocationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// first the container (0,1)
		IsMethodContainerOf imco = getFirstIsMethodContainerOf(EdgeDirection.IN);
		if (imco != null) {
			((CGExpression) imco.getAlpha()).generateCode(jcg, bw, indentLevel);
			bw.append('.');
		}

		// TODO: Do I need to do something with the `type' attribute?

		// then the method name (0,1), 0 in the case of constructors
		IsNameOfInvokedMethod inoim = getFirstIsNameOfInvokedMethod(EdgeDirection.IN);
		if (inoim != null) {
			((CGIdentifierImpl) inoim.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		bw.append('(');

		// now the parameters (0.*)
		boolean first = true;
		for (IsArgumentOfMethodInvocation iaomi : getIsArgumentOfMethodInvocationIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGExpression) iaomi.getAlpha())
					.generateCode(jcg, bw, indentLevel);
		}
		bw.append(')');
	}

}
