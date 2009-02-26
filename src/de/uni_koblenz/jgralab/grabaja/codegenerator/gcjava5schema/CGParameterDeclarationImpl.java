package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ParameterDeclarationImpl;

public class CGParameterDeclarationImpl extends ParameterDeclarationImpl implements
		CGFieldDeclaration {

	public CGParameterDeclarationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// TODO Auto-generated method stub

	}

}
