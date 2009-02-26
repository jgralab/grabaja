package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
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
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		bw.append('(');

		// now the run var inits
		boolean first = true;
		for (IsRunVariableInitializationOf irvio : getIsRunVariableInitializationOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGStatement) irvio.getAlpha()).generateCode(bw, 0);
		}
		bw.append("; ");

		// now the condition (0 or 1)
		IsForConditionOf ifco = getFirstIsForConditionOf(EdgeDirection.IN);
		if (ifco != null) {
			((CGExpression) ifco.getAlpha()).generateCode(bw, 0);
		}
		bw.append("; ");

		// now the iterators
		for (IsIteratorOf iio : getIsIteratorOfIncidences(EdgeDirection.IN)) {
			((CGExpression) iio.getAlpha()).generateCode(bw, 0);
		}

		bw.append(')');
	}

}
