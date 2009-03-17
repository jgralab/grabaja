import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDefinition;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;

public class CGTest {

	public static void testCompleteGraphCodeGeneration()
			throws GraphIOException, IOException, InterruptedException {
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

	public static void testPartialGraphCodeGeneration()
			throws GraphIOException, IOException, InterruptedException {
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
		JavaExtractor.main(new String[] {
				"-o",
				graphFile,
				"testit" + File.separator + "cginput" + File.separator + "bar"
						+ File.separator + "baz" + File.separator
						+ "TestClass.java",
				"testit" + File.separator + "cginput" + File.separator + "bar"
						+ File.separator + "TestClass2.java" });
		JavaCodeGenerator jcg = new JavaCodeGenerator(graphFile, outputDir);
		BooleanGraphMarker codeElems = new BooleanGraphMarker(jcg
				.getJavaGraph());
		// Mark the method definition of varLenMeth()
		for (MethodDefinition md : jcg.getJavaGraph()
				.getMethodDefinitionVertices()) {
			if (((Identifier) md.getFirstIsNameOfMethod(EdgeDirection.IN)
					.getAlpha()).getName().equals("varLenMeth")) {
				codeElems.mark(md);
			}
		}
		jcg.setCgElements(codeElems);

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
		jcg.generateCode();

		System.out.println("Finito.");
	}

	public static void main(String[] args) throws GraphIOException,
			IOException, InterruptedException {
		testCompleteGraphCodeGeneration();
		// testPartialGraphCodeGeneration();
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
