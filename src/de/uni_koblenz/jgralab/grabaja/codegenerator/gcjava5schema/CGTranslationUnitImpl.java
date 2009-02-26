package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSourceUsageIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceFile;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.TranslationUnitImpl;

public class CGTranslationUnitImpl extends TranslationUnitImpl implements
		CodeGenerator {

	public CGTranslationUnitImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		// each TU has exactly one SourceFile
		SourceFile sf = (SourceFile) getFirstIsPrimarySourceFor(
				EdgeDirection.IN).getAlpha();
		String fileName = sf.getName();
		fileName = fileName.replaceAll(".*/", "");
		bw = new BufferedWriter(new FileWriter(JavaCodeGenerator.instance()
				.getBaseDirectory().getAbsolutePath()
				+ File.separator + fileName));
		for (IsSourceUsageIn isui : getIsSourceUsageInIncidences(EdgeDirection.IN)) {
			((CGSourceUsageImpl) isui.getAlpha()).generateCode(bw, indentLevel);
		}
		bw.flush();
		bw.close();
	}
}
