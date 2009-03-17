package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BuiltInCastImpl;

public class CGBuiltInCastImpl extends BuiltInCastImpl implements CGExpression {

	public CGBuiltInCastImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append('(');

		// now the type (1,1)
		((CGBuiltInTypeImpl) getFirstIsCastedBuiltInTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		bw.append(") ");

		// now the casted expression (1,1)
		return ((CGExpression) getFirstIsCastedValueOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}
}
