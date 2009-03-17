package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMemberOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfBody;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.Switch;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BlockImpl;

public class CGBlockImpl extends BlockImpl implements CGStatement {

	public CGBlockImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		indentLevel++;

		bw.append("{\n");

		// first the enum constants (0,*)
		boolean first = true;
		for (IsMemberOf imo : getIsMemberOfIncidences(EdgeDirection.IN)) {
			CGMember m = (CGMember) imo.getAlpha();
			if (!(m instanceof EnumConstant)) {
				continue;
			}
			if (first) {
				JavaCodeGenerator.indent(bw, indentLevel);
				first = false;
			} else {
				bw.append(", ");
			}
			m.generateCode(jcg, bw, indentLevel);
		}
		if (!first) {
			bw.append(";\n\n");
		}

		// then every member except enum constants (0,*)
		first = true;
		for (IsMemberOf imo : getIsMemberOfIncidences(EdgeDirection.IN)) {
			CGMember m = (CGMember) imo.getAlpha();
			if (m instanceof EnumConstant) {
				continue;
			} else {
				JavaCodeGenerator.indent(bw, indentLevel);
				Vertex last = m.generateCode(jcg, bw, indentLevel);
				if (last instanceof Block || last instanceof MethodDeclaration) {
					bw.append("\n\n");
				} else {
					bw.append(";\n\n");
				}
			}
		}
		if (!first) {
			bw.append('\n');
		}

		for (IsStatementOfBody isob : getIsStatementOfBodyIncidences(EdgeDirection.IN)) {
			CGStatement s = (CGStatement) isob.getAlpha();
			JavaCodeGenerator.indent(bw, indentLevel);
			Vertex last = s.generateCode(jcg, bw, indentLevel);
			if (!(last instanceof Block || last instanceof Switch)) {
				bw.append(';');
			}
			bw.append("\n");
		}

		indentLevel--;
		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append("}");

		return this;
	}
}
