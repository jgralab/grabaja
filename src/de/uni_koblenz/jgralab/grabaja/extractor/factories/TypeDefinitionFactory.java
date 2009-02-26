package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAnnotationBlockOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsClassBlockOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsEnumBlockOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceBlockOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsInterfaceOfEnum;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfClass;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSuperClassOfInterface;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsTypeIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.Statement;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;

/**
 * Provides functionality for creating type definition elements in graph.
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class TypeDefinitionFactory extends SubgraphFactory{

	/**
	 * Instantiates and initializes an instance.
	 * @param programGraph The graph to be used.
	 */
    public TypeDefinitionFactory( Java5 pg ){
      programGraph = pg;
    }

	/**
	 * Creates an edge between a super type specification and it's inherited type (class or interface).
	 * @param parentVertex The type.
	 * @param qualifiedNameVertex The qualified type (specification).
	 * @param beginAST The AST element representing the first element of the qualified type.
	 * @param endAST The AST element representing the last element of the qualified type.
	 */
    public void attachSuperClass( Vertex parentVertex, QualifiedName qualifiedNameVertex, AST beginAST, AST endAST ){
        if( parentVertex instanceof ClassDefinition){
            IsSuperClassOfClass isSuperClassOfClassEdge = programGraph.createIsSuperClassOfClass( qualifiedNameVertex, ( ClassDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isSuperClassOfClassEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof InterfaceDefinition ){
            IsSuperClassOfInterface isSuperClassOfInterfaceEdge = programGraph.createIsSuperClassOfInterface( qualifiedNameVertex, ( InterfaceDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isSuperClassOfInterfaceEdge, beginAST, endAST );
        }

    }

	/**
	 * Creates an edge between an interface type specification and it's implementing type (class or enum).
	 * @param parentVertex The type.
	 * @param qualifiedNameVertex The qualified type (specification).
	 * @param beginAST The AST element representing the first element of the qualified type.
	 * @param endAST The AST element representing the last element of the qualified type.
	 */
    public void attachInterface( Vertex parentVertex, QualifiedName qualifiedNameVertex, AST beginAST, AST endAST ){
        if( parentVertex instanceof ClassDefinition){
            IsInterfaceOfClass isInterfaceOfClassEdge = programGraph.createIsInterfaceOfClass( qualifiedNameVertex, ( ClassDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isInterfaceOfClassEdge, beginAST, endAST );
        }
        else if( parentVertex instanceof EnumDefinition ){
            IsInterfaceOfEnum isInterfaceOfEnumEdge = programGraph.createIsInterfaceOfEnum( qualifiedNameVertex, ( EnumDefinition )parentVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isInterfaceOfEnumEdge, beginAST, endAST );
        }
    }

	/**
	 * Creates an edge between a body of a class and it's class.
	 * @param blockVertex The body.
	 * @param classDefinitionVertex The class.
	 * @param beginAST The AST element representing the first element of the body.
	 * @param endAST The AST element representing the last element of the body.
	 */
    public void attachClassBlock( Block blockVertex, ClassDefinition classDefinitionVertex, AST beginAST, AST endAST ){
        IsClassBlockOf isClassBlockOfEdge = programGraph.createIsClassBlockOf( blockVertex, classDefinitionVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isClassBlockOfEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a body of an interface and it's interface.
	 * @param blockVertex The body.
	 * @param interfaceDefinitionVertex The interface.
	 * @param beginAST The AST element representing the first element of the body.
	 * @param endAST The AST element representing the last element of the body.
	 */
    public void attachInterfaceBlock( Block blockVertex, InterfaceDefinition interfaceDefinitionVertex, AST beginAST, AST endAST ){
        IsInterfaceBlockOf isInterfaceBlockOfEdge = programGraph.createIsInterfaceBlockOf( blockVertex, interfaceDefinitionVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isInterfaceBlockOfEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a body of an enum and it's enum.
	 * @param blockVertex The body.
	 * @param enumDefinitionVertex The enum.
	 * @param beginAST The AST element representing the first element of the body.
	 * @param endAST The AST element representing the last element of the body.
	 */
    public void attachEnumBlock( Block blockVertex, EnumDefinition enumDefinitionVertex, AST beginAST, AST endAST ){
        IsEnumBlockOf isEnumBlockOfEdge = programGraph.createIsEnumBlockOf( blockVertex, enumDefinitionVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isEnumBlockOfEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a body of an annotation and it's class.
	 * @param blockVertex The body.
	 * @param annotationDefinitionVertex The annotation.
	 * @param beginAST The AST element representing the first element of the body.
	 * @param endAST The AST element representing the last element of the body.
	 */
    public void attachAnnotationBlock( Block blockVertex, AnnotationDefinition annotationDefinitionVertex, AST beginAST, AST endAST ){
        IsAnnotationBlockOf isAnnotationBlockOfEdge = programGraph.createIsAnnotationBlockOf( blockVertex, annotationDefinitionVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isAnnotationBlockOfEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a type definition and its source usage.
	 * @param typeVertex The type definition.
	 * @param sourceUsageVertex The source usage.
	 * @param beginAST The AST element representing the first element of the type definition.
	 * @param endAST The AST element representing the last element of the type definition.
	 */
    public void attachType( Type typeVertex, SourceUsage sourceUsageVertex, AST beginAST, AST endAST ){
        IsExternalDeclarationIn isExternalDeclarationInEdge = programGraph.createIsExternalDeclarationIn( typeVertex, sourceUsageVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isExternalDeclarationInEdge, beginAST, endAST );
    }

	/**
	 * Creates an edge between a type definition and the statement (should be a Block, Case or Default) it is defined in (for nested classes).
	 * @param typeVertex The type definition.
	 * @param statementVertex The statement.
	 * @param beginAST The AST element representing the first element of the type definition.
	 * @param endAST The AST element representing the last element of the type definition.
	 */
    public void attachType( Type typeVertex, Statement statementVertex, AST beginAST, AST endAST ){
        IsTypeIn isTypeInEdge = programGraph.createIsTypeIn( typeVertex, statementVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isTypeInEdge, beginAST, endAST );
    }

}