package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfType;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.InterfaceDefinitionImpl;

public class CGInterfaceDefinitionImpl extends InterfaceDefinitionImpl
		implements CGType {

	public CGInterfaceDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// first the annotations (0,*)
		for (IsAnnotationOfType iaot : getIsAnnotationOfTypeIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) iaot.getAlpha()).generateCode(bw, indentLevel);
		}

		// write all modifiers (0,*)
		for (IsModifierOfInterface imoc : getIsModifierOfInterfaceIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(bw, indentLevel);
		}
		bw.append(" interface ");

		// write the iface name (1,1)
		((CGIdentifierImpl) getFirstIsInterfaceNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(bw, indentLevel);

		// now the type parameters (0,*)
		boolean first = true;
		for (IsTypeParameterOfInterface itpoc : getIsTypeParameterOfInterfaceIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append("<");
			} else {
				bw.append(", ");
			}
			((CGTypeParameterDeclarationImpl) itpoc.getAlpha()).generateCode(
					bw, indentLevel);
		}
		if (!first) {
			bw.append(">");
		}

		// super-interfaces (0,*)
		first = true;
		for (IsSuperClassOfInterface iscoi : getIsSuperClassOfInterfaceIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append(" extends ");
			} else {
				bw.append(", ");
			}
			((CGTypeSpecification) iscoi.getAlpha()).generateCode(bw,
					indentLevel);
		}

		bw.append(' ');

		// the body block (1,1)
		((CGBlockImpl) getFirstIsInterfaceBlockOf(EdgeDirection.IN).getAlpha())
				.generateCode(bw, indentLevel);
	}

}
