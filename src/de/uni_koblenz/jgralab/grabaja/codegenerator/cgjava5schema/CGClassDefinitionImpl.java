package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfType;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.ClassDefinitionImpl;

public class CGClassDefinitionImpl extends ClassDefinitionImpl implements
		CGType {

	public CGClassDefinitionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// first the annotations (0,*)
		for (IsAnnotationOfType iaot : getIsAnnotationOfTypeIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) iaot.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// write all modifiers
		for (IsModifierOfClass imoc : getIsModifierOfClassIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}
		bw.append("class ");

		// write the class name
		((CGIdentifierImpl) getFirstIsClassNameOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		// now the type parameters (0,*)
		boolean first = true;
		for (IsTypeParameterOfClass itpoc : getIsTypeParameterOfClassIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append("<");
			} else {
				bw.append(", ");
			}
			((CGTypeParameterDeclarationImpl) itpoc.getAlpha()).generateCode(
					jcg, bw, indentLevel);
		}
		if (!first) {
			bw.append(">");
		}

		// the superclass (0,1)
		IsSuperClassOfClass iscoc = getFirstIsSuperClassOfClass(EdgeDirection.IN);
		if (iscoc != null) {
			bw.append(" extends ");
			((CGTypeSpecification) iscoc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// the interfaces (0,*)
		first = true;
		for (IsInterfaceOfClass iioc : getIsInterfaceOfClassIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append(" implements ");
			} else {
				bw.append(", ");
			}
			((CGTypeSpecification) iioc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		bw.append(' ');

		// write the class block (there's exactly one)
		((CGBlockImpl) getFirstIsClassBlockOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
