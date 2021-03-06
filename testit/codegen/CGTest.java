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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.exception.GraphIOException;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDefinition;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.greql.GreqlQuery;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;

public class CGTest {

	public static void main(String[] args) throws GraphIOException,
			IOException, InterruptedException {
		String outputDir = "testit" + File.separator + "cgoutput";
		String graphFile = outputDir + File.separator + "cginput.tg";
		String dotFile = outputDir + File.separator + "cginput.dot";

		File outDir = new File(outputDir);
		if (outDir.exists()) {
			if (deleteFile(outDir)) {
				System.out.println("Deleted the old output directory.");
			} else {
				System.out.println("Couldn't delete the old output directory.");
			}
		}
		outDir.mkdir();

		// Extract
		JavaExtractor.main(new String[] { "-o", graphFile, "-complete",
				"testit" + File.separator + "cginput" });
		JavaCodeGenerator jcg = new JavaCodeGenerator(graphFile, outputDir);

		// Generate Code

		// markMBazMethod(jcg);
		BooleanGraphMarker m = null;
		m = markClassDefinition(jcg, "TestClass");
		// m = markVarLenMethMethod(jcg);
		jcg.setCgElements(m);
		// markAll(jcg, Switch.class);
		jcg.generateCode();

		markNeededEdges(jcg.getJavaGraph(), m);

		// Dot
		Tg2Dot t2d = new Tg2Dot();
		t2d.setGraph(jcg.getJavaGraph());
		t2d.setReversedEdges(true);
		t2d.setOutputFile(dotFile);
		t2d.setGraphMarker(m);
		System.out.println("Creating dot file...");
		t2d.convert();
		System.out.println("Creating dot file... DONE");

		// PDF
		Runtime.getRuntime()
				.exec("dot -Tpdf -o " + outputDir + File.separator
						+ "cginput.pdf " + dotFile).waitFor();

		// Make a diff
		Process proc = Runtime.getRuntime().exec(
				"diff -ru --ignore-all-space testit/cginput " + outputDir);
		proc.waitFor();
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputDir
				+ File.separator + "differences.diff"));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));
		String line = br.readLine();
		while (line != null) {
			bw.write(line + "\n");
			line = br.readLine();
		}
		br.close();
		bw.close();

		System.out.println("Finito.");
	}

	private static BooleanGraphMarker markClassDefinition(
			JavaCodeGenerator jcg, String name) {
		String query = "from cd  : V{ClassDefinition} "
				+ "     with cd.name = \""
				+ name
				+ "\" "
				+ "     reportSet cd                                           "
				+ "     end";
		@SuppressWarnings("unchecked")
		Set<ClassDefinition> result = (Set<ClassDefinition>) GreqlQuery
				.createQuery(query).evaluate(jcg.getJavaGraph());
		;

		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (ClassDefinition md : result) {
			marker.mark(md);
		}
		return marker;
	}

	private static void markNeededEdges(Java5 graph, BooleanGraphMarker m) {
		if (m == null) {
			return;
		}
		for (Edge e : graph.edges()) {
			if (m.isMarked(e.getAlpha()) && m.isMarked(e.getOmega())) {
				m.mark(e);
			}
		}
	}

	private static BooleanGraphMarker markVarLenMethMethod(JavaCodeGenerator jcg) {
		String query = "from md  : V{MethodDefinition}, "
				+ "          idx : V{Identifier} "
				+ "     with idx.name = \"varLenMeth\" and idx -->{IsNameOfMethod} md "
				+ "     reportSet md                                           "
				+ "     end";
		@SuppressWarnings("unchecked")
		Set<MethodDefinition> result = (Set<MethodDefinition>) GreqlQuery
				.createQuery(query).evaluate(jcg.getJavaGraph());
		;

		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (MethodDefinition md : result) {
			marker.mark(md);
		}
		return marker;
	}

	private BooleanGraphMarker markMBazMethod(JavaCodeGenerator jcg) {
		String query = "from md  : V{MethodDefinition}, "
				+ "          idx : V{Identifier} "
				+ "     with idx.name = \"mBaz\" and idx -->{IsNameOfMethod} md "
				+ "     reportSet md                                           "
				+ "     end";
		@SuppressWarnings("unchecked")
		Set<MethodDefinition> result = (Set<MethodDefinition>) GreqlQuery
				.createQuery(query).evaluate(jcg.getJavaGraph());

		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (MethodDefinition md : result) {
			marker.mark(md);
		}
		return marker;
	}

	private BooleanGraphMarker markAll(JavaCodeGenerator jcg, VertexClass clazz) {
		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (Vertex v : jcg.getJavaGraph().vertices(clazz)) {
			marker.mark(v);
		}
		return marker;
	}

	public static boolean deleteFile(File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				if (!deleteFile(child)) {
					System.out.println("Couldn't delete " + child + ".");
				}
			}
		}
		return f.delete();
	}
}
