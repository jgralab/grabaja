package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ClassDefinitionImpl;

public class CGClassDefinitionImpl extends ClassDefinitionImpl implements
		CGType {

	public CGClassDefinitionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// write all modifiers
		for (IsModifierOfClass imoc : getIsModifierOfClassIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(bw, indentLevel);
		}
		bw.append(" class ");

		// write the class name
		((CGIdentifierImpl) getFirstIsClassNameOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
		bw.append(' ');

		// TODO: here come extended classes and implemented interfaces...

		// write the class block (there's exactly one)
		((CGBlockImpl) getFirstIsClassBlockOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}
