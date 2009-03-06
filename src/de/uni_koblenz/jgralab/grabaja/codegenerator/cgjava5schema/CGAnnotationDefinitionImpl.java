package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMetaAnnotationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfAnnotation;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.AnnotationDefinitionImpl;

public class CGAnnotationDefinitionImpl extends AnnotationDefinitionImpl
		implements CGType {

	public CGAnnotationDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// first the meta annotations (annotations annotating this annotation
		// def) (0,*)
		for (IsMetaAnnotationOf imao : getIsMetaAnnotationOfIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) imao.getAlpha()).generateCode(bw, indentLevel);
		}

		// write all modifiers (0,*)
		for (IsModifierOfAnnotation imoa : getIsModifierOfAnnotationIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoa.getAlpha()).generateCode(bw, indentLevel);
			bw.append(' ');
		}
		bw.append("@interface ");

		// write the annotation name (1,1)
		((CGIdentifierImpl) getFirstIsAnnotationDefinitionNameOf(
				EdgeDirection.IN).getAlpha()).generateCode(bw, indentLevel);

		bw.append(' ');

		// the body block (1,1)
		((CGBlockImpl) getFirstIsAnnotationBlockOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}
