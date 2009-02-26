package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

public interface CodeGenerator {
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException;
}
