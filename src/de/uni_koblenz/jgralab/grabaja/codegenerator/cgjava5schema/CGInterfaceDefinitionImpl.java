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
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.InterfaceDefinitionImpl;

public class CGInterfaceDefinitionImpl extends InterfaceDefinitionImpl
		implements CGType {

	public CGInterfaceDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	//@Override
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

		// write all modifiers (0,*)
		for (IsModifierOfInterface imoc : getIsModifierOfInterfaceIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}
		bw.append("interface ");

		// write the iface name (1,1)
		((CGIdentifierImpl) getFirstIsInterfaceNameOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);

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
					jcg, bw, indentLevel);
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
			((CGTypeSpecification) iscoi.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		bw.append(' ');

		// the body block (1,1)
		return ((CGBlockImpl) getFirstIsInterfaceBlockOf(EdgeDirection.IN)
				.getAlpha()).generateCode(jcg, bw, indentLevel);
	}

}
