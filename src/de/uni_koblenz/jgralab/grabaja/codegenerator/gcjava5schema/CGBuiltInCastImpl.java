package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BuiltInCastImpl;

public class CGBuiltInCastImpl extends BuiltInCastImpl implements CGExpression {

	public CGBuiltInCastImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append('(');

		// now the type (1,1)
		((CGBuiltInTypeImpl) getFirstIsCastedBuiltInTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		bw.append(") ");

		// now the casted expression (1,1)
		((CGExpression) getFirstIsCastedValueOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}
}
