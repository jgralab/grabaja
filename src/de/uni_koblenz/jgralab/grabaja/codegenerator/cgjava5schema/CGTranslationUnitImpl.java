package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

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

	private String cg_tu_directory;

	public CGTranslationUnitImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	void setDirectory(String dir) {
		cg_tu_directory = dir;
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		// each TU has exactly one SourceFile
		SourceFile sf = (SourceFile) getFirstIsPrimarySourceFor(
				EdgeDirection.IN).getAlpha();
		String fileName = cg_tu_directory + File.separator
				+ sf.getName().replaceAll(".*/", "");
		bw = new BufferedWriter(new FileWriter(fileName));
		for (IsSourceUsageIn isui : getIsSourceUsageInIncidences(EdgeDirection.IN)) {
			((CGSourceUsageImpl) isui.getAlpha()).generateCode(jcg, bw,
					indentLevel);
		}
		bw.flush();
		bw.close();
	}
}
