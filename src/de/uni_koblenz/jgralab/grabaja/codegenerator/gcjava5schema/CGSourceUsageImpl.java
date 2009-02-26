package de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.SourceUsageImpl;

public class CGSourceUsageImpl extends SourceUsageImpl implements
		CGFoldGraphReference {

	public CGSourceUsageImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(BufferedWriter bw, int indentLevel)
			throws IOException {
		for (IsExternalDeclarationIn iedi : getIsExternalDeclarationInIncidences(EdgeDirection.IN)) {
			((CGExternalDeclaration) iedi.getAlpha()).generateCode(bw,
					indentLevel);
		}
	}

}
