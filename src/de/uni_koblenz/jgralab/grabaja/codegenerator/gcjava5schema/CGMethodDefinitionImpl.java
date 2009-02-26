package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.MethodDefinitionImpl;

public class CGMethodDefinitionImpl extends MethodDefinitionImpl implements
		CGMember {

	public CGMethodDefinitionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		CGMethodDeclarationImpl.generateMethodDeclarationCodeFor(this, bw,
				indentLevel);
		bw.append(' ');

		// the last part is the method's body (exactly one)
		((CGBlockImpl) getFirstIsBodyOfMethod(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}
}
