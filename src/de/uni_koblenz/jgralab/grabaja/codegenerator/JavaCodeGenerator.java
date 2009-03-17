package de.uni_koblenz.jgralab.grabaja.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGAnnotationDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGAnnotationFieldImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGAnnotationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGArrayCreationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGArrayInitializerImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGArrayTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGAssertImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGBlockImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGBooleanConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGBreakImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGBuiltInCastImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGBuiltInTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGCaseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGCatchImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGCharConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGClassCastImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGClassDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGClassImportDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGConditionalExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGConstructorDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGContinueImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGDefaultImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGDoWhileImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGDoubleConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGEmptyStatementImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGEnumConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGEnumDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGFieldAccessImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGFieldImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGFloatConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGForEachClauseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGForImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGIdentifierImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGIfImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGInfixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGIntegerConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGInterfaceDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGJavaPackageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGLabelImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGLongConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGMethodDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGMethodDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGMethodInvocationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGModifierImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGNullImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGObjectCreationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGPackageDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGPackageImportDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGParameterDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGPostfixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGPrefixExpressionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGProgramImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGQualifiedNameImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGQualifiedTypeImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGReturnImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGSimpleArgumentImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGSourceFileImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGSourceUsageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGStaticConstructorDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGStaticInitializerDefinitionImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGStringConstantImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGSwitchImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGSynchronizedImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGThrowImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGTraditionalForClauseImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGTranslationUnitImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGTryImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGTypeArgumentImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGTypeParameterDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGTypeParameterUsageImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGVariableDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGVariableInitializerImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGVariableLengthDeclarationImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGWhileImpl;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGWildcardArgumentImpl;
import de.uni_koblenz.jgralab.grabaja.java5schema.Annotation;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayCreation;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayInitializer;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Assert;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.BooleanConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Break;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.Case;
import de.uni_koblenz.jgralab.grabaja.java5schema.Catch;
import de.uni_koblenz.jgralab.grabaja.java5schema.CharConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassCast;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassImportDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConditionalExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Continue;
import de.uni_koblenz.jgralab.grabaja.java5schema.Default;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoubleConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EmptyStatement;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
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
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBreakTargetOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsContinueTargetOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5Schema;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.Label;
import de.uni_koblenz.jgralab.grabaja.java5schema.LongConstant;
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
import de.uni_koblenz.jgralab.grabaja.java5schema.StaticConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.StaticInitializerDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.StringConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Switch;
import de.uni_koblenz.jgralab.grabaja.java5schema.Synchronized;
import de.uni_koblenz.jgralab.grabaja.java5schema.Throw;
import de.uni_koblenz.jgralab.grabaja.java5schema.TraditionalForClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;
import de.uni_koblenz.jgralab.grabaja.java5schema.Try;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeArgument;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableInitializer;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableLengthDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.While;
import de.uni_koblenz.jgralab.grabaja.java5schema.WildcardArgument;

public class JavaCodeGenerator {

	private BooleanGraphMarker cgElements = null;

	/**
	 * @return the cgElements
	 */
	public BooleanGraphMarker getCgElements() {
		return cgElements;
	}

	/**
	 * @param cgElements
	 *            the cgElements to set
	 */
	public void setCgElements(BooleanGraphMarker cgElements) {
		this.cgElements = cgElements;
	}

	private Java5 javaGraph = null;

	/**
	 * @return the javaGraph
	 */
	public Java5 getJavaGraph() {
		return javaGraph;
	}

	private File baseDirectory = null;

	/**
	 * @return the baseDirectory
	 */
	public File getBaseDirectory() {
		return baseDirectory;
	}

	public static boolean isNestedExpression(Vertex v) {
		for (Edge e : v.incidences(EdgeDirection.OUT)) {
			if (e.getOmega() instanceof Expression) {
				return true;
			}
		}
		return false;
	}

	public static void indent(BufferedWriter bw, int level) throws IOException {
		for (int i = 0; i < level; i++) {
			bw.append("    ");
		}
	}

