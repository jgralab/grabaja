package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ConstructorDefinitionImpl;

public class CGConstructorDefinitionImpl extends ConstructorDefinitionImpl
		implements CGMember {

	public CGConstructorDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// the modifiers (0,*)
		for (IsModifierOfConstructor imoc : getIsModifierOfConstructorIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}

		// the name (1,1)
		((CGIdentifierImpl) getFirstIsNameOfConstructor(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		// the type parameters (0,*)
		boolean first = true;
		for (IsTypeParameterOfConstructor itpoc : getIsTypeParameterOfConstructorIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append('<');
				first = false;
			} else {
				bw.append(", ");
			}
			((CGTypeParameterDeclarationImpl) itpoc.getAlpha()).generateCode(
					jcg, bw, indentLevel);
		}

		// the parameters (0,*)
		first = true;
		bw.append('(');
		for (IsParameterOfConstructor ipoc : getIsParameterOfConstructorIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGParameterDeclarationImpl) ipoc.getAlpha()).generateCode(jcg,
					bw, indentLevel);
		}
		bw.append(')');

		// the exceptions (0,*)
		first = true;
		for (IsExceptionThrownByConstructor ietbc : getIsExceptionThrownByConstructorIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append(" throws ");
			} else {
				bw.append(", ");
			}
			((CGTypeSpecification) ietbc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// the block (1,1)
		return ((CGBlockImpl) getFirstIsBodyOfConstructor(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}

}
