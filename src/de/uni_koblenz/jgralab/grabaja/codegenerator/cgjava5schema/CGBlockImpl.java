package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMemberOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfBody;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.StaticConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.StaticInitializerDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.BlockImpl;

public class CGBlockImpl extends BlockImpl implements CGStatement {

	public CGBlockImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
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
			m.generateCode(bw, indentLevel);
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
			} else if (m instanceof MethodDeclaration
					|| m instanceof ClassDefinition
					|| m instanceof InterfaceDefinition
					|| m instanceof AnnotationDefinition
					|| m instanceof StaticConstructorDefinition
					|| m instanceof StaticInitializerDefinition) {
				JavaCodeGenerator.indent(bw, indentLevel);
				m.generateCode(bw, indentLevel);
				bw.append("\n\n");
			} else {
				JavaCodeGenerator.indent(bw, indentLevel);
				m.generateCode(bw, indentLevel);
				bw.append(";\n\n");
			}
		}
		if (!first) {
			bw.append('\n');
		}

		for (IsStatementOfBody isob : getIsStatementOfBodyIncidences(EdgeDirection.IN)) {
			CGStatement s = (CGStatement) isob.getAlpha();
			JavaCodeGenerator.indent(bw, indentLevel);
			s.generateCode(bw, indentLevel);
			if (!JavaCodeGenerator.isBlockConstruct(s)) {
				bw.append(';');
			}
			bw.append("\n");
		}

		indentLevel--;
		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append("}");
	}
}
