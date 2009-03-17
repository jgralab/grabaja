package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPackageOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ProgramImpl;

public class CGProgramImpl extends ProgramImpl implements CodeGenerator {

	public CGProgramImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		Vertex last = this;
		for (IsPackageOf ipo : getIsPackageOfIncidences(EdgeDirection.IN)) {
			last = ((CGJavaPackageImpl) ipo.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
		return last;
	}
}
