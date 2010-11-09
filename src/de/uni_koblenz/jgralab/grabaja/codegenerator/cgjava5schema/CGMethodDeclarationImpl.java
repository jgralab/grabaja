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
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfMember;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExceptionThrownByMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeParameterOfMethod;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.MethodDeclarationImpl;

public class CGMethodDeclarationImpl extends MethodDeclarationImpl implements
		CGMember {

	public CGMethodDeclarationImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	// @Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		generateMethodDeclarationCodeFor(jcg, this, bw, indentLevel);
		bw.append(";");
		return this;
	}

	static void generateMethodDeclarationCodeFor(JavaCodeGenerator jcg,
			MethodDeclaration md, BufferedWriter bw, int indentLevel)
			throws IOException {
		// first the annotations (0,*)
		for (IsAnnotationOfMember iaot : md
				.getIsAnnotationOfMemberIncidences(EdgeDirection.IN)) {
			((CGAnnotationImpl) iaot.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}

		// the modifiers
		for (IsModifierOfMethod imom : md
				.getIsModifierOfMethodIncidences(EdgeDirection.IN)) {
			((CGModifierImpl) imom.getAlpha()).generateCode(jcg, bw,
					indentLevel);
			bw.append(' ');
		}

		// then the return type (exactly one)
		if (md.getFirstIsReturnTypeOfIncidence(EdgeDirection.IN) != null) {
			((CGTypeSpecification) md.getFirstIsReturnTypeOfIncidence(
					EdgeDirection.IN).getAlpha()).generateCode(jcg, bw,
					indentLevel);
		} else {
			bw.append("/*Missing return type */ Object");
		}

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
					jcg, bw, indentLevel);
		}
		if (!first) {
			bw.append('>');
		}

		bw.append(' ');

		// then the name (exactly one)
		((CGIdentifierImpl) md
				.getFirstIsNameOfMethodIncidence(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

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
			((CGParameterDeclaration) ipom.getAlpha()).generateCode(jcg, bw,
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
			((CGTypeSpecification) ietbm.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
	}

}
