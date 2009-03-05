import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5Schema;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;

public class CGTest {
	public static void main(String[] args) throws GraphIOException,
			IOException, InterruptedException {

		JavaCodeGenerator.instance();

		String outputDir = "testit" + File.separator + "cgoutput";
		String graphFile = outputDir + File.separator + "cginput.tg";
		String dotFile = outputDir + File.separator + "cginput.dot";

		// Delete the files in the output dir
		Runtime.getRuntime().exec("rm -rf " + outputDir).waitFor();
		new File(outputDir).mkdir();

		// Extract
		JavaExtractor.main(new String[] { "-o", graphFile,
				"testit" + File.separator + "cginput" });
		Java5 g = Java5Schema.instance().loadJava5(graphFile);

		// Dot & PDF
		Tg2Dot t2d = new Tg2Dot();
		t2d.setGraph(g);
		t2d.setReversedEdges(true);
		t2d.setOutputFile(dotFile);
		t2d.printGraph();
		Runtime.getRuntime().exec(
				"dot -Tpdf -o " + outputDir + File.separator + "cginput.pdf "
						+ dotFile).waitFor();

		// Generate Code
		JavaCodeGenerator.instance().setGraph(g);
		JavaCodeGenerator.instance().setOutputDirectory(outputDir);
		JavaCodeGenerator.instance().generateCode();

		// Make a diff
		Process proc = Runtime.getRuntime().exec(
				"diff -ru --ignore-all-space testit/cginput " + outputDir);
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
}
