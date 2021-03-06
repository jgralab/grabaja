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
import de.uni_koblenz.jgralab.grabaja.java5schema.IsForConditionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsIteratorOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsRunVariableInitializationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.TraditionalForClauseImpl;

public class CGTraditionalForClauseImpl extends TraditionalForClauseImpl
		implements CGForHead {

	public CGTraditionalForClauseImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	// @Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		Vertex last = this;

		// the run var inits
		boolean first = true;
		for (IsRunVariableInitializationOf irvio : getIsRunVariableInitializationOfIncidences(EdgeDirection.IN)) {
			if (first) {
				first = false;
			} else {
				bw.append(", ");
			}
			last = ((CGStatement) irvio.getAlpha()).generateCode(jcg, bw, 0);
		}
		bw.append("; ");

		// now the condition (0 or 1)
		IsForConditionOf ifco = getFirstIsForConditionOfIncidence(EdgeDirection.IN);
		if (ifco != null) {
			last = ((CGExpression) ifco.getAlpha()).generateCode(jcg, bw, 0);
		}
		bw.append("; ");

		// now the iterators
		for (IsIteratorOf iio : getIsIteratorOfIncidences(EdgeDirection.IN)) {
			last = ((CGExpression) iio.getAlpha()).generateCode(jcg, bw, 0);
		}

		return last;
	}

}
