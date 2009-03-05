import java.io.File;
import java.io.IOException;

import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5Schema;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;

public class CGTest {
	public static void main(String[] args) throws GraphIOException, IOException {

		JavaCodeGenerator.instance();

		String outputDir = "testit" + File.separator + "cgoutput";
		String graphFile = outputDir + File.separator + "cginput.tg";
		String dotFile = outputDir + File.separator + "cginput.dot";

		// Delete the files in the output dir
		Runtime.getRuntime().exec("rm -rf " + outputDir);
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
						+ dotFile);

		// Generate Code
		JavaCodeGenerator.instance().setGraph(g);
		JavaCodeGenerator.instance().setOutputDirectory(outputDir);
		JavaCodeGenerator.instance().generateCode();

	}
}
