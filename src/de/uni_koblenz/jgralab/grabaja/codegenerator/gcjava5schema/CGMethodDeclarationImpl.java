package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfMember;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.MethodDeclarationImpl;

public class CGMethodDeclarationImpl extends MethodDeclarationImpl implements
		CGMember {

	public CGMethodDeclarationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		generateMethodDeclarationCodeFor(this, bw, indentLevel);
		bw.append(";");

	}

	static void generateMethodDeclarationCodeFor(MethodDeclaration md,
			BufferedWriter bw, int indentLevel) throws IOException {
		// first the annotations (0,*)
		for (IsAnnotationOfMember iaot : md
				.getIsAnnotationOfMemberIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) iaot.getAlpha()).generateCode(bw, indentLevel);
		}

		// the modifiers
		for (IsModifierOfMethod imom : md
				.getIsModifierOfMethodIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imom.getAlpha()).generateCode(bw, indentLevel);
			bw.append(' ');
		}

		// then the return type (exactly one)
		((CGTypeSpecification) md.getFirstIsReturnTypeOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// then the type parameters (0,*)
		boolean first = true;
		for (IsTypeParameterOfMethod itpom : md
				.getIsTypeParameterOfMethodIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append("<");
				first = false;
			} else {
				bw.append(", ");
			}
			((CGTypeParameterDeclarationImpl) itpom.getAlpha()).generateCode(
					bw, indentLevel);
		}
		if (!first) {
			bw.append('>');
		}

		bw.append(' ');

		// then the name (exactly one)
		((CGIdentifierImpl) md.getFirstIsNameOfMethod(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// now comes the parameter list as many ParameterDeclarations
		bw.append('(');
		first = true;
		for (IsParameterOfMethod ipom : md
				.getIsParameterOfMethodIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGParameterDeclarationImpl) ipom.getAlpha()).generateCode(bw,
					indentLevel);
		}
		bw.append(')');

		// now the exceptions (0,*)
		first = true;
		for (IsExceptionThrownByMethod ietbm : md
				.getIsExceptionThrownByMethodIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append(" throws ");
			} else {
				bw.append(", ");
			}
			((CGTypeSpecification) ietbm.getAlpha()).generateCode(bw,
					indentLevel);
		}
	}

}
