package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ForEachClauseImpl;

public class CGForEachClauseImpl extends ForEachClauseImpl implements CGForHead {

	public CGForEachClauseImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// the parameter decl (1,1)
		((CGParameterDeclarationImpl) getFirstIsParameterOfForEachClause(
				EdgeDirection.IN).getAlpha()).generateCode(bw, indentLevel);

		bw.append(" : ");

		// the enumerable (1,1)
		((CGExpression) getFirstIsEnumerableOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}