	// TODO: Those are missing:
	// "SingleLineCommentImpl" "MultiLineCommentImpl"
	// "JavaDocCommentImpl"
	// "CommentImpl"
	static {
		GraphFactory f = Java5Schema.instance().getGraphFactory();
		f
				.setVertexImplementationClass(Annotation.class,
						CGAnnotationImpl.class);
		f.setVertexImplementationClass(AnnotationDefinition.class,
				CGAnnotationDefinitionImpl.class);
		f.setVertexImplementationClass(AnnotationField.class,
				CGAnnotationFieldImpl.class);
		f.setVertexImplementationClass(ArrayCreation.class,
				CGArrayCreationImpl.class);
		f.setVertexImplementationClass(ArrayInitializer.class,
				CGArrayInitializerImpl.class);
		f.setVertexImplementationClass(ArrayType.class, CGArrayTypeImpl.class);
		f.setVertexImplementationClass(Assert.class, CGAssertImpl.class);
		f.setVertexImplementationClass(Block.class, CGBlockImpl.class);
		f.setVertexImplementationClass(BooleanConstant.class,
				CGBooleanConstantImpl.class);
		f.setVertexImplementationClass(Break.class, CGBreakImpl.class);
		f.setVertexImplementationClass(BuiltInCast.class,
				CGBuiltInCastImpl.class);
		f.setVertexImplementationClass(BuiltInType.class,
				CGBuiltInTypeImpl.class);
		f.setVertexImplementationClass(Case.class, CGCaseImpl.class);
		f.setVertexImplementationClass(Catch.class, CGCatchImpl.class);
		f.setVertexImplementationClass(CharConstant.class,
				CGCharConstantImpl.class);
		f.setVertexImplementationClass(ClassCast.class, CGClassCastImpl.class);
		f.setVertexImplementationClass(ClassDefinition.class,
				CGClassDefinitionImpl.class);
		f.setVertexImplementationClass(ClassImportDefinition.class,
				CGClassImportDefinitionImpl.class);
		f.setVertexImplementationClass(ConditionalExpression.class,
				CGConditionalExpressionImpl.class);
		f.setVertexImplementationClass(ConstructorDefinition.class,
				CGConstructorDefinitionImpl.class);
		f.setVertexImplementationClass(Continue.class, CGContinueImpl.class);
		f.setVertexImplementationClass(Default.class, CGDefaultImpl.class);
		f.setVertexImplementationClass(DoubleConstant.class,
				CGDoubleConstantImpl.class);
		f.setVertexImplementationClass(DoWhile.class, CGDoWhileImpl.class);
		f.setVertexImplementationClass(EmptyStatement.class,
				CGEmptyStatementImpl.class);
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
		f.setVertexImplementationClass(LongConstant.class,
				CGLongConstantImpl.class);
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
		f.setVertexImplementationClass(StaticConstructorDefinition.class,
				CGStaticConstructorDefinitionImpl.class);
		f.setVertexImplementationClass(StaticInitializerDefinition.class,
				CGStaticInitializerDefinitionImpl.class);
		f.setVertexImplementationClass(StringConstant.class,
				CGStringConstantImpl.class);
		f.setVertexImplementationClass(Switch.class, CGSwitchImpl.class);
		f.setVertexImplementationClass(Synchronized.class,
				CGSynchronizedImpl.class);
		f.setVertexImplementationClass(Throw.class, CGThrowImpl.class);
		f.setVertexImplementationClass(TraditionalForClause.class,
				CGTraditionalForClauseImpl.class);
		f.setVertexImplementationClass(TranslationUnit.class,
				CGTranslationUnitImpl.class);
		f.setVertexImplementationClass(Try.class, CGTryImpl.class);
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
		f.setVertexImplementationClass(VariableLengthDeclaration.class,
				CGVariableLengthDeclarationImpl.class);
		f.setVertexImplementationClass(While.class, CGWhileImpl.class);
		f.setVertexImplementationClass(WildcardArgument.class,
				CGWildcardArgumentImpl.class);
	}

	private void setOutputDirectory(String sourcesDir) throws IOException {
		baseDirectory = new File(sourcesDir);
		if (!baseDirectory.exists()) {
			baseDirectory.mkdir();
		}
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

	public JavaCodeGenerator(Java5 graph, String outputDir) throws IOException {
		javaGraph = graph;
		setOutputDirectory(outputDir);
	}

	public JavaCodeGenerator(String graphFile, String outputDir)
			throws IOException, GraphIOException {
		this(Java5Schema.instance().loadJava5(graphFile), outputDir);
	}

	public JavaCodeGenerator(Java5 graph, String outputDir,
			BooleanGraphMarker elementsToBeGenerated) throws IOException {
		this(graph, outputDir);
		cgElements = elementsToBeGenerated;
	}

	public JavaCodeGenerator(String graphFile, String outputDir,
			BooleanGraphMarker elementsToBeGenerated) throws IOException,
			GraphIOException {
		this(graphFile, outputDir);
		cgElements = elementsToBeGenerated;
	}

	private void markDependencies() {
		// TODO: Mark dependent vertices
		Vertex v = null;
		for (AttributedElement ae : cgElements.getMarkedElements()) {
			if (!(ae instanceof Vertex)) {
				continue;
			}

			v = (Vertex) ae;
			markTop(v);
			markBottom(v);
		}
	}

	private boolean isNonTreeEdge(Edge e) {
		return e instanceof IsBreakTargetOf || e instanceof IsContinueTargetOf;
	}

	private HashSet<Vertex> x = new HashSet<Vertex>();

	private void markTop(Vertex v) {
		cgElements.mark(v);

		if (v instanceof QualifiedName) {
			// QNs are linked to multiple other vertices and introduce cycles...
			return;
		}

		for (Edge e : v.incidences(EdgeDirection.OUT)) {
			if (isNonTreeEdge(e)) {
				continue;
			}
			markTop(e.getOmega());
		}
		x.remove(v);
	}

	private void markBottom(Vertex v) {
		cgElements.mark(v);
		for (Edge e : v.incidences(EdgeDirection.IN)) {
			if (isNonTreeEdge(e)) {
				continue;
			}
			markBottom(e.getAlpha());
		}
	}

	public boolean generationWanted(AttributedElement v) {
		return cgElements == null || cgElements.isMarked(v);
	}

	public void generateCode() throws IOException {
		markDependencies();
		for (Program p : javaGraph.getProgramVertices()) {
			((CGProgramImpl) p).generateCode(this, null, 0);
		}
	}

	public static void main(String[] args) throws IOException, GraphIOException {
		if (args.length != 2) {
			System.err
					.println("Usage: java JavaCodeGenerator java-graph.tg /path/to/output/dir");
			return;
		}
		JavaCodeGenerator jcg = new JavaCodeGenerator(args[0], args[1]);
		jcg.generateCode();
		System.out.println("Successfully generated code in " + args[1] + ".");
	}

}
