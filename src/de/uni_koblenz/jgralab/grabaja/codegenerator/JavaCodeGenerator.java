package de.uni_koblenz.jgralab.grabaja.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGAnnotationDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGAnnotationFieldImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGAnnotationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBlockImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBooleanConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBreakImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBuiltInCastImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGBuiltInTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGCaseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGClassDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGClassImportDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGConditionalExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGDefaultImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGDoubleConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGEnumConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGEnumDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGFieldAccessImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGFieldImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGFloatConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGForEachClauseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGForImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGIdentifierImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGIfImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGInfixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGIntegerConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGInterfaceDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGJavaPackageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGLabelImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGMethodDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGMethodDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGMethodInvocationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGModifierImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGNullImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGObjectCreationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGPackageDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGPackageImportDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGParameterDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGPostfixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGPrefixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGProgramImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGQualifiedNameImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGQualifiedTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGReturnImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGSimpleArgumentImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGSourceFileImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGSourceUsageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGStringConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGSwitchImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGThrowImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTraditionalForClauseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTranslationUnitImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTypeArgumentImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTypeParameterDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGTypeParameterUsageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGVariableDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGVariableInitializerImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.gcjava5schema.CGWildcardArgumentImpl;
import de.uni_koblenz.jgralab.grabaja.java5schema.Annotation;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.BooleanConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Break;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Case;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConditionalExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.Default;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoubleConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Field;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.FloatConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.For;
import de.uni_koblenz.jgralab.grabaja.java5schema.ForEachClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.If;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IntegerConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5Schema;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.Label;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.Null;
import de.uni_koblenz.jgralab.grabaja.java5schema.ObjectCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.PostfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.PrefixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.Program;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Return;
import de.uni_koblenz.jgralab.grabaja.java5schema.SimpleArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceFile;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.StringConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Switch;
import de.uni_koblenz.jgralab.grabaja.java5schema.Throw;
import de.uni_koblenz.jgralab.grabaja.java5schema.TraditionalForClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableInitializer;
import de.uni_koblenz.jgralab.grabaja.java5schema.While;
import de.uni_koblenz.jgralab.grabaja.java5schema.WildcardArgument;

public class JavaCodeGenerator {

	private Java5 javaGraph = null;
	private File baseDirectory = null;

	/**
	 * @return the baseDirectory
	 */
	public File getBaseDirectory() {
		return baseDirectory;
	}

	public static boolean isLoop(Vertex v) {
		return v instanceof While || v instanceof For || v instanceof DoWhile;
	}

	public static boolean isBlockConstruct(Vertex v) {
		return v instanceof Block || v instanceof If || v instanceof Switch
				| isLoop(v);
	}

	public static void indent(BufferedWriter bw, int level) throws IOException {
		for (int i = 0; i < level; i++) {
			bw.append("    ");
		}
	}

