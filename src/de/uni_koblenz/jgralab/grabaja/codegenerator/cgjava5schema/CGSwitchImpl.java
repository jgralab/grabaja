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
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCaseOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDefaultCaseOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.SwitchImpl;

public class CGSwitchImpl extends SwitchImpl implements CGStatement {

	public CGSwitchImpl(int id, Graph g) {
		super(id, g);
	}

	//@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		bw.append("switch (");

		// the argument condition (1,1)
		((CGExpression) getFirstIsSwitchArgumentOf(EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(") {\n");

		// the cases (0,*)
		for (IsCaseOf c : getIsCaseOfIncidences(EdgeDirection.IN)) {
			JavaCodeGenerator.indent(bw, indentLevel);
			((CGCaseImpl) c.getAlpha()).generateCode(jcg, bw, indentLevel + 1);
			bw.append('\n');
		}

		// the default case (0,1)
		IsDefaultCaseOf idco = getFirstIsDefaultCaseOf(EdgeDirection.IN);
		if (idco != null) {
			JavaCodeGenerator.indent(bw, indentLevel);
			((CGDefaultImpl) idco.getAlpha()).generateCode(jcg, bw,
					indentLevel + 1);
			bw.append('\n');
		}

		JavaCodeGenerator.indent(bw, indentLevel);
		bw.append('}');

		return this;
	}

}
