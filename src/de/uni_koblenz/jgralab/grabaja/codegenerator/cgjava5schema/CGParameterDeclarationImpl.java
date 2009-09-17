package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfParameter;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.ParameterDeclarationImpl;

public class CGParameterDeclarationImpl extends ParameterDeclarationImpl
		implements CGParameterDeclaration {

	public CGParameterDeclarationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// first the modifier (0,1)
		IsModifierOfParameter imop = getFirstIsModifierOfParameter(EdgeDirection.IN);
		if (imop != null) {
			((CGModifierImpl) imop.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}

		// then the type spec (1,1)
		if (getFirstIsTypeOfParameter(EdgeDirection.IN)!=null) {
			((CGTypeSpecification) getFirstIsTypeOfParameter(EdgeDirection.IN)
					.getAlpha()).generateCode(jcg, bw, indentLevel);
		} else {
			bw.append("/* Missing Parameter Type */ Object");
		}
		bw.append(' ');

		// then the name (1,1)
		return ((CGIdentifierImpl) getFirstIsParameterNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}

}