	static {
		GraphFactory f = Java5Schema.instance().getGraphFactory();
		f
				.setVertexImplementationClass(Annotation.class,
						CGAnnotationImpl.class);
		f.setVertexImplementationClass(AnnotationDefinition.class,
				CGAnnotationDefinitionImpl.class);
		f.setVertexImplementationClass(AnnotationField.class,
				CGAnnotationFieldImpl.class);
		f.setVertexImplementationClass(Block.class, CGBlockImpl.class);
		f.setVertexImplementationClass(BooleanConstant.class,
				CGBooleanConstantImpl.class);
		f.setVertexImplementationClass(Break.class, CGBreakImpl.class);
		f.setVertexImplementationClass(BuiltInCast.class,
				CGBuiltInCastImpl.class);
		f.setVertexImplementationClass(BuiltInType.class,
				CGBuiltInTypeImpl.class);
		f.setVertexImplementationClass(Case.class, CGCaseImpl.class);
		f.setVertexImplementationClass(ClassDefinition.class,
				CGClassDefinitionImpl.class);
		f.setVertexImplementationClass(ClassImportDefinition.class,
				CGClassImportDefinitionImpl.class);
		f.setVertexImplementationClass(ConditionalExpression.class,
				CGConditionalExpressionImpl.class);
		f.setVertexImplementationClass(Default.class, CGDefaultImpl.class);
		f.setVertexImplementationClass(DoubleConstant.class,
				CGDoubleConstantImpl.class);
		f.setVertexImplementationClass(EnumConstant.class,
				CGEnumConstantImpl.class);
		f.setVertexImplementationClass(EnumDefinition.class,
				CGEnumDefinitionImpl.class);
		f.setVertexImplementationClass(Field.class, CGFieldImpl.class);
		f.setVertexImplementationClass(FieldAccess.class,
				CGFieldAccessImpl.class);
		f.setVertexImplementationClass(FloatConstant.class,
				CGFloatConstantImpl.class);
		f.setVertexImplementationClass(For.class, CGForImpl.class);
		f.setVertexImplementationClass(ForEachClause.class,
				CGForEachClauseImpl.class);
		f
				.setVertexImplementationClass(Identifier.class,
						CGIdentifierImpl.class);
		f.setVertexImplementationClass(If.class, CGIfImpl.class);
		f.setVertexImplementationClass(InfixExpression.class,
				CGInfixExpressionImpl.class);
		f.setVertexImplementationClass(InterfaceDefinition.class,
				CGInterfaceDefinitionImpl.class);
		f.setVertexImplementationClass(IntegerConstant.class,
				CGIntegerConstantImpl.class);
		f.setVertexImplementationClass(JavaPackage.class,
				CGJavaPackageImpl.class);
		f.setVertexImplementationClass(Label.class, CGLabelImpl.class);
		f.setVertexImplementationClass(MethodDeclaration.class,
				CGMethodDeclarationImpl.class);
		f.setVertexImplementationClass(MethodDefinition.class,
				CGMethodDefinitionImpl.class);
		f.setVertexImplementationClass(MethodInvocation.class,
				CGMethodInvocationImpl.class);
		f.setVertexImplementationClass(Modifier.class, CGModifierImpl.class);
		f.setVertexImplementationClass(Null.class, CGNullImpl.class);
		f.setVertexImplementationClass(ObjectCreation.class,
				CGObjectCreationImpl.class);
		f.setVertexImplementationClass(PackageDefinition.class,
				CGPackageDefinitionImpl.class);
		f.setVertexImplementationClass(PackageImportDefinition.class,
				CGPackageImportDefinitionImpl.class);
		f.setVertexImplementationClass(ParameterDeclaration.class,
				CGParameterDeclarationImpl.class);
		f.setVertexImplementationClass(PostfixExpression.class,
				CGPostfixExpressionImpl.class);
		f.setVertexImplementationClass(PrefixExpression.class,
				CGPrefixExpressionImpl.class);
		f.setVertexImplementationClass(Program.class, CGProgramImpl.class);
		f.setVertexImplementationClass(QualifiedName.class,
				CGQualifiedNameImpl.class);
		f.setVertexImplementationClass(QualifiedType.class,
				CGQualifiedTypeImpl.class);
		f.setVertexImplementationClass(Return.class, CGReturnImpl.class);
		f.setVertexImplementationClass(SimpleArgument.class,
				CGSimpleArgumentImpl.class);
		f
				.setVertexImplementationClass(SourceFile.class,
						CGSourceFileImpl.class);
		f.setVertexImplementationClass(SourceUsage.class,
				CGSourceUsageImpl.class);
		f.setVertexImplementationClass(StringConstant.class,
				CGStringConstantImpl.class);
		f.setVertexImplementationClass(Switch.class, CGSwitchImpl.class);
		f.setVertexImplementationClass(Throw.class, CGThrowImpl.class);
		f.setVertexImplementationClass(TraditionalForClause.class,
				CGTraditionalForClauseImpl.class);
		f.setVertexImplementationClass(TranslationUnit.class,
				CGTranslationUnitImpl.class);
		f.setVertexImplementationClass(TypeArgument.class,
				CGTypeArgumentImpl.class);
		f.setVertexImplementationClass(TypeParameterDeclaration.class,
				CGTypeParameterDeclarationImpl.class);
		f.setVertexImplementationClass(TypeParameterUsage.class,
				CGTypeParameterUsageImpl.class);
		f.setVertexImplementationClass(VariableDeclaration.class,
				CGVariableDeclarationImpl.class);
		f.setVertexImplementationClass(VariableInitializer.class,
				CGVariableInitializerImpl.class);
		f.setVertexImplementationClass(WildcardArgument.class,
				CGWildcardArgumentImpl.class);
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
