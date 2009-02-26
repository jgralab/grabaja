package de.uni_koblenz.jgralab.grabaja.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBlockImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBooleanConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBuiltInCastImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBuiltInTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGClassDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGConditionalExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGFieldAccessImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGForImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGIdentifierImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGIfImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGInfixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGIntegerConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGMethodDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGMethodDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGMethodInvocationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGModifierImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGParameterDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGPostfixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGProgramImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGQualifiedNameImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGQualifiedTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGReturnImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGSourceFileImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGSourceUsageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGStringConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTraditionalForClauseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTranslationUnitImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGVariableDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.BooleanConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConditionalExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.For;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.If;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IntegerConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5Schema;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.PostfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.Program;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Return;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceFile;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.StringConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.TraditionalForClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;

public class JavaCodeGenerator {

	private Java5 javaGraph = null;
	private File baseDirectory = null;

	/**
	 * @return the baseDirectory
	 */
	public File getBaseDirectory() {
		return baseDirectory;
	}

	public static void indent(BufferedWriter bw, int level) throws IOException {
		for (int i = 0; i < level; i++) {
			bw.append("    ");
		}
	}

	static {
		GraphFactory f = Java5Schema.instance().getGraphFactory();
		f.setVertexImplementationClass(Block.class, CGBlockImpl.class);
		f.setVertexImplementationClass(BooleanConstant.class,
				CGBooleanConstantImpl.class);
		f.setVertexImplementationClass(BuiltInCast.class,
				CGBuiltInCastImpl.class);
		f.setVertexImplementationClass(BuiltInType.class,
				CGBuiltInTypeImpl.class);
		f.setVertexImplementationClass(ClassDefinition.class,
				CGClassDefinitionImpl.class);
		f.setVertexImplementationClass(ConditionalExpression.class,
				CGConditionalExpressionImpl.class);
		f.setVertexImplementationClass(FieldAccess.class,
				CGFieldAccessImpl.class);
		f.setVertexImplementationClass(For.class, CGForImpl.class);
		f
				.setVertexImplementationClass(Identifier.class,
						CGIdentifierImpl.class);
		f.setVertexImplementationClass(If.class, CGIfImpl.class);
		f.setVertexImplementationClass(InfixExpression.class,
				CGInfixExpressionImpl.class);
		f.setVertexImplementationClass(IntegerConstant.class,
				CGIntegerConstantImpl.class);
		f.setVertexImplementationClass(MethodDeclaration.class,
				CGMethodDeclarationImpl.class);
		f.setVertexImplementationClass(MethodDefinition.class,
				CGMethodDefinitionImpl.class);
		f.setVertexImplementationClass(MethodInvocation.class,
				CGMethodInvocationImpl.class);
		f.setVertexImplementationClass(Modifier.class, CGModifierImpl.class);
		f.setVertexImplementationClass(ParameterDeclaration.class,
				CGParameterDeclarationImpl.class);
		f.setVertexImplementationClass(PostfixExpression.class,
				CGPostfixExpressionImpl.class);
		f.setVertexImplementationClass(Program.class, CGProgramImpl.class);
		f.setVertexImplementationClass(QualifiedName.class,
				CGQualifiedNameImpl.class);
		f.setVertexImplementationClass(QualifiedType.class,
				CGQualifiedTypeImpl.class);
		f.setVertexImplementationClass(Return.class, CGReturnImpl.class);
		f
				.setVertexImplementationClass(SourceFile.class,
						CGSourceFileImpl.class);
		f.setVertexImplementationClass(SourceUsage.class,
				CGSourceUsageImpl.class);
		f.setVertexImplementationClass(StringConstant.class,
				CGStringConstantImpl.class);
		f.setVertexImplementationClass(TraditionalForClause.class,
				CGTraditionalForClauseImpl.class);
		f.setVertexImplementationClass(TranslationUnit.class,
				CGTranslationUnitImpl.class);
		f.setVertexImplementationClass(VariableDeclaration.class,
				CGVariableDeclarationImpl.class);
	}

	public void setGraph(String graphFile) throws GraphIOException {
		javaGraph = Java5Schema.instance().loadJava5(graphFile);
	}

	public void setOutputDirectory(String sourcesDir) throws IOException {
		baseDirectory = new File(sourcesDir);
		if (!baseDirectory.isDirectory()) {
			throw new IOException(baseDirectory + " is no directory!");
		}
		if (!baseDirectory.exists()) {
			throw new IOException(baseDirectory + " does not exist!");
		}
		if (!(baseDirectory.canExecute() && baseDirectory.canRead() && baseDirectory
				.canWrite())) {
			throw new IOException(baseDirectory + " has wrong permissions!");
		}
	}

	private JavaCodeGenerator() {
	}

	private static JavaCodeGenerator thisInstance = null;

	public static JavaCodeGenerator instance() {
		if (thisInstance == null) {
			thisInstance = new JavaCodeGenerator();
		}
		return thisInstance;
	}

	public void generateCode() throws IOException {
		for (Program p : javaGraph.getProgramVertices()) {
			((CGProgramImpl) p).generateCode(null, 0);
		}
	}

	/**
	 * @param args
	 * @throws GraphIOException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, GraphIOException {
		if (args.length != 2) {
			System.err
					.println("Usage: java JavaCodeGenerator java-graph.tg /path/to/src/dir");
			return;
		}
		JavaCodeGenerator.instance().setGraph(args[0]);
		JavaCodeGenerator.instance().setOutputDirectory(args[1]);
		JavaCodeGenerator.instance().generateCode();
		System.out.println("Successfully generated code in " + args[1] + ".");
	}

}
