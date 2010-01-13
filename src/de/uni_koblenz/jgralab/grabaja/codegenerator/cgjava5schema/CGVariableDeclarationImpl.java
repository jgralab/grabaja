package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInitializerOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.VariableDeclarationImpl;

public class CGVariableDeclarationImpl extends VariableDeclarationImpl
		implements CGStatement, CGFieldDeclaration {

	public CGVariableDeclarationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// first the modifiers
		for (IsModifierOfVariable imov : getIsModifierOfVariableIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imov.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}

		// then the type (exactly one)
		if (getFirstIsTypeOfVariable(EdgeDirection.IN) != null) {
			((CGTypeSpecification) getFirstIsTypeOfVariable(EdgeDirection.IN)
					.getAlpha()).generateCode(jcg, bw, indentLevel);
		} else {
			bw.append("/* Missing type */ Object");
		}
		bw.append(' ');

		// then the name (exactly one)
		Vertex last = ((CGIdentifierImpl) getFirstIsVariableNameOf(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		// then the initializer (0 or 1)
		IsInitializerOfVariable iiov = getFirstIsInitializerOfVariable(EdgeDirection.IN);
		if (iiov != null) {
			bw.append(" = ");
			last = ((CGExpression) iiov.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		return last;
	}

}
