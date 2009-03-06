package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
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
			CGExternalDeclaration exDec = (CGExternalDeclaration) iedi
					.getAlpha();

			if (exDec instanceof Type) {
				bw.append('\n');
			}

			exDec.generateCode(bw, indentLevel);
		}
	}
}
