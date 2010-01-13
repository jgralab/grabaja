package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBreakTargetOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Label;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.BreakImpl;

public class CGBreakImpl extends BreakImpl implements CGStatement {

	public CGBreakImpl(int id, Graph g) {
		super(id, g);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append("break");

		// the label (0,1)
		IsBreakTargetOf ibto = getFirstIsBreakTargetOf(EdgeDirection.IN);
		if (ibto != null) {
			bw.append(' ');
			// We need to write the identifier on our own here, else we'd
			// produce an endless recursion
			bw.append(((Identifier) ((Label) ibto.getAlpha())
					.getFirstIsLabelNameOf(EdgeDirection.IN).getAlpha())
					.get_name());
		}

		return this;
	}

}
