import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueSet;
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
		t2d.printGraph();
		System.out.println("Creating dot file... DONE");

		// PDF
		Runtime.getRuntime().exec(
				"dot -Tpdf -o " + outputDir + File.separator + "cginput.pdf "
						+ dotFile).waitFor();

		// Make a diff
		Process proc = Runtime.getRuntime().exec(
				"diff -ru --ignore-all-space testit/cginput " + outputDir);
		proc.waitFor();
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputDir
				+ File.separator + "differences.diff"));
		BufferedReader br = new BufferedReader(new InputStreamReader(proc
				.getInputStream()));
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
		GreqlEvaluator eval = new GreqlEvaluator(query, jcg.getJavaGraph(),
				null);
		eval.startEvaluation();
		JValueSet result = (JValueSet) eval.getEvaluationResult();

		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (JValue md : result) {
			marker.mark(md.toVertex());
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
		GreqlEvaluator eval = new GreqlEvaluator(query, jcg.getJavaGraph(),
				null);
		eval.startEvaluation();
		JValueSet result = (JValueSet) eval.getEvaluationResult();

		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (JValue md : result) {
			marker.mark(md.toVertex());
		}
		return marker;
	}

	private BooleanGraphMarker markMBazMethod(JavaCodeGenerator jcg) {
		String query = "from md  : V{MethodDefinition}, "
				+ "          idx : V{Identifier} "
				+ "     with idx.name = \"mBaz\" and idx -->{IsNameOfMethod} md "
				+ "     reportSet md                                           "
				+ "     end";
		GreqlEvaluator eval = new GreqlEvaluator(query, jcg.getJavaGraph(),
				null);
		eval.startEvaluation();
		JValueSet result = (JValueSet) eval.getEvaluationResult();

		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (JValue md : result) {
			marker.mark(md.toVertex());
		}
		return marker;
	}

	private BooleanGraphMarker markAll(JavaCodeGenerator jcg,
			Class<? extends Vertex> clazz) {
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
