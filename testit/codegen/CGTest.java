import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor;
import de.uni_koblenz.jgralab.grabaja.java5schema.Switch;
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
		JavaExtractor.main(new String[] { "-o", graphFile,
				"testit" + File.separator + "cginput" });
		JavaCodeGenerator jcg = new JavaCodeGenerator(graphFile, outputDir);

		// Dot
		Tg2Dot t2d = new Tg2Dot();
		t2d.setGraph(jcg.getJavaGraph());
		t2d.setReversedEdges(true);
		t2d.setOutputFile(dotFile);
		t2d.printGraph();

		// PDF
		Runtime.getRuntime().exec(
				"dot -Tpdf -o " + outputDir + File.separator + "cginput.pdf "
						+ dotFile).waitFor();

		// Generate Code

		// markMBazMethod(jcg);
		// markVarLenMethMethod(jcg);
		markAll(jcg, Switch.class);
		jcg.generateCode();

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

	private static void markVarLenMethMethod(JavaCodeGenerator jcg) {
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
		jcg.setCgElements(marker);
	}

	private static void markMBazMethod(JavaCodeGenerator jcg) {
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
		jcg.setCgElements(marker);
	}

	private static void markAll(JavaCodeGenerator jcg,
			Class<? extends Vertex> clazz) {
		BooleanGraphMarker marker = new BooleanGraphMarker(jcg.getJavaGraph());
		for (Vertex v : jcg.getJavaGraph().vertices(clazz)) {
			marker.mark(v);
		}
		jcg.setCgElements(marker);
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
