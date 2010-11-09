/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfType;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.ClassDefinitionImpl;

public class CGClassDefinitionImpl extends ClassDefinitionImpl implements
		CGType {

	public CGClassDefinitionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	// @Override
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

		// write all modifiers
		for (IsModifierOfClass imoc : getIsModifierOfClassIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}
		bw.append("class ");

		// write the class name
		((CGIdentifierImpl) getFirstIsClassNameOfIncidence(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

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
		IsSuperClassOfClass iscoc = getFirstIsSuperClassOfClassIncidence(EdgeDirection.IN);
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
		return ((CGBlockImpl) getFirstIsClassBlockOfIncidence(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}

}
