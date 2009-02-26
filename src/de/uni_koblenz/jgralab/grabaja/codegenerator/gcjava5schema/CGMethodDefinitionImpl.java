package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.MethodDefinitionImpl;

public class CGMethodDefinitionImpl extends MethodDefinitionImpl implements
		CGMember {

	public CGMethodDefinitionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {

		// first the modifiers
		for (IsModifierOfMethod imom : getIsModifierOfMethodIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imom.getAlpha()).generateCode(bw, indentLevel);
			bw.append(' ');
		}

		// then the return type (exactly one)
		((CGTypeSpecification) getFirstIsReturnTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);
		bw.append(' ');

		// then the name (exactly one)
		((CGIdentifierImpl) getFirstIsNameOfMethod(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);

		// now comes the parameter list as many ParameterDeclarations
		bw.append('(');
		boolean first = true;
		for (IsParameterOfMethod ipom : getIsParameterOfMethodIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGParameterDeclarationImpl) ipom.getAlpha()).generateCode(bw,
					indentLevel);
		}
		bw.append(") ");

		// the last part is the method's body (exactly one)
		((CGBlockImpl) getFirstIsBodyOfMethod(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}
}
