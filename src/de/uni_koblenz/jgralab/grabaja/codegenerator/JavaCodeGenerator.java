/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.grabaja.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
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
import de.uni_koblenz.jgralab.grabaja.java5schema.*;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

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
	public static GraphFactory initImpls(GraphFactory f) {
		f.setVertexImplementationClass(Annotation.VC, CGAnnotationImpl.class);
		f.setVertexImplementationClass(AnnotationDefinition.VC,
				CGAnnotationDefinitionImpl.class);
		f.setVertexImplementationClass(AnnotationField.VC,
				CGAnnotationFieldImpl.class);
		f.setVertexImplementationClass(ArrayCreation.VC,
				CGArrayCreationImpl.class);
		f.setVertexImplementationClass(ArrayInitializer.VC,
				CGArrayInitializerImpl.class);
		f.setVertexImplementationClass(ArrayType.VC, CGArrayTypeImpl.class);
		f.setVertexImplementationClass(Assert.VC, CGAssertImpl.class);
		f.setVertexImplementationClass(Block.VC, CGBlockImpl.class);
		f.setVertexImplementationClass(BooleanConstant.VC,
				CGBooleanConstantImpl.class);
		f.setVertexImplementationClass(Break.VC, CGBreakImpl.class);
		f.setVertexImplementationClass(BuiltInCast.VC, CGBuiltInCastImpl.class);
		f.setVertexImplementationClass(BuiltInType.VC, CGBuiltInTypeImpl.class);
		f.setVertexImplementationClass(Case.VC, CGCaseImpl.class);
		f.setVertexImplementationClass(Catch.VC, CGCatchImpl.class);
		f.setVertexImplementationClass(CharConstant.VC,
				CGCharConstantImpl.class);
		f.setVertexImplementationClass(ClassCast.VC, CGClassCastImpl.class);
		f.setVertexImplementationClass(ClassDefinition.VC,
				CGClassDefinitionImpl.class);
		f.setVertexImplementationClass(ClassImportDefinition.VC,
				CGClassImportDefinitionImpl.class);
		f.setVertexImplementationClass(ConditionalExpression.VC,
				CGConditionalExpressionImpl.class);
		f.setVertexImplementationClass(ConstructorDefinition.VC,
				CGConstructorDefinitionImpl.class);
		f.setVertexImplementationClass(Continue.VC, CGContinueImpl.class);
		f.setVertexImplementationClass(Default.VC, CGDefaultImpl.class);
		f.setVertexImplementationClass(DoubleConstant.VC,
				CGDoubleConstantImpl.class);
		f.setVertexImplementationClass(DoWhile.VC, CGDoWhileImpl.class);
		f.setVertexImplementationClass(EmptyStatement.VC,
				CGEmptyStatementImpl.class);
		f.setVertexImplementationClass(EnumConstant.VC,
				CGEnumConstantImpl.class);
		f.setVertexImplementationClass(EnumDefinition.VC,
				CGEnumDefinitionImpl.class);
		f.setVertexImplementationClass(Field.VC, CGFieldImpl.class);
		f.setVertexImplementationClass(FieldAccess.VC, CGFieldAccessImpl.class);
		f.setVertexImplementationClass(FloatConstant.VC,
				CGFloatConstantImpl.class);
		f.setVertexImplementationClass(For.VC, CGForImpl.class);
		f.setVertexImplementationClass(ForEachClause.VC,
				CGForEachClauseImpl.class);
		f.setVertexImplementationClass(Identifier.VC, CGIdentifierImpl.class);
		f.setVertexImplementationClass(If.VC, CGIfImpl.class);
		f.setVertexImplementationClass(InfixExpression.VC,
				CGInfixExpressionImpl.class);
		f.setVertexImplementationClass(InterfaceDefinition.VC,
				CGInterfaceDefinitionImpl.class);
		f.setVertexImplementationClass(IntegerConstant.VC,
				CGIntegerConstantImpl.class);
		f.setVertexImplementationClass(JavaPackage.VC, CGJavaPackageImpl.class);
		f.setVertexImplementationClass(Label.VC, CGLabelImpl.class);
		f.setVertexImplementationClass(LongConstant.VC,
				CGLongConstantImpl.class);
		f.setVertexImplementationClass(MethodDeclaration.VC,
				CGMethodDeclarationImpl.class);
		f.setVertexImplementationClass(MethodDefinition.VC,
				CGMethodDefinitionImpl.class);
		f.setVertexImplementationClass(MethodInvocation.VC,
				CGMethodInvocationImpl.class);
		f.setVertexImplementationClass(Modifier.VC, CGModifierImpl.class);
		f.setVertexImplementationClass(Null.VC, CGNullImpl.class);
		f.setVertexImplementationClass(ObjectCreation.VC,
				CGObjectCreationImpl.class);
		f.setVertexImplementationClass(PackageDefinition.VC,
				CGPackageDefinitionImpl.class);
		f.setVertexImplementationClass(PackageImportDefinition.VC,
				CGPackageImportDefinitionImpl.class);
		f.setVertexImplementationClass(ParameterDeclaration.VC,
				CGParameterDeclarationImpl.class);
		f.setVertexImplementationClass(PostfixExpression.VC,
				CGPostfixExpressionImpl.class);
		f.setVertexImplementationClass(PrefixExpression.VC,
				CGPrefixExpressionImpl.class);
		f.setVertexImplementationClass(Program.VC, CGProgramImpl.class);
		f.setVertexImplementationClass(QualifiedName.VC,
				CGQualifiedNameImpl.class);
		f.setVertexImplementationClass(QualifiedType.VC,
				CGQualifiedTypeImpl.class);
		f.setVertexImplementationClass(Return.VC, CGReturnImpl.class);
		f.setVertexImplementationClass(SimpleArgument.VC,
				CGSimpleArgumentImpl.class);
		f.setVertexImplementationClass(SourceFile.VC, CGSourceFileImpl.class);
		f.setVertexImplementationClass(SourceUsage.VC, CGSourceUsageImpl.class);
		f.setVertexImplementationClass(StaticConstructorDefinition.VC,
				CGStaticConstructorDefinitionImpl.class);
		f.setVertexImplementationClass(StaticInitializerDefinition.VC,
				CGStaticInitializerDefinitionImpl.class);
		f.setVertexImplementationClass(StringConstant.VC,
				CGStringConstantImpl.class);
		f.setVertexImplementationClass(Switch.VC, CGSwitchImpl.class);
		f.setVertexImplementationClass(Synchronized.VC,
				CGSynchronizedImpl.class);
		f.setVertexImplementationClass(Throw.VC, CGThrowImpl.class);
		f.setVertexImplementationClass(TraditionalForClause.VC,
				CGTraditionalForClauseImpl.class);
		f.setVertexImplementationClass(TranslationUnit.VC,
				CGTranslationUnitImpl.class);
		f.setVertexImplementationClass(Try.VC, CGTryImpl.class);
		f.setVertexImplementationClass(TypeArgument.VC,
				CGTypeArgumentImpl.class);
		f.setVertexImplementationClass(TypeParameterDeclaration.VC,
				CGTypeParameterDeclarationImpl.class);
		f.setVertexImplementationClass(TypeParameterUsage.VC,
				CGTypeParameterUsageImpl.class);
		f.setVertexImplementationClass(VariableDeclaration.VC,
				CGVariableDeclarationImpl.class);
		f.setVertexImplementationClass(VariableInitializer.VC,
				CGVariableInitializerImpl.class);
		f.setVertexImplementationClass(VariableLengthDeclaration.VC,
				CGVariableLengthDeclarationImpl.class);
		f.setVertexImplementationClass(While.VC, CGWhileImpl.class);
		f.setVertexImplementationClass(WildcardArgument.VC,
				CGWildcardArgumentImpl.class);
		return f;
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
		this(Java5Schema.instance().loadJava5(
				graphFile,
				initImpls(Java5Schema.instance().createDefaultGraphFactory(
						ImplementationType.STANDARD))), outputDir);
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
		if (cgElements == null) {
			return;
		}

		Vertex v = null;
		ArrayList<AttributedElement> markedElems = new ArrayList<AttributedElement>();

		// Walk over all marked elements and add them to the list (afuhr)
		for (AttributedElement element : cgElements.getMarkedElements()) {
			markedElems.add(element);
		}

		for (AttributedElement ae : markedElems) {
			if (!(ae instanceof Vertex)) {
				continue;
			}
			v = (Vertex) ae;
			markTop(v);
			markAllBelow(v);
		}

		fixMarks();
	}

	/**
	 * Fix the marks. For example, if a ClassDefinition is marked, then it's
	 * naming Identifier has to be marked, too.
	 */
	private void fixMarks() {
		ArrayList<AttributedElement> markedElems = new ArrayList<AttributedElement>();

		// Walk over all marked elements and add them to the list (afuhr)
		for (AttributedElement element : cgElements.getMarkedElements()) {
			markedElems.add(element);
		}

		for (AttributedElement ae : markedElems) {
			// Fix all Types (TypeParameterDeclarations don't need to be fixed
			// explicitly, because the class/interface/enum/annotation defs mark
			// them automatically)
			if (ae instanceof AnnotationDefinition) {
				fixAnnotationDefinition((AnnotationDefinition) ae);
			} else if (ae instanceof ClassDefinition) {
				fixClassDefinition((ClassDefinition) ae);
			} else if (ae instanceof InterfaceDefinition) {
				fixInterfaceDefinition((InterfaceDefinition) ae);
			} else if (ae instanceof EnumDefinition) {
				fixEnumDefinition((EnumDefinition) ae);
			}

			// Fix all marked SourceUsages (PackageDefinitions and
			// ImportDefinitions have to be included)
			else if (ae instanceof SourceUsage) {
				fixSourceUsage((SourceUsage) ae);
			}

			// Fix MethodDeclarations (complete signature)
			else if (ae instanceof MethodDeclaration) {
				fixMethodDeclaration((MethodDeclaration) ae);
			}

			// Fix Labels (need to mark the naming Identifier)
			else if (ae instanceof Label) {
				markBelowEdge((Label) ae, IsLabelNameOf.EC);
			}
		}
	}

	private void fixMethodDeclaration(MethodDeclaration md) {
		markBelowEdge(md, IsAnnotationOfMember.EC);
		markBelowEdge(md, IsModifierOfMethod.EC);
		markBelowEdge(md, IsReturnTypeOf.EC);
		markBelowEdge(md, IsTypeParameterOfMethod.EC);
		markBelowEdge(md, IsNameOfMethod.EC);
		markBelowEdge(md, IsParameterOfMethod.EC);
		markBelowEdge(md, IsExceptionThrownByMethod.EC);
	}

	private void fixSourceUsage(SourceUsage tu) {
		markBelowVertex(tu, PackageDefinition.VC);
		markBelowVertex(tu, ImportDefinition.VC);
	}

	private void fixEnumDefinition(EnumDefinition ed) {
		markBelowEdge(ed, IsAnnotationOfType.EC);
		markBelowEdge(ed, IsModifierOfEnum.EC);
		markBelowEdge(ed, IsEnumNameOf.EC);
	}

	private void fixInterfaceDefinition(InterfaceDefinition id) {
		markBelowEdge(id, IsAnnotationOfType.EC);
		markBelowEdge(id, IsModifierOfInterface.EC);
		markBelowEdge(id, IsInterfaceNameOf.EC);
		markBelowEdge(id, IsTypeParameterOfInterface.EC);
		markBelowEdge(id, IsSuperClassOfInterface.EC);
	}

	private void fixClassDefinition(ClassDefinition cd) {
		markBelowEdge(cd, IsAnnotationOfType.EC);
		markBelowEdge(cd, IsModifierOfClass.EC);
		markBelowEdge(cd, IsClassNameOf.EC);
		markBelowEdge(cd, IsTypeParameterOfClass.EC);
		markBelowEdge(cd, IsSuperClassOfClass.EC);
		markBelowEdge(cd, IsInterfaceOfClass.EC);
	}

	private void fixAnnotationDefinition(AnnotationDefinition ad) {
		markBelowEdge(ad, IsMetaAnnotationOf.EC);
		markBelowEdge(ad, IsModifierOfAnnotation.EC);
		markBelowEdge(ad, IsAnnotationDefinitionNameOf.EC);
	}

	private void markBelowEdge(Vertex v, EdgeClass clazz) {
		for (Edge e : v.incidences(clazz, EdgeDirection.IN)) {
			markAllBelow(e.getAlpha());
		}
	}

	private void markBelowVertex(Vertex v, VertexClass clazz) {
		for (Edge e : v.incidences(EdgeDirection.IN)) {
			Vertex child = e.getAlpha();
			if (child.isInstanceOf(clazz)) {
				markAllBelow(child);
			}
		}
	}

	private boolean isNonTreeEdge(Edge e) {
		return (e instanceof IsBreakTargetOf)
				|| (e instanceof IsContinueTargetOf)
				|| (e instanceof IsTypeDefinitionOf)
				|| (e instanceof IsDeclarationOfAccessedField);
	}

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
			// System.out.println("Marking omega of " + e);
			markTop(e.getOmega());
		}
	}

	private void markAllBelow(Vertex v) {
		cgElements.mark(v);
		for (Edge e : v.incidences(EdgeDirection.IN)) {
			if (isNonTreeEdge(e)) {
				continue;
			}
			markAllBelow(e.getAlpha());
		}
	}

	public boolean generationWanted(AttributedElement v) {
		return (cgElements == null) || cgElements.isMarked(v);
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
