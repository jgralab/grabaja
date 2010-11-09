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
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPartOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.JavaPackageImpl;

public class CGJavaPackageImpl extends JavaPackageImpl implements CodeGenerator {

	public CGJavaPackageImpl(int id, Graph g) {
		super(id, g);
	}

	// @Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		// create the directory
		String relativeDirName = _fullyQualifiedName.replaceAll(
				Pattern.quote("."), Matcher.quoteReplacement(File.separator));
		File pkg = new File(jcg.getBaseDirectory().getCanonicalPath()
				+ File.separator + relativeDirName);
		pkg.mkdirs();

		// create code for the translation units (0,*)
		for (IsPartOf ipo : getIsPartOfIncidences(EdgeDirection.IN)) {
			CGTranslationUnitImpl tu = (CGTranslationUnitImpl) ipo.getAlpha();
			tu.setDirectory(pkg.getAbsolutePath());
			tu.generateCode(jcg, bw, indentLevel);
		}
		return this;
	}

}
