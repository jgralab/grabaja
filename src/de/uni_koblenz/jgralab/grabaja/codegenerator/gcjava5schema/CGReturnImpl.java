package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsReturnedBy;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ReturnImpl;

public class CGReturnImpl extends ReturnImpl implements CGStatement {

	public CGReturnImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append("return");

		// now the returned exp (0 or 1)
		IsReturnedBy irb = getFirstIsReturnedBy(EdgeDirection.IN);
		if (irb != null) {
			bw.append(' ');
			((CGExpression) irb.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
