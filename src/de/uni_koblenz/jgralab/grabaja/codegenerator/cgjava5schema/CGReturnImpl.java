package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsReturnedBy;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.ReturnImpl;

public class CGReturnImpl extends ReturnImpl implements CGStatement {

	public CGReturnImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append("return");

		Vertex last = this;

		// now the returned exp (0 or 1)
		IsReturnedBy irb = getFirstIsReturnedBy(EdgeDirection.IN);
		if (irb != null) {
			bw.append(' ');
			last = ((CGExpression) irb.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
		return last;
	}

}
