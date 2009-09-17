package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfType;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfEnum;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.EnumDefinitionImpl;

public class CGEnumDefinitionImpl extends EnumDefinitionImpl implements CGType {

	public CGEnumDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// first the annotations (0,*)
		for (IsAnnotationOfType iaot : getIsAnnotationOfTypeIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) iaot.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// modifiers (0,*)
		for (IsModifierOfEnum imoe : getIsModifierOfEnumIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoe.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		bw.append(" enum ");

		// the name (1,1)
		((CGIdentifierImpl) getFirstIsEnumNameOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(' ');

		// the block (1,1)
		return ((CGBlockImpl) getFirstIsEnumBlockOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}

}
