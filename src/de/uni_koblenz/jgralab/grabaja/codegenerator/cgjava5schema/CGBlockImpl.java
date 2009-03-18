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
		if (!jcg.generationWanted(this)) {
			return null;
		}

		indentLevel++;

		bw.append("{\n");
		JavaCodeGenerator.indent(bw, indentLevel);

		Vertex last = null;

		// first the enum constants (0,*)
		for (IsMemberOf imo : getIsMemberOfIncidences(EdgeDirection.IN)) {
			CGMember m = (CGMember) imo.getAlpha();
			if (!(m instanceof EnumConstant)) {
				continue;
			}
			if (last != null) {
				bw.append(", ");
			}
			last = m.generateCode(jcg, bw, indentLevel);
		}
		if (last != null) {
			bw.append(";\n\n");
			JavaCodeGenerator.indent(bw, indentLevel);
		}

		last = null;
		// then every member except enum constants (0,*)
		for (IsMemberOf imo : getIsMemberOfIncidences(EdgeDirection.IN)) {
			CGMember m = (CGMember) imo.getAlpha();
			if (m instanceof EnumConstant) {
				continue;
			} else {
				if (last != null) {
					bw.append("\n\n");
					JavaCodeGenerator.indent(bw, indentLevel);
				}
				last = m.generateCode(jcg, bw, indentLevel);
				if (last != null) {
					if (!(last instanceof Block || last instanceof MethodDeclaration)) {
						bw.append(";");
					}
				}
			}
		}

		for (IsStatementOfBody isob : getIsStatementOfBodyIncidences(EdgeDirection.IN)) {
			CGStatement s = (CGStatement) isob.getAlpha();
			if (last != null) {
				bw.append("\n");
				JavaCodeGenerator.indent(bw, indentLevel);
			}
			last = s.generateCode(jcg, bw, indentLevel);
			if (last != null) {
				if (!(last instanceof Block || last instanceof Switch)) {
					bw.append(';');
				}
			}
		}

		indentLevel--;
		bw.append("\n");
		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append("}");
		return this;
	}
}
