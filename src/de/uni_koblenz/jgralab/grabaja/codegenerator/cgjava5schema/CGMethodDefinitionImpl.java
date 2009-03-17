package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.MethodDefinitionImpl;

public class CGMethodDefinitionImpl extends MethodDefinitionImpl implements
		CGMember {

	public CGMethodDefinitionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		CGMethodDeclarationImpl.generateMethodDeclarationCodeFor(jcg, this, bw,
				indentLevel);
		bw.append(' ');

		// the last part is the method's body (exactly one)
		return ((CGBlockImpl) getFirstIsBodyOfMethod(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}
}
