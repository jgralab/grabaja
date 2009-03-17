package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDefaultValueOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfAnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.AnnotationFieldImpl;

public class CGAnnotationFieldImpl extends AnnotationFieldImpl implements
		CGMember {

	public CGAnnotationFieldImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// modifiers (0,*)
		for (IsModifierOfAnnotationField imoaf : getIsModifierOfAnnotationFieldIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoaf.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}

		// the type (1,1)
		((CGTypeSpecification) getFirstIsTypeOfAnnotationField(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

		bw.append(' ');

		// the name (1,1)
		((CGIdentifierImpl) getFirstIsAnnotationFieldNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
		bw.append("()");

		// the default value (TODO: Currently it's (1,1), but (0,1) would be
		// correct)
		IsDefaultValueOf idvo = getFirstIsDefaultValueOf(EdgeDirection.IN);
		if (idvo != null) {
			bw.append(" default ");
			((CGExpression) idvo.getAlpha()).generateCode(jcg, bw, indentLevel);
		}
	}

}
