package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeDefinitionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.TypeParameterUsageImpl;

public class CGTypeParameterUsageImpl extends TypeParameterUsageImpl implements
		CGTypeSpecification {

	public CGTypeParameterUsageImpl(int id, Graph g) {
		super(id, g);
	}

	@Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		Vertex last = this;
		// the types (0,*), but i guess in this case it should always be (1,1)
		for (IsTypeDefinitionOf itdo : getIsTypeDefinitionOfIncidences(EdgeDirection.IN)) {
			last = ((CGType) itdo.getAlpha())
					.generateCode(jcg, bw, indentLevel);
		}

		return last;
	}

}
