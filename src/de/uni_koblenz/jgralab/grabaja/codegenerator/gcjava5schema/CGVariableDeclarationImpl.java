package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInitializerOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.VariableDeclarationImpl;

public class CGVariableDeclarationImpl extends VariableDeclarationImpl
		implements CGStatement, CGFieldDeclaration {

	public CGVariableDeclarationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {

		// first the modifiers
		for (IsModifierOfVariable imov : getIsModifierOfVariableIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imov.getAlpha()).generateCode(bw, indentLevel);
			bw.append(' ');
		}

		// then the type (exactly one)
		((CGTypeSpecification) getFirstIsTypeOfVariable(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
		bw.append(' ');

		// then the name (exactly one)
		((CGIdentifierImpl) getFirstIsVariableNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// then the initializer (0 or 1)
		IsInitializerOfVariable iiov = getFirstIsInitializerOfVariable(EdgeDirection.IN);
		if (iiov != null) {
			bw.append(" = ");
			((CGExpression) iiov.getAlpha()).generateCode(bw, indentLevel);
		}
	}

}
