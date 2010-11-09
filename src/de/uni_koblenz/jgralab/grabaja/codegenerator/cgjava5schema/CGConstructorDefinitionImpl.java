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
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfConstructor;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.ConstructorDefinitionImpl;

public class CGConstructorDefinitionImpl extends ConstructorDefinitionImpl
		implements CGMember {

	public CGConstructorDefinitionImpl(int id, Graph g) {
		super(id, g);
	}

	// @Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// the modifiers (0,*)
		for (IsModifierOfConstructor imoc : getIsModifierOfConstructorIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imoc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}

		// the name (1,1)
		((CGIdentifierImpl) getFirstIsNameOfConstructorIncidence(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		// the type parameters (0,*)
		boolean first = true;
		for (IsTypeParameterOfConstructor itpoc : getIsTypeParameterOfConstructorIncidences(EdgeDirection.IN)) {
			if (first) {
				bw.append('<');
				first = false;
			} else {
				bw.append(", ");
			}
			((CGTypeParameterDeclarationImpl) itpoc.getAlpha()).generateCode(
					jcg, bw, indentLevel);
		}

		// the parameters (0,*)
		first = true;
		bw.append('(');
		for (IsParameterOfConstructor ipoc : getIsParameterOfConstructorIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			((CGParameterDeclarationImpl) ipoc.getAlpha()).generateCode(jcg,
					bw, indentLevel);
		}
		bw.append(')');

		// the exceptions (0,*)
		first = true;
		for (IsExceptionThrownByConstructor ietbc : getIsExceptionThrownByConstructorIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
				bw.append(" throws ");
			} else {
				bw.append(", ");
			}
			((CGTypeSpecification) ietbc.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// the block (1,1)
		return ((CGBlockImpl) getFirstIsBodyOfConstructorIncidence(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);
	}

}
