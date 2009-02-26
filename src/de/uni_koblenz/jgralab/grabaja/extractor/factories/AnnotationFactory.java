package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.Annotation;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationField;
import de.uni_koblenz.jgralab.grabaja.java5schema.ArrayInitializer;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.InfixExpression;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfEnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfMember;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfType;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationOfVariable;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsContentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsDefaultValueOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMetaAnnotationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Member;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableInitializer;

/**
 * Provides functionality to create graph representations of annotations.
 * @author: ultbreit@uni-koblenz.de
 */
public class AnnotationFactory extends SubgraphFactory{

	/**
	 * Creates a new <code>AnnotationFactory</code> from given values.
	 * @param programGraph The graph to be used.
	 */
    public AnnotationFactory( Java5 pg ){
        programGraph = pg;
    }

	/**
	 * Creates an edge between an annotation vertex and the annotated element's vertex.
	 * @param annotationVertex Vertex representing annotation.
	 * @param parentVertex Vertex representing annotated element.
	 * @param beginAST AST element representing first element of the annotation.
	 * @param endAST AST element representing last element of the annotation.
	 */
	public void attach( Annotation annotationVertex, Vertex parentVertex, AST beginAST, AST endAST ){
        if( parentVertex instanceof PackageDefinition ){
            IsAnnotationOfPackage isAnnotationOfPackageEdge = programGraph.createIsAnnotationOfPackage( annotationVertex, ( PackageDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isAnnotationOfPackageEdge, beginAST, endAST );
        }
        // before we check for types let's check for the subtype annotation definition
        else if( parentVertex instanceof AnnotationDefinition ){
            IsMetaAnnotationOf isMetaAnnotationOfEdge = programGraph.createIsMetaAnnotationOf( annotationVertex, ( AnnotationDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isMetaAnnotationOfEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof Type ){
            IsAnnotationOfType isAnnotationOfTypeEdge = programGraph.createIsAnnotationOfType( annotationVertex, ( Type )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isAnnotationOfTypeEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof EnumConstant ){
            IsAnnotationOfEnumConstant isAnnotationOfEnumConstantEdge = programGraph.createIsAnnotationOfEnumConstant( annotationVertex, ( EnumConstant )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isAnnotationOfEnumConstantEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof Member ){
            IsAnnotationOfMember isAnnotationOfMemberEdge = programGraph.createIsAnnotationOfMember( annotationVertex, ( Member )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isAnnotationOfMemberEdge, beginAST, endAST );
        }
        // @TODO could be an Infixexpression, too
        else if( parentVertex instanceof VariableDeclaration ){
            IsAnnotationOfVariable isAnnotationOfVariableEdge = programGraph.createIsAnnotationOfVariable( annotationVertex, ( VariableDeclaration )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isAnnotationOfVariableEdge, beginAST, endAST );
        }
    }

	/**
	 * Creates an edge between a qualified name and an annotation vertex.
	 * @param qualifiedNameVertex Vertex representing qualified name.
	 * @param annotationVertex Vertex representing annotation.
	 * @param beginAST AST element representing first element of the qualified name.
	 * @param endAST AST element representing last element of the qualified name.
	 */
    public void attachAnnotationName( QualifiedName qualifiedNameVertex, Annotation annotationVertex, AST beginAST, AST endAST ){
        IsAnnotationNameOf isAnnotationNameOfEdge = programGraph.createIsAnnotationNameOf( qualifiedNameVertex, annotationVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isAnnotationNameOfEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between an annotation argument and an annotation vertex.
	 * @param vertex Vertex representing annotation argument.
	 * @param annotationVertex Vertex representinge annotation.
	 * @param beginAST AST element representing first element of annotation argument.
	 * @param endAST AST element representing last element of annotation argument.
	 */
    public void attachAnnotationArgument( Vertex vertex, Annotation annotationVertex, AST beginAST, AST endAST ){
        if( vertex instanceof VariableInitializer ){
            IsAnnotationArgumentOf isAnnotationArgumentOfEdge = programGraph.createIsAnnotationArgumentOf( ( VariableInitializer )vertex, annotationVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isAnnotationArgumentOfEdge, beginAST, endAST );
        }
        else if( vertex instanceof InfixExpression ){
            IsAnnotationArgumentOf annotationMemberValuePair = programGraph.createIsAnnotationArgumentOf( ( InfixExpression )vertex, annotationVertex );
            Utilities.fillEdgeAttributesFromASTDifference( annotationMemberValuePair, beginAST, endAST );
        }
    }

	/**
	 * Creates an edge between an expression and an array initializer vertex.
	 * @param arrayInitializerVertex Vertex representing array initializer.
	 * @param expressionVertex Vertex representing expression.
	 * @param beginAST AST element representing first element of expression.
	 * @param endAST AST element representing last element of expression.
	 */
    public void attachContent( ArrayInitializer arrayInitializerVertex, Expression expressionVertex, AST beginAST, AST endAST ){
        IsContentOf isContentOfEdge = programGraph.createIsContentOf( expressionVertex, arrayInitializerVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isContentOfEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a variable initializer and an annotation field vertex.
	 * @param annotationFieldVertex Vertex representing annotation field.
	 * @param variableInitializerVertex Vertex representing variable initializer.
	 * @param beginAST AST element representing first element of variable initializer.
	 * @param endAST AST element representing last element of variable initializer.
	 */
    public void attachDefaultValue( AnnotationField annotationFieldVertex, VariableInitializer variableInitializerVertex, AST beginAST, AST endAST ){
        IsDefaultValueOf isDefaultValueOfEdge = programGraph.createIsDefaultValueOf( variableInitializerVertex, annotationFieldVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isDefaultValueOfEdge, beginAST, endAST );
    }
}