package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;

public interface CodeGenerator {
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw, int indentLevel)
			throws IOException;
}
