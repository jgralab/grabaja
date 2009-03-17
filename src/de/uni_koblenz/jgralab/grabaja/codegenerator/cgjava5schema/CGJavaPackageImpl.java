package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPartOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.JavaPackageImpl;

public class CGJavaPackageImpl extends JavaPackageImpl implements CodeGenerator {

	public CGJavaPackageImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// create the directory
		String relativeDirName = fullyQualifiedName.replaceAll("\\.",
				File.separator);
		File pkg = new File(jcg.getBaseDirectory().getCanonicalPath()
				+ File.separator + relativeDirName);
		pkg.mkdirs();

		// create code for the translation units (0,*)
		for (IsPartOf ipo : getIsPartOfIncidences(EdgeDirection.IN)) {
			CGTranslationUnitImpl tu = (CGTranslationUnitImpl) ipo.getAlpha();
			tu.setDirectory(pkg.getAbsolutePath());
			tu.generateCode(jcg, bw, indentLevel);
		}
	}

}
