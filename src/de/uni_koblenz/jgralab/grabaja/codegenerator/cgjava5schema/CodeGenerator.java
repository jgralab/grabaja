package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;

public interface CodeGenerator extends Vertex {
	/**
	 * Generate code for this java5 vertex.
	 *
	 * @param jcg
	 *            the {@link JavaCodeGenerator}
	 * @param bw
	 *            a {@link BufferedWriter} for the output
	 * @param indentLevel
	 *            the indentation level for this element
	 * @return the last Vertex for which something was written to
	 *         <code>bw</code>
	 * @throws IOException
	 *             on IO errors
	 */
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException;
}
