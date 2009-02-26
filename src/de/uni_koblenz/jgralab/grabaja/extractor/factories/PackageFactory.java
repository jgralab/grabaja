package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsExternalDeclarationIn;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPackageNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsPackageOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.PackageDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.Program;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;

/**
 * Provides functionality for creating package elements in graph.
 * @author: ultbreit@uni-koblenz.de
 */
public class PackageFactory extends SubgraphFactory{

	/**
	 * Instantiates and initializes an instance.
	 * @param programGraph The graph to be used.
	 * @param symbolTable The symbol table to be used.
	 */
    public PackageFactory( Java5 pgh, SymbolTable symbolTable ){
        programGraph = pgh;
        this.symbolTable = symbolTable;
    }

	/**
	 * Creates a semantic vertex for a package.
	 * @param fullyQualifiedName The fully qualified name of the package.
	 * @return The created vertex.
	 */
	public JavaPackage createJavaPackage( String fullyQualifiedName ){
		JavaPackage javaPackageVertex = programGraph.createJavaPackage();
		javaPackageVertex.setFullyQualifiedName( fullyQualifiedName );
		return javaPackageVertex;
	}

	/**
	 * Creates a semantic edge between a package and it's translation unit.
	 * @param javaPackageVertex The package.
	 * @param translationUnitVertex The translation unit.
	 */
	public void attach( JavaPackage javaPackageVertex, TranslationUnit translationUnitVertex ){
        /*IsPartOf isPartOfEdge =*/ programGraph.createIsPartOf( translationUnitVertex, javaPackageVertex );
        IsPackageOf isPackageOfEdge = javaPackageVertex.getFirstIsPackageOf();
        if( isPackageOfEdge == null ){
			Program programVertex = programGraph.getFirstProgram();
			programGraph.createIsPackageOf( javaPackageVertex, programVertex );
		}
	}

	/**
	 * Creates a semantic edge between a package and it's parent package.
	 * @param javaPackageVertex The package.
	 * @param javaPackageParentVertex The parent package.
	 */
	public void attach( JavaPackage javaPackageVertex, JavaPackage javaPackageParentVertex ){
		programGraph.createIsSubPackageOf( javaPackageVertex, javaPackageParentVertex );
	}

	/**
	 * Creates an edge between a package definition and it's source usage.
	 * @param packageDefinitionVertex The package definition.
	 * @param sourceUsageVertex The source usage.
	 * @param beginAST The AST element representing the first element of the package definition.
	 * @param endAST The AST element representing the last element of the package definition.
	 */
    public void attach( PackageDefinition packageDefinitionVertex, SourceUsage sourceUsageVertex, AST beginAST, AST endAST ){
		IsExternalDeclarationIn isExternalDeclarationInEdge = programGraph.createIsExternalDeclarationIn( packageDefinitionVertex, sourceUsageVertex );
		Utilities.fillEdgeAttributesFromASTDifference( isExternalDeclarationInEdge, beginAST, endAST );
	}

	/**
	 * Creates an edge between a qualified name and a package definition.
	 * @param qualifiedNameVertex The qualified name.
	 * @param packageDefinitionVertex The package definition.
	 * @param beginAST The AST element representing the first element of the qualified name.
	 * @param endAST The AST element representing the last element of the qualified name.
	 */
	public void attach( QualifiedName qualifiedNameVertex, PackageDefinition packageDefinitionVertex, AST beginAST, AST endAST  ){
        IsPackageNameOf isPackageNameOfEdge = programGraph.createIsPackageNameOf( qualifiedNameVertex, packageDefinitionVertex );
        Utilities.fillEdgeAttributesFromASTDifference( isPackageNameOfEdge, beginAST, endAST );
	}

	/**
	 * Creates semantic edges between a package and the translation unit / source usage / program.
	 * @param javaPackageVertex The package.
	 * @param sourceUsageVertex The source usage.
	 * @param translationUnitVertex The translation unit.
	 */
    public void attach( JavaPackage javaPackageVertex, SourceUsage sourceUsageVertex, TranslationUnit translationUnitVertex ){
        /*IsPartOf isPartOfEdge = */programGraph.createIsPartOf( translationUnitVertex, javaPackageVertex );
        IsPackageOf isPackageOfEdge = javaPackageVertex.getFirstIsPackageOf();
        if( isPackageOfEdge == null ){
			Program programVertex = programGraph.getFirstProgram();
			programGraph.createIsPackageOf( javaPackageVertex, programVertex );
		}
    }

}