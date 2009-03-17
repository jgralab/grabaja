package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsForConditionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsIteratorOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsRunVariableInitializationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.TraditionalForClauseImpl;

public class CGTraditionalForClauseImpl extends TraditionalForClauseImpl
		implements CGForHead {

	public CGTraditionalForClauseImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		Vertex last = this;

		// the run var inits
		boolean first = true;
		for (IsRunVariableInitializationOf irvio : getIsRunVariableInitializationOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			last = ((CGStatement) irvio.getAlpha()).generateCode(jcg, bw, 0);
		}
		bw.append("; ");

		// now the condition (0 or 1)
		IsForConditionOf ifco = getFirstIsForConditionOf(EdgeDirection.IN);
		if (ifco != null) {
			last = ((CGExpression) ifco.getAlpha()).generateCode(jcg, bw, 0);
		}
		bw.append("; ");

		// now the iterators
		for (IsIteratorOf iio : getIsIteratorOfIncidences(EdgeDirection.IN)) {
			last = ((CGExpression) iio.getAlpha()).generateCode(jcg, bw, 0);
		}

		return last;
	}

}
