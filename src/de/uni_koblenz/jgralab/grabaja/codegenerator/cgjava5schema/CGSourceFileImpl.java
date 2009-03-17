package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.SourceFileImpl;

public class CGSourceFileImpl extends SourceFileImpl implements CodeGenerator {

	public CGSourceFileImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// nothing to do here...

	}

}
