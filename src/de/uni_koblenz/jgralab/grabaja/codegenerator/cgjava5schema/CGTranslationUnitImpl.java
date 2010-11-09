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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSourceUsageIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceFile;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.TranslationUnitImpl;

public class CGTranslationUnitImpl extends TranslationUnitImpl implements
		CodeGenerator {

	private String cg_tu_directory;

	public CGTranslationUnitImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	void setDirectory(String dir) {
		cg_tu_directory = dir;
	}

	// @Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// each TU has exactly one SourceFile
		SourceFile sf = (SourceFile) getFirstIsPrimarySourceForIncidence(
				EdgeDirection.IN).getAlpha();
		String fileName = cg_tu_directory
				+ File.separator
				+ sf.get_name().replaceAll(
						".*" + Pattern.quote(File.separator), "");
		bw = new BufferedWriter(new FileWriter(fileName));

		Vertex last = this;

		for (IsSourceUsageIn isui : getIsSourceUsageInIncidences(EdgeDirection.IN)) {
			last = ((CGSourceUsageImpl) isui.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
		bw.flush();
		bw.close();

		return last;
	}
}
