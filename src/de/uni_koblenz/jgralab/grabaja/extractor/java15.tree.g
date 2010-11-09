header{
package de.uni_koblenz.jgralab.grabaja.extractor.parser;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import de.uni_koblenz.jgralab.grabaja.java5schema.*;
import de.uni_koblenz.jgralab.grabaja.extractor.resolvers.ResolverUtilities;
import de.uni_koblenz.jgralab.grabaja.extractor.factories.*;
import de.uni_koblenz.jgralab.grabaja.extractor.*;

import de.uni_koblenz.jgralab.*;
import antlr.*;
}

/** Java 1.51 AST Recognizer Grammar
 * Author: (see java.g preamble)
 * This grammar is in the PUBLIC DOMAIN
 */
class JavaTreeParser extends TreeParser;

options{ importVocab = Java; }

{
    /**
     * Holds the graph to build.
     */
    private Java5 programGraph = null;

    /**
     * Holds the top vertex of the program graph.
     */
    private Program programVertex = null;

    /**
     * Holds the vertex to append a subgraph converted from AST to.
     */
    private SourceUsage sourceUsageVertex = null;

    /**
     * Holds the vertex to append the ASG to.
     */
    private TranslationUnit translationUnitVertex = null;

    /**
     * Holds the global symbol table for the graph to build.
     */
    private SymbolTable symbolTable;

    public SymbolTable getSymbolTable(){
		return symbolTable;
	}

	public JavaTreeParser( SymbolTable symbolTable ){
		this();
		this.symbolTable = symbolTable;
	}

    private AnnotationFactory annotationFactory;
    private ConstantFactory constantFactory;
    private ExpressionFactory expressionFactory;
    private IdentifierFactory identifierFactory;
    private ImportFactory importFactory;
    private MemberFactory memberFactory;
    private ModifierFactory modifierFactory;
    private PackageFactory packageFactory;
    private QualifiedNameFactory qualifiedNameFactory;
    private TypeParameterFactory typeParameterFactory;
    private TypeSpecificationFactory typeSpecificationFactory;
    private StatementFactory statementFactory;
    private TypeDefinitionFactory typeDefinitionFactory;
    private FieldFactory fieldFactory;

    /**
     * Sets the graph to build, creates symbol table and all factories.
     * @param programGraph
     */
    public void setProgramGraph( Java5 programGraph ){
        this.programGraph = programGraph;
        annotationFactory = new AnnotationFactory( programGraph );
        identifierFactory = new IdentifierFactory( programGraph );
        memberFactory = new MemberFactory( programGraph );
        typeDefinitionFactory = new TypeDefinitionFactory( programGraph );
        typeParameterFactory = new TypeParameterFactory( symbolTable );
        constantFactory = new ConstantFactory( programGraph, symbolTable );
        expressionFactory  = new ExpressionFactory( programGraph, symbolTable );
        importFactory = new ImportFactory( programGraph, symbolTable );
        packageFactory = new PackageFactory( programGraph, symbolTable );
        modifierFactory = new ModifierFactory( programGraph, symbolTable );
        qualifiedNameFactory = new QualifiedNameFactory( programGraph, symbolTable );
        statementFactory = new StatementFactory( programGraph, symbolTable );
        typeSpecificationFactory = new TypeSpecificationFactory( programGraph, symbolTable );
        fieldFactory = new FieldFactory( programGraph, symbolTable );
    }

    /**
     * Sets the top element of the actual ASG to generate from AST walked.
     */
    public void setSourceUsage( SourceUsage sourceUsageVertex ){
        this.sourceUsageVertex = sourceUsageVertex;
    }

    /**
     * Sets the vertex the ASG is appended to.
     */
    public void setTranslationUnit( TranslationUnit translationUnitVertex ){
        this.translationUnitVertex = translationUnitVertex;
    }

    /**
     * Sets the vertex the ASG is appended to.
     */
    public void setProgramVertex( Program programVertex ){
        this.programVertex = programVertex;
    }

    /**
     * Holds the current vertex (equivalent to the last visited AST element).
     */
    private Vertex currentVertex = null;

    /**
     * Holds the scope which is currently valid (required for variables).
     */
    private Vertex currentScope = null;

    /**
     * Holds the last visited AST element (only needed to fill edge attributes in some cases).
     */
    private AST currentAST = null;

    /**
     * Holds the last begin AST element (needed to fill the edge attributes pointing from any complex structure).
     */
    private AST currentBeginAST = null;

    /**
     * Holds the last end AST element (needed to fill the edge attributes pointing from any complex structure).
     */
    private AST currentEndAST = null;

    /**
     * Holds the last AST element of a array type specification (needed only for this case, as current[Begin,End]AST is used for other informations).
     */
    private AST currentArrayTypeEndAST = null;

    /**
     * Needed to keep track of the dimensions of an array.
     */
    private int currentDimensionCount = 0;

    private void setBeginAndEndAST( AST ast ){
        currentBeginAST = currentEndAST = ast;
    }

    private void setBeginAndEndAST( AST beginAST, AST endAST ){
        currentBeginAST = beginAST;
        currentEndAST = endAST;
    }

    /**
     * This is needed only for a single case: there are two different elists below the traditional for clause,
     * so the elist needs to know which edge to create between it's expressions and the parent vertex (which is
     * of type TraditionalForClause in both cases).
     */
    private boolean createForInitializerEdge = false;

    /**
     * Holds the full qualified name of the currently parsed class.
     * @TODO implement something to get rid of it.
     */
    private String fullyQualifiedNameOfCurrentType;

    /**
     * Flag which shows if tree walker is in a method declaration/definition or constructor definition.
     */
    private boolean inMethod = false;

    /**
     * Flag which shows if tree walker is in a import declaration.
     */
    private boolean inImport = false;

    /**
     * Flag which shows if tree walker is in an annotation usage.
     */
    private boolean inAnnotation = false;

    /**
     * Flag which shows if tree walker is in a package definition.
     */
    boolean inPackageDefinition = false;

    /**
     * Holds last qualified name found in treewalk.
     */
    String currentFullyQualifiedName = "";

}

compilationUnit{ fullyQualifiedNameOfCurrentType = ""; currentFullyQualifiedName = ""; }
    :
    ( packageDefinition{ packageFactory.attach( ( PackageDefinition) currentVertex, sourceUsageVertex, currentBeginAST, currentEndAST ); }	)?
    ( importDefinition{ importFactory.attach( ( ImportDefinition )currentVertex, sourceUsageVertex, currentBeginAST, currentEndAST ); } )*
    ( typeDefinition{ typeDefinitionFactory.attachType( ( Type )currentVertex, sourceUsageVertex, currentBeginAST, currentEndAST ); } )*
    {
        statementFactory.reset();  // @TODO remove when labels are stored in symbol table and no longer in statement factory
        currentScope = null;
    }
    ;

packageDefinition
    :
    #(
        packageKeyWord:PACKAGE_DEF{
            Vertex parentVertex = currentVertex;
            PackageDefinition packageDefinitionVertex = programGraph.createPackageDefinition();
            currentVertex = packageDefinitionVertex;
            AST packageBegin = null;
            currentFullyQualifiedName = "";
        }
        annotations{
            if ( currentBeginAST == null ) packageBegin = packageKeyWord; // if there are annotations, they are the beginning of the package def.,
            else{
				packageBegin = currentBeginAST; // otherwise, the keyword is beginning of definition
			}
            inPackageDefinition = true;
            currentVertex = packageDefinitionVertex;
            currentFullyQualifiedName = "";
        }
        identifier{
			if( currentVertex instanceof JavaPackage ){
				packageFactory.attach( ( JavaPackage )currentVertex,  translationUnitVertex); //@TODO ast elements
			}
            QualifiedName qualifiedNameVertex = qualifiedNameFactory.createQualifiedName( currentFullyQualifiedName );
            packageFactory.attach( qualifiedNameVertex, packageDefinitionVertex, currentBeginAST, currentEndAST );
			fullyQualifiedNameOfCurrentType += currentFullyQualifiedName;
			currentFullyQualifiedName = "";
            inPackageDefinition = false;
            currentVertex = packageDefinitionVertex;
        }
        packageEnd:SEMI{ setBeginAndEndAST( packageBegin, packageEnd );	}
    )
    ;

importDefinition
    :
    (
        #(
            importBegin:IMPORT{ inImport = true; }
            identifierStar{
				ImportDefinition importVertex = null;
				QualifiedName qualifiedNameVertex = null;
				if( currentFullyQualifiedName.endsWith( ".*" ) ){
					qualifiedNameVertex = qualifiedNameFactory.createQualifiedName( currentFullyQualifiedName.substring( 0, currentFullyQualifiedName.length() - 2 ) );
					importVertex = importFactory.createPackageImportDefinition();
				}
				else{
					qualifiedNameVertex = qualifiedNameFactory.createQualifiedType( null, currentFullyQualifiedName );
					importVertex = importFactory.createClassImportDefinition();
				}
				importFactory.attach( qualifiedNameVertex, importVertex, currentBeginAST, currentEndAST );
				currentVertex = importVertex;
				currentFullyQualifiedName = "";
			}
            importEnd:SEMI{ setBeginAndEndAST( importBegin, importEnd ); }
        )
        |
        #(  //@TODO static imports
            staticImportBegin:STATIC_IMPORT{ inImport = true; }
            identifierStar{
				ImportDefinition importVertex = null;
				QualifiedName qualifiedNameVertex = null;
				if( currentFullyQualifiedName.endsWith( ".*" ) ){
					qualifiedNameVertex = qualifiedNameFactory.createQualifiedName( currentFullyQualifiedName.substring( 0, currentFullyQualifiedName.length() - 2 ) );
					importVertex = importFactory.createPackageImportDefinition();
				}
				else{
					qualifiedNameVertex = qualifiedNameFactory.createQualifiedType( null, currentFullyQualifiedName );
					importVertex = importFactory.createClassImportDefinition();
				}
				importFactory.attach( qualifiedNameVertex, importVertex, currentBeginAST, currentEndAST );
				currentVertex = importVertex;
				currentFullyQualifiedName = "";

				//currentVertex = importFactory.createImportDefinition( ( QualifiedName )currentVertex, currentBeginAST, currentEndAST );
			}
            staticImportEnd:SEMI{ setBeginAndEndAST( staticImportBegin, staticImportEnd ); }
        )
    )
    { inImport = false; }
    ;

typeDefinition{ Type typeVertex = null; }
    :
    (
        #(
            CLASS_DEF{
                ClassDefinition classDefinitionVertex = programGraph.createClassDefinition();
                classDefinitionVertex.set_external( false );
                currentVertex = classDefinitionVertex;
                // Check if this is the first type definition in the current file.
                if( currentScope == null ) symbolTable.setSupremeTypeInFile( classDefinitionVertex );
                Vertex parentScope = currentScope;
                currentScope = classDefinitionVertex;
                symbolTable.addScopeInfo( currentScope, parentScope );
                AST classBeginAST = null;
            }
            modifiers
            classKeyWord:"class"{
                if( currentBeginAST == null ) classBeginAST = classKeyWord; // there are no modifiers, so the class keyword is the first element.
                else classBeginAST = currentBeginAST; // otherwise, it is the first modifier.
		    }
            className:IDENT{
		    	identifierFactory.createIdentifier( classDefinitionVertex, className );
		    	classDefinitionVertex.set_name( className.getText() ); //@TODO obsolete, remove  from schema
		    	if( fullyQualifiedNameOfCurrentType.equals( "" ) ) fullyQualifiedNameOfCurrentType = className.getText();
		    	else fullyQualifiedNameOfCurrentType += "." + className.getText();
		    	classDefinitionVertex.set_fullyQualifiedName( fullyQualifiedNameOfCurrentType );
                // Check if it is a nested type.
                if( parentScope != null ) symbolTable.addNestedClassDefinition( fullyQualifiedNameOfCurrentType, classDefinitionVertex );
				else symbolTable.addTypeDefinition( fullyQualifiedNameOfCurrentType, classDefinitionVertex );
                currentVertex = classDefinitionVertex;
            }
            ( typeParameters{ currentVertex = classDefinitionVertex; } )?
            extendsClause{ currentVertex = classDefinitionVertex; }
            implementsClause{ currentVertex = classDefinitionVertex; }
            objBlock{
                typeDefinitionFactory.attachClassBlock( ( Block )currentVertex, classDefinitionVertex, currentBeginAST, currentEndAST );
                if( fullyQualifiedNameOfCurrentType.length() > classDefinitionVertex.get_name().length() )
                	fullyQualifiedNameOfCurrentType = fullyQualifiedNameOfCurrentType.substring( 0, fullyQualifiedNameOfCurrentType.length() - classDefinitionVertex.get_name().length() - 1 );
                typeVertex = classDefinitionVertex;
                currentBeginAST = classBeginAST; // currentEndAST already set correctly
                currentScope = parentScope;
            }
        )
        |
        #(
            INTERFACE_DEF{
                InterfaceDefinition interfaceDefinitionVertex = programGraph.createInterfaceDefinition();
                interfaceDefinitionVertex.set_external( false );
                currentVertex = interfaceDefinitionVertex;
                AST interfaceBeginAST = null;
                // Check if this is the first type definition in the current file.
                if( currentScope == null ) symbolTable.setSupremeTypeInFile( interfaceDefinitionVertex );
			    Vertex parentScope = currentScope;
			    currentScope = interfaceDefinitionVertex;
			    symbolTable.addScopeInfo( currentScope, parentScope );
            }
            modifiers
            interfaceKeyWord:"interface"{
                if( currentBeginAST == null ) interfaceBeginAST = interfaceKeyWord; // There are no modifiers, so the interface keyword is the first element.
                else interfaceBeginAST = currentBeginAST; // There are modifiers.
		    }
            interfaceName:IDENT{
		       	identifierFactory.createIdentifier( interfaceDefinitionVertex, interfaceName );
                interfaceDefinitionVertex.set_name( interfaceName.getText() ); //@TODO could be obsolete
		    	if( fullyQualifiedNameOfCurrentType.equals( "" ) ) fullyQualifiedNameOfCurrentType = interfaceName.getText();
		    	else fullyQualifiedNameOfCurrentType += "." + interfaceName.getText();
		    	interfaceDefinitionVertex.set_fullyQualifiedName( fullyQualifiedNameOfCurrentType );
		    	if( parentScope != null ) symbolTable.addNestedInterfaceDefinition( fullyQualifiedNameOfCurrentType, interfaceDefinitionVertex );
                else symbolTable.addTypeDefinition( fullyQualifiedNameOfCurrentType, interfaceDefinitionVertex );
                currentVertex = interfaceDefinitionVertex;
            }
            ( typeParameters{ currentVertex = interfaceDefinitionVertex; } )?
            extendsClause{ currentVertex = interfaceDefinitionVertex; }
            interfaceBlock{
                typeDefinitionFactory.attachInterfaceBlock( ( Block )currentVertex, interfaceDefinitionVertex, currentBeginAST, currentEndAST );
                if( fullyQualifiedNameOfCurrentType.length() > interfaceDefinitionVertex.get_name().length() )
                	fullyQualifiedNameOfCurrentType = fullyQualifiedNameOfCurrentType.substring( 0, fullyQualifiedNameOfCurrentType.length() - interfaceDefinitionVertex.get_name().length() - 1 );
                typeVertex = interfaceDefinitionVertex;
                currentBeginAST = interfaceBeginAST;  // currentEndAST already set correctly
                currentScope = parentScope;
            }
        )
        |
        #(
            ENUM_DEF{
                EnumDefinition enumDefinitionVertex = programGraph.createEnumDefinition();
                enumDefinitionVertex.set_external( false );
                currentVertex = enumDefinitionVertex;
                AST enumBeginAST = null;
                // Check if this is the first type definition in the current file.
                if( currentScope == null ) symbolTable.setSupremeTypeInFile( enumDefinitionVertex );
                Vertex parentScope = currentScope;
                currentScope = enumDefinitionVertex;
                symbolTable.addScopeInfo( currentScope, parentScope );
            }
            modifiers
            enumKeyWord:"enum"{
                if (currentBeginAST == null) enumBeginAST = enumKeyWord; // currentBeginAST == null means there are no modifiers, so the enum keyword is the first element.
                else enumBeginAST = currentBeginAST; // Otherwise, it is the first modifier.
		    }
            enumName:IDENT{
                identifierFactory.createIdentifier( enumDefinitionVertex, enumName );
                enumDefinitionVertex.set_name( enumName.getText() ); //@TODO could be obsolete
		    	if( fullyQualifiedNameOfCurrentType.equals( "" ) ) fullyQualifiedNameOfCurrentType = enumName.getText();
		    	else fullyQualifiedNameOfCurrentType += "." + enumName.getText();
		    	enumDefinitionVertex.set_fullyQualifiedName( fullyQualifiedNameOfCurrentType );
                if( parentScope != null ) symbolTable.addNestedEnumDefinition( fullyQualifiedNameOfCurrentType, enumDefinitionVertex );
                else symbolTable.addTypeDefinition( fullyQualifiedNameOfCurrentType, enumDefinitionVertex );
                currentVertex = enumDefinitionVertex;
            }
            implementsClause{ currentVertex = enumDefinitionVertex; }
            enumBlock{
                typeDefinitionFactory.attachEnumBlock( ( Block )currentVertex, enumDefinitionVertex, currentBeginAST, currentEndAST );
                if( fullyQualifiedNameOfCurrentType.length() > enumDefinitionVertex.get_name().length() )
                	fullyQualifiedNameOfCurrentType = fullyQualifiedNameOfCurrentType.substring( 0, fullyQualifiedNameOfCurrentType.length() - enumDefinitionVertex.get_name().length() - 1 );
                typeVertex = enumDefinitionVertex;
                currentBeginAST = enumBeginAST; // currentEndAST already set correctly
                currentScope = parentScope;
            }
        )
        |
        #(
            ANNOTATION_DEF{
                AnnotationDefinition annotationDefinitionVertex = programGraph.createAnnotationDefinition();
                annotationDefinitionVertex.set_external( false );
                currentVertex = annotationDefinitionVertex;
                AST annotationBeginAST = null;
                Vertex parentScope = currentScope;
                currentScope = annotationDefinitionVertex;
                symbolTable.addScopeInfo( currentScope, parentScope );
            }
            modifiers
            atSign:AT{
                if (currentBeginAST == null) annotationBeginAST = atSign; // currentBeginAST == null means there are no modifiers, so the annotation's "@" is the first element.
                else annotationBeginAST = currentBeginAST; // Otherwise, it is the first modifier.
		    }
            annotationName:IDENT{
                identifierFactory.createIdentifier( annotationDefinitionVertex, annotationName );
                annotationDefinitionVertex.set_name( annotationName.getText() );
                if( fullyQualifiedNameOfCurrentType.equals( "" ) ) fullyQualifiedNameOfCurrentType = annotationName.getText();
                else fullyQualifiedNameOfCurrentType += "." + annotationName.getText();
                annotationDefinitionVertex.set_fullyQualifiedName( fullyQualifiedNameOfCurrentType );
                if( parentScope != null) symbolTable.addNestedAnnotationDefinition( fullyQualifiedNameOfCurrentType, annotationDefinitionVertex );
                else symbolTable.addTypeDefinition( fullyQualifiedNameOfCurrentType, annotationDefinitionVertex );
                currentVertex = annotationDefinitionVertex;
            }
           annotationBlock{
               typeDefinitionFactory.attachAnnotationBlock( ( Block )currentVertex, annotationDefinitionVertex, currentBeginAST, currentEndAST );
               if( fullyQualifiedNameOfCurrentType.length() > annotationDefinitionVertex.get_name().length() )
                   fullyQualifiedNameOfCurrentType = fullyQualifiedNameOfCurrentType.substring( 0, fullyQualifiedNameOfCurrentType.length() - annotationDefinitionVertex.get_name().length() - 1 );
               typeVertex = annotationDefinitionVertex;
               currentBeginAST = annotationBeginAST; // currentEndAST already set correctly
               currentScope = parentScope;
           }
        )
    )
    { currentVertex = typeVertex; }
    ;

typeParameters{ Vertex parentVertex = currentVertex; }
    :
    #(
        TYPE_PARAMETERS
        (
            typeParameter{ typeParameterFactory.attachTypeParameter( ( TypeParameterDeclaration )currentVertex, parentVertex, currentBeginAST, currentEndAST ); }
        )+
    )
    ;

typeParameter{ Vertex parentVertex = currentVertex; }
    :
    #(
        TYPE_PARAMETER
        name:IDENT{
            TypeParameterDeclaration typeParameterDeclarationVertex = typeParameterFactory.createTypeParameterDeclaration( name, currentScope );
            currentVertex = typeParameterDeclarationVertex;
            currentEndAST = name;
        }
        (typeUpperBounds)?
        {
            currentVertex = typeParameterDeclarationVertex;
            currentBeginAST = name; // currentEndAST already set correctly
        }
    )
    ;

typeUpperBounds{ Vertex parentVertex = currentVertex; }
    :
    #(
        TYPE_UPPER_BOUNDS
        (
 			{
				QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
				currentVertex = qualifiedTypeVertex;
			}
 			classOrInterfaceType{
				symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
				typeParameterFactory.attachUpperBound( qualifiedTypeVertex, ( TypeParameterDeclaration )parentVertex, currentBeginAST, currentEndAST );
			}
        )+
    )
    ;

typeSpec : #(TYPE typeSpecArray ) ;

/**
 * Takes care of an array type specification.
 */
typeSpecArray
    :
    #(
        ARRAY_DECLARATOR
        {currentDimensionCount++;}
    	typeSpecArray{
            //System.Out.writeln("Found array type specification!");
            //currentDimensionCount++;
            currentArrayTypeEndAST = currentEndAST; // this must be set if there is no closing bracket (which is the case for int a[] array declarations)
		}
    	( arrayTypeEnd:RBRACK{ currentArrayTypeEndAST = arrayTypeEnd;} )?
    )
    |
        type
    ;

type{ Vertex parentVertex = currentVertex; }
    :
    (
        {
			QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
			currentVertex = qualifiedTypeVertex;
        }
        classType:classOrInterfaceType{
			symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
           	if( !typeSpecificationFactory.attachTypeSpecification( qualifiedTypeVertex, parentVertex, currentBeginAST, currentEndAST ) ) currentAST = classType;
            currentVertex = qualifiedTypeVertex;
        }
        |
        primitiveType:builtInType{
            // added on 2009-03-03 as quick fix by ultbreit
            if(currentDimensionCount == 0) //check if it is an array type specification or else it is attached twice as IsReturnTypeOf
            // end of quick fix
            	if( !typeSpecificationFactory.attachTypeSpecification( ( TypeSpecification )currentVertex, parentVertex, currentBeginAST, currentEndAST ) ) currentAST = primitiveType;
        }
    )
    ;

classOrInterfaceType{
    Vertex parentVertex = currentVertex;
    TypeSpecification typeSpecificationVertex = null; // Has to be null, otherwise arguments of a method invocation would be falsely attached as method names
}
    :
    typeName:IDENT{
        Utilities.addToFullyQualifiedName( parentVertex, typeName );
        setBeginAndEndAST( typeName );
    }
    (
        typeArguments{
            currentVertex = typeSpecificationVertex;
            currentBeginAST = typeName; // currentEndAST already set correctly
        }
    )?
    |
    #(
        DOT
        classOrInterfaceType
        typeNameWithDot:IDENT{
			Utilities.addToFullyQualifiedName( parentVertex, typeNameWithDot );
            currentEndAST = typeNameWithDot;
            AST typeBeginAST = currentBeginAST;
        }
        (
            typeArguments{
                currentBeginAST = typeBeginAST; // currentEndAST already set correctly
            }
        )?
    )
    ;

typeArguments{ Vertex parentVertex = currentVertex; }
    :
    #(
        TYPE_ARGUMENTS
    	(
		    typeArgument{
				if( parentVertex instanceof QualifiedType ){
					IsTypeArgumentOfTypeSpecification isTypeArgumentOfTypeSpecificationEdge = programGraph.createIsTypeArgumentOfTypeSpecification( ( TypeArgument )currentVertex, ( QualifiedType )parentVertex );
					Utilities.fillEdgeAttributesFromASTDifference( isTypeArgumentOfTypeSpecificationEdge, currentBeginAST, currentEndAST );
				}
				currentVertex = parentVertex; // reset currentVertex so that the next created edge is attached correctly
			}
		)+
    )
    ;

typeArgument{ Vertex parentVertex = currentVertex; }
    :
    #(
        TYPE_ARGUMENT{
            TypeArgument typeArgumentVertex = programGraph.createTypeArgument();
            // Determine if current type argument is part of a list of type arguments (e. g. SomeClass< String, File, Object >)
            if( parentVertex instanceof TypeArgument ){
                // yes: so get the only QualifiedType and attach list element to it
                QualifiedName qualifiedNameVertex = ( ( QualifiedName )( ( TypeArgument )parentVertex ).getFirstIsTypeArgumentOfTypeSpecificationIncidence().getOmega() );
            }
            currentVertex = typeArgumentVertex;
        }
        (
            typeSpec{
                typeParameterFactory.createSimpleArgument( typeArgumentVertex, ( TypeSpecification )currentVertex, currentBeginAST, currentEndAST );
            }
            |
            wildcardType{
				typeParameterFactory.attachWildCardArgument( ( WildcardArgument )currentVertex, typeArgumentVertex, currentBeginAST, currentEndAST );
            }
        )
        { currentVertex = typeArgumentVertex; }
    )
    ;

wildcardType
    :
    #(
        wildCardBegin:WILDCARD_TYPE{
            WildcardArgument wildcardArgumentVertex = programGraph.createWildcardArgument();
            currentVertex = wildcardArgumentVertex;
            currentEndAST = wildCardBegin;
        }
        ( typeArgumentBounds )?
        {
            currentVertex = wildcardArgumentVertex;
            currentBeginAST = wildCardBegin; // currentEndAST already set correctly
        }
    )
    ;

typeArgumentBounds{ WildcardArgument wildcardArgumentVertex = ( WildcardArgument )currentVertex; }
    :
    #(
        TYPE_UPPER_BOUNDS{
			QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
			currentVertex = qualifiedTypeVertex;
		}
        classOrInterfaceType{
			symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
			typeParameterFactory.attachUpperBound( wildcardArgumentVertex, qualifiedTypeVertex, currentBeginAST, currentEndAST );
        }
    )
    |
    #(
        TYPE_LOWER_BOUNDS{
			QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
			currentVertex = qualifiedTypeVertex;
		}
		classOrInterfaceType{
			symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
			typeParameterFactory.attachLowerBound( wildcardArgumentVertex, qualifiedTypeVertex, currentBeginAST, currentEndAST );
        }
    )
    ;

builtInType
    :
    (
        typeVoid:"void"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.VOID );
            setBeginAndEndAST( typeVoid );
        }
        |
        typeBoolean:"boolean"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.BOOLEAN );
            setBeginAndEndAST( typeBoolean );
        }
        |
        typeByte:"byte"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.BYTE );
            setBeginAndEndAST( typeByte );
        }
        |
        typeChar:"char"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.CHAR );
            setBeginAndEndAST( typeChar );
        }
        |
        typeShort:"short"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.SHORT );
            setBeginAndEndAST( typeShort );
        }
        |
        typeInt:"int"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.INT );
            setBeginAndEndAST( typeInt );
        }
        |
        typeFloat:"float"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.FLOAT );
            setBeginAndEndAST( typeFloat );
        }
        |
        typeLong:"long"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.LONG );
            setBeginAndEndAST( typeLong );
        }
        |
        typeDouble:"double"{
            currentVertex = typeSpecificationFactory.createBuiltInType( BuiltInTypes.DOUBLE );
            setBeginAndEndAST( typeDouble );
        }
    )
    ;

modifiers
    :
    #(
        MODIFIERS{
            Vertex parentVertex = currentVertex;
            currentBeginAST = null;
            AST modifiersBeginAST = null;
        }
        (
            modifier{
		    	if( currentVertex instanceof Annotation ) annotationFactory.attach( ( Annotation )currentVertex, parentVertex, currentBeginAST, currentEndAST );
		    	else modifierFactory.attachModifier( ( Modifier )currentVertex, parentVertex, currentBeginAST, currentEndAST );
                if( modifiersBeginAST == null ) modifiersBeginAST = currentBeginAST;
            }
        )*
        {
			if( modifiersBeginAST != null ) currentBeginAST = modifiersBeginAST; // currentEndAST already set correctly
            currentVertex = parentVertex;
		}
    )
    ;

modifier
    :
    (
        modPrivate:"private"{
            currentVertex = modifierFactory.createModifier( Modifiers.PRIVATE, symbolTable );
            setBeginAndEndAST( modPrivate );
        }
    |   modPublic:"public"{
            currentVertex = modifierFactory.createModifier( Modifiers.PUBLIC, symbolTable );
            setBeginAndEndAST( modPublic );
        }
    |   modProtected:"protected"{
            currentVertex = modifierFactory.createModifier( Modifiers.PROTECTED, symbolTable );
            setBeginAndEndAST( modProtected );
        }
    |   modStatic:"static"{
            currentVertex = modifierFactory.createModifier( Modifiers.STATIC, symbolTable );
            setBeginAndEndAST( modStatic );
        }
    |   modTransient:"transient"{
            currentVertex = modifierFactory.createModifier( Modifiers.TRANSIENT, symbolTable );
            setBeginAndEndAST( modTransient );
        }
    |   modFinal:"final"{
            currentVertex = modifierFactory.createModifier( Modifiers.FINAL, symbolTable );
            setBeginAndEndAST( modFinal );
        }
    |   modAbstract:"abstract"{
            currentVertex = modifierFactory.createModifier( Modifiers.ABSTRACT, symbolTable );
            setBeginAndEndAST( modAbstract );
        }
    |   modNative:"native"{
            currentVertex = modifierFactory.createModifier( Modifiers.NATIVE, symbolTable );
            setBeginAndEndAST( modNative );
        }
    |   modThreadsafe:"threadsafe"{
            currentVertex = modifierFactory.createModifier( Modifiers.VOLATILE, symbolTable );
            setBeginAndEndAST( modThreadsafe );
        }
    |   modSynchronized:"synchronized"{
            currentVertex = modifierFactory.createModifier( Modifiers.SYNCHRONIZED, symbolTable );
            setBeginAndEndAST( modSynchronized );
        }
    |   modVolatile:"volatile"{
            currentVertex = modifierFactory.createModifier( Modifiers.VOLATILE, symbolTable );
            setBeginAndEndAST( modVolatile );
        }
    |   modStrictfp:"strictfp"{
            currentVertex = modifierFactory.createModifier( Modifiers.STRICTFP, symbolTable );
            setBeginAndEndAST( modStrictfp );
        }
    |   annotation // arrrgs...
    )
    ;

annotations{
    Vertex parentVertex = currentVertex;
    AST annotationListBegin = null;
}
    :
    #(
        ANNOTATIONS
        (
            annotation{

                annotationFactory.attach( ( Annotation )currentVertex, parentVertex, currentBeginAST, currentEndAST );
                if( annotationListBegin == null ) annotationListBegin = currentBeginAST;
            }
        )*
        { currentBeginAST = annotationListBegin; } // currentEndAST already set correctly
    )
    ;

annotation
    :
    #(
        ANNOTATION{
            Annotation annotationVertex = programGraph.createAnnotation();
            currentVertex = annotationVertex;
            inAnnotation = true;
        }
        annotationBegin:AT
        identifier{
            QualifiedName qualifiefNameVertex = qualifiedNameFactory.createQualifiedType( currentScope, currentFullyQualifiedName );
            annotationFactory.attachAnnotationName( qualifiefNameVertex, annotationVertex, currentBeginAST, currentEndAST );
            currentVertex = annotationVertex;
        }
        (
            annotationMemberValueInitializer{
                annotationFactory.attachAnnotationArgument( currentVertex, annotationVertex, currentBeginAST, currentEndAST );
                currentVertex = annotationVertex;
            }
            |
            (
                annotationMemberValuePair{
                    annotationFactory.attachAnnotationArgument( currentVertex, annotationVertex, currentBeginAST, currentEndAST );
                    currentVertex = annotationVertex;
                }
            )+
        )?
        ( annotationEnd:RPAREN{ currentEndAST = annotationEnd; } )?
        {
            currentBeginAST = annotationBegin; // currentEndAST already set correctly
            inAnnotation = false;
        }
    )
    ;

annotationMemberValueInitializer{ Vertex parentVertex = currentVertex; }
    :
    {
        VariableInitializer variableInitializerVertex = programGraph.createVariableInitializer();
        currentVertex = variableInitializerVertex;
    }
    conditionalExpr{ // check if expression is right hand side of an infix expression
        if( parentVertex instanceof InfixExpression ){
            expressionFactory.attachRightHandSide( ( InfixExpression )parentVertex, variableInitializerVertex, currentBeginAST, currentEndAST );
            currentVertex = variableInitializerVertex;
        }
        else if( parentVertex instanceof AnnotationField ){
// added on 2009-03-03 as quick fix by abaldauf
            expressionFactory.attachVariableInitializer( ( Expression )currentVertex, variableInitializerVertex, currentBeginAST, currentEndAST );
// end fix
            annotationFactory.attachDefaultValue( ( AnnotationField )parentVertex, variableInitializerVertex, currentBeginAST, currentEndAST );
        }
        else if( parentVertex instanceof Annotation ) currentVertex = variableInitializerVertex; // must be a variable initializer serving as argument for an annotation (like some int constant)
    }
    |
    annotation
    |
    annotationMemberArrayInitializer{
        ArrayCreation arrayCreationVertex = expressionFactory.createArrayCreation( ( ArrayInitializer )currentVertex, currentBeginAST, currentEndAST );
        if( parentVertex instanceof InfixExpression ) expressionFactory.attachRightHandSide( ( InfixExpression )parentVertex, arrayCreationVertex, currentBeginAST, currentEndAST );
        currentVertex = arrayCreationVertex;
    }
    ;

annotationMemberValuePair
    :
    #(
        ANNOTATION_MEMBER_VALUE_PAIR{ InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.ASSIGNMENT ); }
        ident:IDENT{ // ident is the left hand side of the infix expression
            FieldAccess fieldAccessVertex = fieldFactory.createFieldAccess( ident, currentScope );
            expressionFactory.attachLeftHandSide( infixExpressionVertex, fieldAccessVertex, ident, ident );
            currentVertex = infixExpressionVertex;
        }
        annotationMemberValueInitializer{
           currentVertex = infixExpressionVertex;
           currentBeginAST = ident; // currentEndAST already set correctly
        }
    )
    ;

annotationMemberArrayInitializer
    :
    #(
        arrayInitBegin:ANNOTATION_ARRAY_INIT{ ArrayInitializer arrayInitializerVertex = programGraph.createArrayInitializer(); }
        ( annotationMemberArrayValueInitializer{ annotationFactory.attachContent( arrayInitializerVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST ); } )*
        arrayInitEnd:RCURLY{
            currentVertex = arrayInitializerVertex;
            setBeginAndEndAST( arrayInitBegin, arrayInitEnd );
        }
    )
    ;

annotationMemberArrayValueInitializer : conditionalExpr | annotation ;

extendsClause{ Vertex parentVertex = currentVertex; }
    :
    #(
        EXTENDS_CLAUSE
        (

            {
				QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
				currentVertex = qualifiedTypeVertex;
			}
            classOrInterfaceType{
				symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
				typeDefinitionFactory.attachSuperClass( parentVertex, qualifiedTypeVertex, currentBeginAST, currentEndAST );
            }
        )*
    )
    ;
/**
 * Takes care of implements clause in a class declaration. E. g. "class String implements Comparable"
 */
implementsClause{ Vertex parentVertex = currentVertex; }
    :
    #(
        IMPLEMENTS_CLAUSE
        (
			{
				QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType(); //vertex representing implemented interface
				currentVertex = qualifiedTypeVertex;
			}
			classOrInterfaceType{
				symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
				typeDefinitionFactory.attachInterface( parentVertex, qualifiedTypeVertex, currentBeginAST, currentEndAST );
			}
		)*
    )
    ;

interfaceBlock
    :
    #(
        OBJBLOCK{
            Block blockVertex = programGraph.createBlock();
            Vertex parentScope = currentScope;
            currentScope = blockVertex;
            symbolTable.addScopeInfo( currentScope, parentScope );
        }
        blockBegin:LCURLY
        (
            methodDef{ memberFactory.attachMember( ( Member )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            variableDef{
                Field fieldVertex = memberFactory.createField( blockVertex, currentAST );
                memberFactory.attachFieldCreation( ( Statement )currentVertex, fieldVertex, currentBeginAST, currentEndAST );
            }
            |
            typeDefinition{ memberFactory.attachMember( ( Type )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
        )*
        blockEnd:RCURLY{
            currentVertex = blockVertex;
            setBeginAndEndAST( blockBegin, blockEnd );
            currentScope = parentScope;
        }
    )
    ;

objBlock
    :
    #(
        OBJBLOCK{
            Block blockVertex = programGraph.createBlock();
            Vertex parentScope = currentScope;
            currentScope = blockVertex;
            symbolTable.addScopeInfo( currentScope, parentScope );
        }
        blockBegin:LCURLY
        (
            ctorDef{ memberFactory.attachMember( ( ConstructorDefinition )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            methodDef{ memberFactory.attachMember( ( MethodDeclaration )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            variableDef{
                Field fieldVertex = memberFactory.createField( blockVertex, currentAST );
                IsFieldCreationOf isFieldCreationOfEdge = memberFactory.attachFieldCreation( ( Statement )currentVertex, fieldVertex, currentBeginAST, currentEndAST );
            }
            ( varDefEnd:SEMI{ Utilities.fillEdgeAttributesFromASTDifference( isFieldCreationOfEdge, currentBeginAST, varDefEnd ); } )?
            |
            typeDefinition{ memberFactory.attachMember( ( Type )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            #(
                STATIC_INIT staticBeginAST:"static"{
                    Block blockVertexOfStaticInitializer = programGraph.createBlock(); //create it here because it is needed in slist
                    currentVertex = blockVertexOfStaticInitializer;
                }
                slist{ memberFactory.createStaticInitializer( blockVertexOfStaticInitializer, blockVertex, currentBeginAST, currentEndAST ); }
            )
            |
            #(
                INSTANCE_INIT{
                    Block blockVertexOfStaticConstructor = programGraph.createBlock(); //create it here because it is needed in slist
                    currentVertex = blockVertexOfStaticConstructor;
                }
                slist{ memberFactory.createStaticConstructor( blockVertexOfStaticConstructor, blockVertex, currentBeginAST, currentEndAST ); }
            )
        )*
        blockEnd:RCURLY{
            currentVertex = blockVertex;
            setBeginAndEndAST( blockBegin, blockEnd );
            currentScope = parentScope;
        }
    )
    ;

annotationBlock
    :
    #(
        OBJBLOCK{
            Block blockVertex = programGraph.createBlock();
            Vertex parentScope = currentScope;
            currentScope = blockVertex;
            symbolTable.addScopeInfo( currentScope, parentScope );
        }
        blockBegin:LCURLY
        (
            annotationFieldDecl{ memberFactory.attachMember( ( AnnotationField )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            variableDef{
                Field fieldVertex = memberFactory.createField( blockVertex, currentAST );
                memberFactory.attachFieldCreation( ( Statement )currentVertex, fieldVertex, currentBeginAST, currentEndAST );
            }
            |
            typeDefinition{ memberFactory.attachMember( ( Type )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
         )*
         blockEnd:RCURLY{
            currentVertex = blockVertex;
            setBeginAndEndAST( blockBegin, blockEnd );
            currentScope = parentScope;
        }
    )
    ;

enumBlock
    :
    #(
        OBJBLOCK{
            Block blockVertex = programGraph.createBlock();
            Vertex parentScope = currentScope;
            currentScope = blockVertex;
            symbolTable.addScopeInfo( currentScope, parentScope );
        }
        blockBegin:LCURLY
        (
            enumConstantDef{ memberFactory.attachMember( ( EnumConstant )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
        )*
        (
            ctorDef{ memberFactory.attachMember( ( ConstructorDefinition )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            methodDef{ memberFactory.attachMember( ( MethodDeclaration )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            variableDef{
                Field fieldVertex = memberFactory.createField( blockVertex, currentAST );
                memberFactory.attachFieldCreation( ( Statement )currentVertex, fieldVertex, currentBeginAST, currentEndAST );
            }
            |
            typeDefinition{ memberFactory.attachMember( ( Type )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            #(
                STATIC_INIT staticBeginAST:"static"{
                    Block blockVertexOfStaticInitializer = programGraph.createBlock();
                    currentVertex = blockVertexOfStaticInitializer;
                }
                slist{ memberFactory.createStaticInitializer( blockVertexOfStaticInitializer, blockVertex, currentBeginAST, currentEndAST ); }
            )
            |
            #(
                INSTANCE_INIT{
                    Block blockVertexOfStaticConstructor = programGraph.createBlock();
                    currentVertex = blockVertexOfStaticConstructor;
                }
                slist{ memberFactory.createStaticConstructor( blockVertexOfStaticConstructor, blockVertex, currentBeginAST, currentEndAST ); }
            )
        )*
        blockEnd:RCURLY{
            currentVertex = blockVertex;
            setBeginAndEndAST( blockBegin, blockEnd );
            currentScope = parentScope;
        }
    )
    ;

ctorDef{ inMethod = true; }
    :
    #(
        CTOR_DEF{
            ConstructorDefinition constructorDefinitionVertex = programGraph.createConstructorDefinition();
            currentVertex = constructorDefinitionVertex;
            AST constructorBeginAST = null;
			Vertex parentScope = currentScope;
			currentScope = constructorDefinitionVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
            symbolTable.addConstructorDefinition( fullyQualifiedNameOfCurrentType, constructorDefinitionVertex );
        }
        modifiers{
            currentVertex = constructorDefinitionVertex;
            constructorBeginAST = currentBeginAST; // if there are modifiers, the begin element will be set; otherwise, it remains null.
        }
        (
            typeParameters{
                currentVertex = constructorDefinitionVertex;
                if( constructorBeginAST == null ) constructorBeginAST = currentBeginAST; // if there have been no modifiers, set the begin element.
            }
        )?
        methodHead{
            if (constructorBeginAST == null) constructorBeginAST = currentBeginAST; // if there have been no modifiers and no type parameters, now there definitely is a value to be set.
        }
        (
            {
                Block blockVertexOfConstructor = programGraph.createBlock();
                currentVertex = blockVertexOfConstructor;
            }
            slist{ memberFactory.attachBlock( blockVertexOfConstructor, constructorDefinitionVertex, currentBeginAST, currentEndAST ); }
        )?
        {
            currentVertex = constructorDefinitionVertex;
            currentBeginAST = constructorBeginAST; // currentEndAST already set correctly
            currentScope = parentScope;
        }
    )
    { inMethod = false; }
    ;

methodDef{
	inMethod = true;
	// added on 2009-03-03 as quick fix by ultbreit
    TypeSpecification typeSpecificationVertex = null;
    AST typeSpecificationBeginAST = null;
    AST typeSpecificationEndAST = null;
	//end of quickfix
}
    :
    #(
        METHOD_DEF{
            MethodDeclaration methodDefinitionVertex = programGraph.createMethodDeclaration();
            Block blockVertexOfMethod = programGraph.createBlock(); // if it's a definition, otherwise it'll be deleted later...
            currentVertex = methodDefinitionVertex;
            AST methodBeginAST = null;
			Vertex parentScope = currentScope;
			currentScope = methodDefinitionVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
        }

        modifiers{
			currentDimensionCount = 0; //set dimensions to 0 because we do not know yet if next rule has an array declaration
            currentVertex = methodDefinitionVertex;
            methodBeginAST = currentBeginAST; // if there are modifiers, the begin element will be set; otherwise, it remains null.
        }
        (
        typeParameters{
            currentVertex = methodDefinitionVertex;
            if (methodBeginAST == null) methodBeginAST = currentBeginAST; // if there have been no modifiers, set the begin element.
        }
        )?
        typeSpec{
            // added on 2009-03-03 as quick fix by ultbreit
            typeSpecificationVertex = ( TypeSpecification )currentVertex; // keep reference
            typeSpecificationBeginAST = currentBeginAST;
            typeSpecificationEndAST = currentEndAST;
            //end of quickfix
            // added on 2009-03-03 as quick fix by ultbreit
            if( currentDimensionCount > 0 ){
                if (typeSpecificationVertex.getLastIncidence() != null) {
                    typeSpecificationVertex.getLastIncidence().delete(); // this one is not needed anymore...
                }
                ArrayType arrayTypeVertex = typeSpecificationFactory.createArrayType( currentDimensionCount, typeSpecificationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
                typeSpecificationFactory.attachTypeSpecification( arrayTypeVertex, methodDefinitionVertex, typeSpecificationBeginAST, currentArrayTypeEndAST );
            }
			//else typeSpecificationFactory.attachTypeSpecification( typeSpecificationVertex, methodDefinitionVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
			// end of quick fix
            currentVertex = methodDefinitionVertex;
            if (methodBeginAST == null) methodBeginAST = currentBeginAST; // if there have been no modifiers an no type parameters, now there definitely is a value to be set.
        }
        methodHead{
			currentVertex = methodDefinitionVertex;
		}
        ({
            currentVertex = blockVertexOfMethod;
         }
         slist { // statement list
                MethodDeclaration old = methodDefinitionVertex;
                methodDefinitionVertex = programGraph.createMethodDefinition();
                Edge curr = old.getFirstIncidence();
                    while (curr != null) {
                        Edge next = curr.getNextIncidence();
                        curr.setThis(methodDefinitionVertex);
                        curr = next;
                }
                old.delete();
                memberFactory.attachBlock( (Block) currentVertex, (MethodDefinition) methodDefinitionVertex,
                                           currentBeginAST, currentEndAST );
                currentVertex = methodDefinitionVertex;
         })?

        ( methodDefinitionEnd:SEMI{ currentEndAST = methodDefinitionEnd; } )?
        {
            if (!(methodDefinitionVertex instanceof MethodDefinition)) {
                blockVertexOfMethod.delete();
            }
            currentBeginAST = methodBeginAST; // currentEndAST already set correctly
            currentScope = parentScope;
            symbolTable.addMethodDeclaration( fullyQualifiedNameOfCurrentType, methodDefinitionVertex );
        }
    )
    { inMethod = false; }
    ;

variableDef{
    TypeSpecification typeSpecificationVertex = null;
    AST typeSpecificationBeginAST = null;
    AST typeSpecificationEndAST = null;
}
    :
    #(
        VARIABLE_DEF{
            VariableDeclaration variableDeclarationVertex = programGraph.createVariableDeclaration();
            currentVertex = variableDeclarationVertex;
            AST variableBeginAST = null;
        }
        modifiers{
            currentDimensionCount = 0; //set dimensions to 0 because we do not know yet if next rule has an array declaration
            currentVertex = variableDeclarationVertex;
            variableBeginAST = currentBeginAST; // if there are modifiers, the begin element will be set; otherwise, it remains null.
        }
        typeSpec{
            typeSpecificationVertex = ( TypeSpecification )currentVertex; // keep reference
            currentVertex = variableDeclarationVertex;
            if (variableBeginAST == null) variableBeginAST = currentBeginAST; // if there have been no modifiers, now there definitely is a value to be set.
            typeSpecificationBeginAST = currentBeginAST;
            typeSpecificationEndAST = currentEndAST;
        }
        variableDeclarator{
            identifierFactory.attachIdentifier( ( Identifier )currentVertex, variableDeclarationVertex, currentBeginAST, currentEndAST );
            currentAST = currentEndAST; // save ident as currentAST for position informations of the field name in invoking rules...
            if( currentDimensionCount > 0 ){
                ArrayType arrayTypeVertex = typeSpecificationFactory.createArrayType( currentDimensionCount, typeSpecificationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
                typeSpecificationFactory.attachTypeSpecification( arrayTypeVertex, variableDeclarationVertex, typeSpecificationBeginAST, currentArrayTypeEndAST );
            }
            else typeSpecificationFactory.attachTypeSpecification( typeSpecificationVertex, variableDeclarationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
        }
        varInitializer{
            //check if this is really a definition or just a declaration
            if( currentVertex != null ) expressionFactory.attachExpression( ( Expression )currentVertex, variableDeclarationVertex, currentBeginAST, currentEndAST );
            currentVertex = variableDeclarationVertex;
            currentBeginAST = variableBeginAST;	// currentEndAST already set correctly
            fieldFactory.addVariableDeclaration( variableDeclarationVertex, currentScope, fullyQualifiedNameOfCurrentType );
        }
    )
    ;

parameterDef{
	// added on 2009-03-03 as quick fix by ultbreit
    TypeSpecification typeSpecificationVertex = null;
    AST typeSpecificationBeginAST = null;
    AST typeSpecificationEndAST = null;
	//end of quickfix
}
    :
    #(
        PARAMETER_DEF{
            ParameterDeclaration parameterDeclarationVertex = programGraph.createParameterDeclaration();
            currentVertex = parameterDeclarationVertex;
            AST parameterBeginAST = null;
        }
        modifiers{
			currentDimensionCount = 0; //set dimensions to 0 because we do not know yet if next rule has an array declaration
            currentVertex = parameterDeclarationVertex;
            parameterBeginAST = currentBeginAST; // if there are modifiers, the begin element will be set; otherwise, it remains null.
        }
        typeSpec{
            // added on 2009-03-03 as quick fix by ultbreit
            typeSpecificationVertex = ( TypeSpecification )currentVertex; // keep reference
            typeSpecificationBeginAST = currentBeginAST;
            typeSpecificationEndAST = currentEndAST;
            //end of quickfix
            currentVertex = parameterDeclarationVertex;
            if (parameterBeginAST == null) parameterBeginAST = currentBeginAST; // if there have been no modifiers, now there definitely is a value to be set.
        }
        parameterName:IDENT{
            // added on 2009-03-03 as quick fix by ultbreit
            if( currentDimensionCount > 0 ){
                if (typeSpecificationVertex.getLastIncidence() != null) {
                    typeSpecificationVertex.getLastIncidence().delete(); // this one is not needed anymore...
                }
                ArrayType arrayTypeVertex = typeSpecificationFactory.createArrayType( currentDimensionCount, typeSpecificationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
                typeSpecificationFactory.attachTypeSpecification( arrayTypeVertex, parameterDeclarationVertex, typeSpecificationBeginAST, currentArrayTypeEndAST );
            }
			//else typeSpecificationFactory.attachTypeSpecification( typeSpecificationVertex, parameterDeclarationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
			// end of quick fix
            identifierFactory.createIdentifier( parameterDeclarationVertex, parameterName );
            setBeginAndEndAST( parameterBeginAST, parameterName );
            fieldFactory.addParameterDeclaration( parameterDeclarationVertex, currentScope );
        }
    )
    ;

variableLengthParameterDef{
	// added on 2009-03-03 as quick fix by ultbreit
    TypeSpecification typeSpecificationVertex = null;
    AST typeSpecificationBeginAST = null;
    AST typeSpecificationEndAST = null;
	//end of quickfix
}
    :
    #(
        VARIABLE_PARAMETER_DEF{
            VariableLengthDeclaration variableLengthDeclarationVertex = programGraph.createVariableLengthDeclaration();
            currentVertex = variableLengthDeclarationVertex;
            AST varParameterBeginAST = null;
        }
        modifiers{
			currentDimensionCount = 0; //set dimensions to 0 because we do not know yet if next rule has an array declaration
            currentVertex = variableLengthDeclarationVertex;
            varParameterBeginAST = currentBeginAST; // if there are modifiers, the begin element will be set; otherwise, it remains null.
        }
        typeSpec{
            // added on 2009-03-03 as quick fix by ultbreit
            typeSpecificationVertex = ( TypeSpecification )currentVertex; // keep reference
            typeSpecificationBeginAST = currentBeginAST;
            typeSpecificationEndAST = currentEndAST;
            //end of quickfix
            currentVertex = variableLengthDeclarationVertex;
            if (varParameterBeginAST == null) varParameterBeginAST = currentBeginAST; // if there have been no modifiers, now there definitely is a value to be set.
        }
        parameterName:IDENT{
            // added on 2009-03-03 as quick fix by ultbreit
            if( currentDimensionCount > 0 ){
                if (typeSpecificationVertex.getLastIncidence() != null) {
                    typeSpecificationVertex.getLastIncidence().delete(); // this one is not needed anymore...
                }
                ArrayType arrayTypeVertex = typeSpecificationFactory.createArrayType( currentDimensionCount, typeSpecificationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
                typeSpecificationFactory.attachTypeSpecification( arrayTypeVertex, variableLengthDeclarationVertex, typeSpecificationBeginAST, currentArrayTypeEndAST );
            }
			//else typeSpecificationFactory.attachTypeSpecification( typeSpecificationVertex, variableLengthDeclarationVertex, typeSpecificationBeginAST, typeSpecificationEndAST );
			// end of quick fix
			identifierFactory.createIdentifier( variableLengthDeclarationVertex, parameterName );
            setBeginAndEndAST( varParameterBeginAST, parameterName );
            fieldFactory.addParameterDeclaration( variableLengthDeclarationVertex, currentScope );
        }
    )
    ;

annotationFieldDecl
    :
    #(
        ANNOTATION_FIELD_DEF{
            AnnotationField annotationFieldVertex = programGraph.createAnnotationField();
            currentVertex = annotationFieldVertex;
            AST annotationBeginAST = null;
        }
        modifiers{
            currentVertex = annotationFieldVertex;
            annotationBeginAST = currentBeginAST; // if there are modifiers, the begin element will be set; otherwise, it remains null.
        }
        typeSpec{
            currentVertex = annotationFieldVertex;
            if (annotationBeginAST == null) annotationBeginAST = currentBeginAST; // if there have been no modifiers, now there definitely is a value to be set.
        }
        annotationFieldName:IDENT{
            identifierFactory.createIdentifier( annotationFieldVertex, annotationFieldName );
            currentVertex = annotationFieldVertex;
            currentEndAST = annotationFieldName;
        }
        ( annotationMemberValueInitializer{ currentVertex = annotationFieldVertex; } )?
        {
			currentBeginAST = annotationBeginAST; // currentEndAST already set correctly
		}
    )
    ;

enumConstantDef
    :
    #(
        ENUM_CONSTANT_DEF{
            EnumConstant enumConstantVertex = programGraph.createEnumConstant();
            currentVertex = enumConstantVertex;
            AST enumConstantBeginAST = null;
        }
        annotations{
			if( currentBeginAST != null ) enumConstantBeginAST = currentBeginAST;
		}
        enumName:IDENT{
			identifierFactory.createIdentifier( enumConstantVertex, enumName );
            currentVertex = enumConstantVertex;
            currentEndAST = enumName;
            if( enumConstantBeginAST == null ) enumConstantBeginAST = enumName; // in case there were no annotations, this is the begin of the enum constant.
        }
        ( elist )?
        ( enumConstantBlock{ memberFactory.attachBlock( ( Block )currentVertex, enumConstantVertex,  currentBeginAST, currentEndAST ); } )?
        {
            currentVertex = enumConstantVertex;
            currentBeginAST = enumConstantBeginAST; // currentEndAST already set correctly
            symbolTable.addEnumConstant( fullyQualifiedNameOfCurrentType, enumConstantVertex );
        }
    )
    ;

enumConstantBlock
    :
    #(
        OBJBLOCK{
            Block blockVertex = programGraph.createBlock();
            Vertex parentScope = currentScope;
            currentScope = blockVertex;
            symbolTable.addScopeInfo( currentScope, parentScope );
        }
        blockBegin:LCURLY
        (
            methodDef{ memberFactory.attachMember( ( MethodDeclaration )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            variableDef{
                Field fieldVertex = memberFactory.createField( blockVertex, currentAST );
                memberFactory.attachFieldCreation( ( Statement )currentVertex, fieldVertex, currentBeginAST, currentEndAST );
            }
            |
            typeDefinition{ memberFactory.attachMember( ( Type )currentVertex, blockVertex, currentBeginAST, currentEndAST ); }
            |
            #(
                INSTANCE_INIT{
                    Block blockVertexOfStaticConstructor = programGraph.createBlock();
                    currentVertex = blockVertexOfStaticConstructor;
                }
                slist{ memberFactory.createStaticConstructor( blockVertexOfStaticConstructor, blockVertex, currentBeginAST, currentEndAST ); }
            )
        )*
        blockEnd:RCURLY{
            currentVertex = blockVertex;
            setBeginAndEndAST( blockBegin, blockEnd );
            currentScope = parentScope;
        }
    )
    ;

objectinitializer :  #(INSTANCE_INIT slist) ; // this rule is not used

variableDeclarator
    :
    variableName:IDENT{
    	currentVertex = identifierFactory.createIdentifier( variableName );
        setBeginAndEndAST( variableName, currentEndAST = variableName );
    }
    |
    LBRACK
    RBRACK
    variableDeclarator
    ;

varInitializer
    :
    #(
        ASSIGN
        initializer
    )
    |
    { currentVertex = null; } //empty alternative means this is no full definition but only a declaration
    ;

initializer{ Vertex parentVertex = currentVertex; }
    :
    expression
    |
    {
        ArrayCreation arrayCreationVertex = null;
        if( !( parentVertex instanceof ArrayInitializer ) && !( parentVertex instanceof ArrayCreation ) ){
            arrayCreationVertex = programGraph.createArrayCreation();
            currentVertex = arrayCreationVertex;
        }
    }
    arrayInitializer{
        if( ( arrayCreationVertex != null ) && ( currentVertex instanceof ArrayInitializer ) ){
            // @TODO someFactory.attachArrayInitializer( ( ArrayInitializer )currentVertex, arrayCreationVertex, currentBeginAST, currentEndAST );
            IsDimensionInitializerOf isDimensionInitializerOfEdge = programGraph.createIsDimensionInitializerOf( ( ArrayInitializer )currentVertex, arrayCreationVertex );
            Utilities.fillEdgeAttributesFromASTDifference( isDimensionInitializerOfEdge, currentBeginAST, currentEndAST );
            currentVertex = arrayCreationVertex;
        }
    }
    ;

arrayInitializer
    :
    #(
        initBegin:ARRAY_INIT{
            ArrayInitializer arrayInitializerVertex = programGraph.createArrayInitializer();
            currentVertex = arrayInitializerVertex;
        }
        (
            initializer{
				// @TODO The Class-Object you get by using int.class can be content of an array
				// in that case a BuiltInType is encountered at this point. FiledAccess class is already lost at this point.
				if( currentVertex instanceof Expression )
                	expressionFactory.attachContent( ( Expression )currentVertex, arrayInitializerVertex, currentBeginAST, currentEndAST );
                // @TODO else BuiltInType
                currentVertex = arrayInitializerVertex;
            }
        )*
        initEnd:RCURLY{ setBeginAndEndAST( initBegin, initEnd ); }
    )
    ;

methodHead{ Vertex parentVertex = currentVertex; }
    :
    methodName:IDENT{ identifierFactory.createIdentifier( parentVertex, methodName ); }
    #(
        PARAMETERS
        ( parameterDef{ memberFactory.attachParameterDeclaration( ( ParameterDeclaration )currentVertex, parentVertex, currentBeginAST, currentEndAST ); } )*
        ( variableLengthParameterDef{ memberFactory.attachParameterDeclaration( ( VariableLengthDeclaration )currentVertex, parentVertex, currentBeginAST, currentEndAST ); } )?
    )
    parametersEnd:RPAREN{
		currentEndAST = parametersEnd;
        currentVertex = parentVertex;
    }
    ( throwsClause )?
    {
		currentBeginAST = methodName; // currentEndAST already set correctly.
    }
    ;

throwsClause{ Vertex parentVertex = currentVertex; }
    :
    #(
        "throws"
        (
			{
				QualifiedType qualifiedTypeVertex = programGraph.createQualifiedType();
				currentVertex = qualifiedTypeVertex;
			}
			classOrInterfaceType{
				symbolTable.addUnresolvedTypeSpecification( currentScope, qualifiedTypeVertex );
				memberFactory.attachExceptionThrown( qualifiedTypeVertex, parentVertex, currentBeginAST, currentEndAST );
			}
		)*
    )
    ;

identifier // This rule is only used by package definitions, import definitions (partially) and annotations.
    :
    (
        name:IDENT{
            setBeginAndEndAST( name );
            if( currentFullyQualifiedName.length() > 0 ) currentFullyQualifiedName += ".";
            currentFullyQualifiedName += name.getText();
			JavaPackage javaPackageVertex = null;
           	if( inPackageDefinition ){
				// @TODO refactor to PackageFactory
                if( symbolTable.hasJavaPackage( currentFullyQualifiedName ) ) javaPackageVertex = symbolTable.getJavaPackage( currentFullyQualifiedName );
           	    else{
           	    	javaPackageVertex = programGraph.createJavaPackage();
           	    	javaPackageVertex.set_fullyQualifiedName( currentFullyQualifiedName );
           	    	symbolTable.addJavaPackage( currentFullyQualifiedName, javaPackageVertex );
				}
                if( currentVertex instanceof JavaPackage && javaPackageVertex.getFirstIsSubPackageOfIncidence() == null ) packageFactory.attach( javaPackageVertex, ( JavaPackage )currentVertex );
                currentVertex = javaPackageVertex;
            }
			currentVertex = javaPackageVertex;
        }
        |
        #(
            DOT
            identifier
            nameWithDot:IDENT{
                if( currentFullyQualifiedName.length() > 0 ) currentFullyQualifiedName += ".";
                currentFullyQualifiedName += nameWithDot.getText();
                if( inPackageDefinition ){
					JavaPackage javaPackageVertex = null;
					// @TODO refactor to PackageFactory
					if( symbolTable.hasJavaPackage( currentFullyQualifiedName ) ) javaPackageVertex = symbolTable.getJavaPackage( currentFullyQualifiedName );
					else{
						javaPackageVertex = programGraph.createJavaPackage();
						javaPackageVertex.set_fullyQualifiedName( currentFullyQualifiedName );
						symbolTable.addJavaPackage( currentFullyQualifiedName, javaPackageVertex );
					}
					if( currentVertex instanceof JavaPackage && javaPackageVertex.getFirstIsSubPackageOfIncidence() == null ) packageFactory.attach( javaPackageVertex, ( JavaPackage )currentVertex );
                	currentVertex = javaPackageVertex;
				}
                currentEndAST = nameWithDot; // currentBeginAST already set correctly
            }
        )
    )
    ;

identifierStar
    :
    ident:IDENT{
        if( currentFullyQualifiedName.length() > 0 ) currentFullyQualifiedName += ".";
        currentFullyQualifiedName += ident.getText();
        setBeginAndEndAST( ident );
    }
    |
    #(
        DOT
        identifier
        (
            star:STAR{
		        if( currentFullyQualifiedName.length() > 0 ) currentFullyQualifiedName += ".";
		        currentFullyQualifiedName += star.getText();
                currentEndAST = star; // currentBeginAST already set correctly
            }
            |
            identifier:IDENT{
                if( currentFullyQualifiedName.length() > 0 ) currentFullyQualifiedName += ".";
        		currentFullyQualifiedName += identifier.getText();
                currentEndAST = identifier; // currentBeginAST already set correctly
            }
        )
    )
    ;

slist
    :
    #(
        compoundBegin:SLIST{ Vertex parentVertex = currentVertex; }
        (
            stat{
                if( currentVertex instanceof Type ) typeDefinitionFactory.attachType( ( Type )currentVertex, ( Statement )parentVertex, currentBeginAST, currentEndAST );
                else if( currentVertex instanceof Statement ) statementFactory.attachStatement( ( Statement )currentVertex, parentVertex, currentBeginAST, currentEndAST );
                else System.out.println( "rule:slist unhandled case: " + currentVertex.getClass().getName() );
                currentVertex = parentVertex; // reset to parent for next statement
            }
        )*
        ( compoundEnd:RCURLY{ setBeginAndEndAST( compoundBegin, compoundEnd ); } )?
    )
    ;

stat{ Vertex parentVertex = currentVertex; }
    :
    ctorCall
    |
    typeDefinition
    |
    variableDef
    ( varDefEnd:SEMI{ currentEndAST = varDefEnd; } )?// currentBeginAST already set correctly
    |
    expression
    expressionEnd:SEMI{ currentEndAST = expressionEnd; } // currentBeginAST already set correctly
    |
    #(
        LABELED_STAT
        labelName:IDENT
        stat{
            currentVertex = statementFactory.createLabel( ( Statement )currentVertex, labelName, currentBeginAST, currentEndAST );
            currentBeginAST = labelName; // currentEndAST already set correctly
        }
    )
    |
    #(
        ifBegin:"if"{
            If ifVertex = programGraph.createIf();
            currentVertex = ifVertex;
			Vertex parentScope = currentScope;
			currentScope = ifVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
        }
        expression{
            statementFactory.attachConditionOfIf( ( Expression )currentVertex, ifVertex, currentBeginAST, currentEndAST );
            currentVertex = ifVertex;
        }
        stat{
            statementFactory.attachThen( ( Statement )currentVertex, ifVertex, currentBeginAST, currentEndAST );
            currentVertex = ifVertex;
            // Set position informations here in case there is no "else" part
            currentBeginAST = ifBegin; // currentEndAST already set correctly
        }
        (
            elseBegin:"else" stat{
                statementFactory.attachElse( ( Statement )currentVertex, ifVertex, currentBeginAST, currentEndAST );
                currentVertex = ifVertex;
                currentBeginAST = ifBegin; // currentEndAST already set correctly
            }
        )?
        { currentScope = parentScope; }
    )
    |
    #(
        forBegin:"for"{
			For forVertex = programGraph.createFor();
			Vertex parentScope = currentScope;
			currentScope = forVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
		}
        (
            {
                TraditionalForClause forHead = programGraph.createTraditionalForClause();
                currentVertex = forHead;
                createForInitializerEdge = true;
            }
            #(
                FOR_INIT
                (
                    ( variableDef{ statementFactory.attachRunVariableInitialization( ( Statement )currentVertex, forHead, currentBeginAST, currentEndAST ); } )+
                    |
                    elist
                )?
            )
            #(
                FOR_CONDITION
                ( expression{ statementFactory.attachForCondition( ( Expression )currentVertex, forHead, currentBeginAST, currentEndAST ); } )?
            )
            #(
                FOR_ITERATOR{
                    currentVertex = forHead;
                    createForInitializerEdge = false;
                }
                ( elist )?
            )
            traditionalForHeadEnd:RPAREN{ statementFactory.attachForHead( forHead, forVertex, forBegin, traditionalForHeadEnd ); }
            |
            { ForEachClause forHead = programGraph.createForEachClause(); }
            #(
                FOR_EACH_CLAUSE
                parameterDef{ statementFactory.attachParameter( ( ParameterDeclaration )currentVertex, forHead, currentBeginAST, currentEndAST ); }
                expression{ statementFactory.attachEnumeratable( ( Expression )currentVertex, forHead, currentBeginAST, currentEndAST ); }
            )
            forEachHeadEnd:RPAREN{ statementFactory.attachForHead( forHead, forVertex, forBegin, forEachHeadEnd ); }
        )
        stat{
            statementFactory.attachLoopBody( ( Statement )currentVertex, forVertex, currentBeginAST, currentEndAST );
            currentVertex = forVertex;
            currentBeginAST = forBegin; // currentEndAST already set correctly
            currentScope = parentScope;
        }
    )
    |
    #(
        whileBegin:"while"{
            While whileVertex = programGraph.createWhile();
            currentVertex = whileVertex;
			Vertex parentScope = currentScope;
			currentScope = whileVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
        }
        expression{
            statementFactory.attachConditionOfWhile( ( Expression )currentVertex, whileVertex, currentBeginAST, currentEndAST );
            currentVertex = whileVertex;
        }
        stat{
            statementFactory.attachLoopBodyOfWhile( ( Statement )currentVertex, whileVertex, currentBeginAST, currentEndAST );
            currentVertex = whileVertex;
            currentBeginAST = whileBegin; // currentEndAST already set correctly
            currentScope = parentScope;
        }
    )
    |
    #(
        doWhileBegin:"do"{
            DoWhile doWhileVertex = programGraph.createDoWhile();
            currentVertex = doWhileVertex;
			Vertex parentScope = currentScope;
			currentScope = doWhileVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
        }
        stat{
            statementFactory.attachLoopBodyOfDoWhile( ( Statement )currentVertex, doWhileVertex, currentBeginAST, currentEndAST );
            currentVertex = doWhileVertex;
        }
        expression{
            statementFactory.attachConditionOfDoWhile( ( Expression )currentVertex, doWhileVertex, currentBeginAST, currentEndAST );
            currentVertex = doWhileVertex;
        }
        doWhileEnd:SEMI{
			setBeginAndEndAST( doWhileBegin, doWhileEnd );
			currentScope = parentScope;
		}
    )
    |
    #(
        breakBegin:"break"{ Break breakVertex = programGraph.createBreak(); }
        ( breakTarget:IDENT{ statementFactory.createLabel( breakVertex, breakTarget ); } )?
        breakEnd:SEMI{
            currentVertex = breakVertex;
            setBeginAndEndAST( breakBegin, breakEnd );
		}
    )
    |
    #(
        continueBegin:"continue"{ Continue continueVertex = programGraph.createContinue(); }
        ( continueTarget:IDENT{ statementFactory.createLabel( continueVertex, continueTarget ); } )?
        continueEnd:SEMI{
            currentVertex = continueVertex;
			setBeginAndEndAST( continueBegin, continueEnd );
		}
    )
    |
    #(
        returnBegin:"return"{
            Return returnVertex = programGraph.createReturn();
            currentVertex = returnVertex; //@TODO ist das wirklich noetig?
        }
        (
            expression{
                statementFactory.attachReturn( ( Expression )currentVertex, returnVertex, currentBeginAST, currentEndAST );
                currentVertex = returnVertex;
            }
        )?
        returnEnd:SEMI{ setBeginAndEndAST( returnBegin, returnEnd ); }
    )
    |
    #(
        switchBegin:"switch"{
            Switch switchVertex = programGraph.createSwitch();
            currentVertex = switchVertex; //@TODO ist das wirklich noetig?
			Vertex parentScope = currentScope;
			currentScope = switchVertex;
			symbolTable.addScopeInfo( currentScope, parentScope );
        }
        expression{
            statementFactory.attachSwitchArgument( ( Expression )currentVertex, switchVertex, currentBeginAST, currentEndAST );
            currentVertex = switchVertex;
        }
        ( caseGroup{ currentVertex = switchVertex; } )*
        switchEnd:RCURLY{
			setBeginAndEndAST( switchBegin, switchEnd );
			currentScope = parentScope;
		}
    )
    |
    #(
        throwBegin:"throw"{
            Throw throwVertex = programGraph.createThrow();
            currentVertex = throwVertex; //@TODO ist das wirklich noetig?
        }
        expression{
            statementFactory.attachThrownException( ( Expression )currentVertex, throwVertex, currentBeginAST, currentEndAST );
            currentVertex = throwVertex;
        }
        throwEnd:SEMI{ setBeginAndEndAST( throwBegin, throwEnd ); }
    )
    |
    #(
        synchronizedBegin:"synchronized"{
            Synchronized synchronizedVertex = programGraph.createSynchronized();
            currentVertex = synchronizedVertex; //@TODO ist das wirklich noetig? Reicht currentVertex = null nicht aus?
        }
        expression{
            statementFactory.attachMonitor( ( Expression )currentVertex, synchronizedVertex, currentBeginAST, currentEndAST );
            currentVertex = synchronizedVertex;
        }
        stat{
            statementFactory.attachSynchronizedBody( ( Block )currentVertex, synchronizedVertex, currentBeginAST, currentEndAST );
            currentVertex = synchronizedVertex;
            currentBeginAST = synchronizedBegin; // currentEndAST already set correctly
        }
    )
    |
    tryBlock
    |
    {
        Block blockVertex = programGraph.createBlock();
        currentVertex = blockVertex;
		Vertex parentScope = currentScope;
		currentScope = blockVertex;
		symbolTable.addScopeInfo( currentScope, parentScope );
    }
    slist{
		currentVertex = blockVertex;
		currentScope = parentScope;
    }
    |
    #(
        assertBegin:"assert"{
            Assert assertVertex = programGraph.createAssert();
            currentVertex = assertVertex; //@TODO ist das wirklich noetig?
        }
        expression{
            statementFactory.attachConditionOfAssert( ( Expression )currentVertex, assertVertex, currentBeginAST, currentEndAST );
            currentVertex = assertVertex;
        }
        (
            expression{
                statementFactory.attachMessage( ( Expression )currentVertex, assertVertex, currentBeginAST, currentEndAST );
                currentVertex = assertVertex;
            }
        )?
        assertEnd:SEMI{ setBeginAndEndAST( assertBegin, assertEnd ); }
    )
    |
    emptyStat:EMPTY_STAT{
        currentVertex = statementFactory.createEmptyStatement();
        setBeginAndEndAST( emptyStat );
    }
    ;

caseGroup{
    Switch parentVertex = ( Switch )currentVertex;
    Vertex currentCaseVertex = null;
    AttributedEdge lastEdgeToBeAttributed = null;
    AST beginASTOfLastCase = null;
}
    :
    #(
        CASE_GROUP
        (
            #(
                // [Case] --- [IsCaseOf] ---> Switch
                caseStatement:"case"{
                    Case caseVertex = programGraph.createCase();
                    lastEdgeToBeAttributed = programGraph.createIsCaseOf( caseVertex, parentVertex );
                    currentCaseVertex = caseVertex;
                    currentVertex = caseVertex;
                    beginASTOfLastCase = caseStatement;
                }
                expression{
                    statementFactory.attachCaseCondition( ( Expression )currentVertex, caseVertex, currentBeginAST, currentEndAST );
                    currentVertex = caseVertex;
                }
                caseEnd:COLON{ Utilities.fillEdgeAttributesFromASTDifference( lastEdgeToBeAttributed, caseStatement, caseEnd ); }
            )
            |
            (
                // [Default] --- [IsDefaultCaseOf] ---> Switch
                defaultStatement:"default"{
                    Default defaultVertex = programGraph.createDefault();
                    lastEdgeToBeAttributed = programGraph.createIsDefaultCaseOf( defaultVertex, parentVertex );
                    currentCaseVertex = defaultVertex;
                    currentVertex = defaultVertex;
                    beginASTOfLastCase = defaultStatement;
                }
                defaultEnd:COLON{ Utilities.fillEdgeAttributesFromASTDifference( lastEdgeToBeAttributed, defaultStatement, defaultEnd ); }
            )
        )+
        slist{ // We don't need to do anything here, as the edges are created within the slist rule
            currentVertex = parentVertex;
            // Set the right edge attributes for the last case (as this is the one the statements were attached to...)
            Utilities.fillEdgeAttributesFromASTDifference( lastEdgeToBeAttributed, beginASTOfLastCase, currentEndAST );
        }
    )
    ;

tryBlock
    :
    #(
        tryBegin:"try"{
            Block blockVertexOfTry = programGraph.createBlock();
            currentVertex = blockVertexOfTry;
			Vertex parentScope = currentScope;
			currentScope = blockVertexOfTry;
			symbolTable.addScopeInfo( currentScope, parentScope );
        }
        slist{
            Try tryVertex = statementFactory.createTry( blockVertexOfTry, currentBeginAST, currentEndAST );
            currentVertex = tryVertex;
            currentScope = parentScope;
        }
        (
            handler{
                statementFactory.attachHandler( ( Catch )currentVertex, tryVertex, currentBeginAST, currentEndAST );
                currentVertex = tryVertex;
            }
        )*
        (
            #(
                "finally"{
                    Block blockVertexOfFinally = programGraph.createBlock();
                    currentVertex = blockVertexOfFinally;
			        currentScope = blockVertexOfFinally;
			        symbolTable.addScopeInfo( currentScope, parentScope );
                }
                slist{
                    statementFactory.attachFinally( blockVertexOfFinally, tryVertex, currentBeginAST, currentEndAST );
                    currentVertex = tryVertex;
                    currentScope = parentScope;
                }
            )
        )?
        { currentBeginAST = tryBegin;	}// currentEndAST already set correctly.
    )
    ;

handler
    :
    #(
        catchBegin:"catch"{
            Catch catchVertex = programGraph.createCatch();
            currentVertex = catchVertex;
            Vertex parentScope = currentScope;
            currentScope = catchVertex;
            symbolTable.addScopeInfo( currentScope, parentScope );
        }
        parameterDef{
            statementFactory.attachCaughtException( ( ParameterDeclaration )currentVertex, catchVertex, currentBeginAST, currentEndAST );
            currentVertex = catchVertex;
            Block blockVertexOfCatch = programGraph.createBlock();
            currentVertex = blockVertexOfCatch;
        }
        slist{
            statementFactory.attachBodyOfCatch( blockVertexOfCatch, catchVertex, currentBeginAST, currentEndAST );
            currentVertex = catchVertex;
            currentBeginAST = catchBegin; // currentEndAST already set correctly
            currentScope = parentScope;
        }
    )
    ;

elist
    :
    #(
        ELIST{
            Vertex parentVertex = currentVertex;
            //has to be set to null because arguments of a method invocation will not be recognized as such, but as name of invocated method
            currentVertex = null;
            setBeginAndEndAST( null );
            AST firstExpressionASTOfList = null;
        }
        (
            expression{
                if( ( parentVertex instanceof TraditionalForClause ) && ( createForInitializerEdge ) )
                    statementFactory.attachRunVariableInitialization( ( Expression )currentVertex, ( TraditionalForClause )parentVertex, currentBeginAST, currentEndAST );
                else if( ( parentVertex instanceof TraditionalForClause ) && ( !createForInitializerEdge ) )
                    statementFactory.attachIterator( ( Expression )currentVertex, ( TraditionalForClause )parentVertex, currentBeginAST, currentEndAST );
                else if( parentVertex instanceof EnumConstant )
                    memberFactory.attachArgumentOfEnumConstant( ( Expression )currentVertex, ( EnumConstant )parentVertex, currentBeginAST, currentEndAST );
                else if( parentVertex instanceof MethodInvocation )
                    expressionFactory.attachArgumentOfMethodInvocation( ( Expression )currentVertex, ( MethodInvocation )parentVertex, currentBeginAST, currentEndAST );
                if( firstExpressionASTOfList == null ) firstExpressionASTOfList = currentBeginAST; // Remember first element
            }
        )*
    {
        currentVertex = parentVertex;
        currentBeginAST = firstExpressionASTOfList; // currentEndAST already set correctly
    }
    )
    ;

expression : #(EXPR currentExpr:expr ) ; // nothing needs to be done here, everything relevant already is set and simply gets passed through

expr
    :
    (
        conditionalExpr

    |   #(ASSIGN expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.ASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(PLUS_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.PLUSASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(MINUS_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.MINUSASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(STAR_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.MULTIPLICATIONASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(DIV_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.DIVISIONASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(MOD_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.MODULOASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(SR_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.RIGHTSHIFTASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BSR_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.UNSIGNEDRIGHTSHIFTASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(SL_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.LEFTSHIFTASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BAND_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.ANDASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BXOR_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.XORASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BOR_ASSIGN
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.ORASSIGNMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    )
    ;

conditionalExpr
    :
    #(
        QUESTION
        expr{
            ConditionalExpression conditionalExpressionVertex = expressionFactory.createConditionalExpression( ( Expression )currentVertex, currentBeginAST, currentEndAST );
            AST expressionBeginAST = currentBeginAST;
        }
        expr{ expressionFactory.attachMatch( ( Expression )currentVertex, conditionalExpressionVertex, currentBeginAST, currentEndAST ); }
        expr{
            expressionFactory.attachMismatch( ( Expression )currentVertex, conditionalExpressionVertex, currentBeginAST, currentEndAST );
            currentVertex = conditionalExpressionVertex;
            currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
        }
    )
    |   #(LOR
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.SHORTCIRCUITOR, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(LAND
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.SHORTCIRCUITAND, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BOR
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.OR, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BXOR
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.XOR, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BAND
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.AND, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(NOT_EQUAL
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.UNEQUALS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(EQUAL
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.EQUALS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(LT
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.LESS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(GT
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.GREATER, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(LE
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.LESSEQUALS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(GE
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.GREATEREQUALS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(SL
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.LEFTSHIFT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(SR
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.RIGHTSHIFT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(BSR
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.UNSIGNEDRIGHTSHIFT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(PLUS
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.PLUS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST;  // currentEndAST already set correctly
            }
        )
    |   #(MINUS
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.MINUS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(DIV
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.DIVISION, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(MOD
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.MODULO, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(STAR
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.MULTIPLICATION, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(preEncrement:INC
            expr{
				currentVertex = expressionFactory.createPrefixExpression( PrefixOperators.INCREMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentBeginAST = preEncrement; // currentEndAST already set correctly
            }
        )
    |   #(preDecrement:DEC
            expr{
				currentVertex = expressionFactory.createPrefixExpression( PrefixOperators.DECREMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentBeginAST = preDecrement; // currentEndAST already set correctly
            }
        )
    |   #(postIncrement:POST_INC
            expr{
				currentVertex = expressionFactory.createPostfixExpression( PostfixOperators.INCREMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentEndAST = postIncrement; // currentBeginAST is already set correctly, so we don't need to do anything here
            }
        )
    |   #(postDecrement:POST_DEC
            expr{
				currentVertex = expressionFactory.createPostfixExpression( PostfixOperators.DECREMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentEndAST = postDecrement; // currentBeginAST is already set correctly, so we don't need to do anything here
            }
        )
    |   #(bitwiseComplement:BNOT
            expr{
				currentVertex = expressionFactory.createPrefixExpression( PrefixOperators.BITWISECOMPLEMENT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentBeginAST = bitwiseComplement; // currentEndAST already set correctly
            }
        )
    |   #(booleanNot:LNOT
            expr{
				currentVertex = expressionFactory.createPrefixExpression( PrefixOperators.NOT, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentBeginAST = booleanNot; // currentEndAST already set correctly
            }
        )
    |   #("instanceof"
            expr{
                InfixExpression infixExpressionVertex = expressionFactory.createInfixExpression( InfixOperators.INSTANCEOF, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                AST expressionBeginAST = currentBeginAST;
            }
            expr{
                expressionFactory.attachRightHandSide( infixExpressionVertex, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentVertex = infixExpressionVertex;
                currentBeginAST = expressionBeginAST; // currentEndAST already set correctly
            }
        )
    |   #(negative:UNARY_MINUS
            expr{
				currentVertex = expressionFactory.createPrefixExpression( PrefixOperators.MINUS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentBeginAST = negative; // currentEndAST already set correctly
            }
        )
    |   #(positive:UNARY_PLUS
            expr{
				currentVertex = expressionFactory.createPrefixExpression( PrefixOperators.PLUS, ( Expression )currentVertex, currentBeginAST, currentEndAST );
                currentBeginAST = positive; // currentEndAST already set correctly
            }
        )
    |   primaryExpression
    ;

primaryExpression{ Vertex parentVertex = currentVertex; }
    :
    (
        ident:IDENT{
            if( parentVertex instanceof MethodInvocation ) identifierFactory.createIdentifier( ( MethodInvocation )parentVertex, ident );
            else{
				FieldAccess fieldAccessVertex = fieldFactory.createFieldAccess( ident, currentScope ); //ehemals identifierFactory.createIdentifier( fieldAccessVertex, ident );
				currentVertex = fieldAccessVertex;
			}
            setBeginAndEndAST( ident );

        }
        |
        #(
            DOT{ currentVertex = null; }
            (
                expr
                (
                    fieldName:IDENT{
                        if( parentVertex instanceof MethodInvocation &&
                        	((( MethodInvocation )parentVertex).getFirstIsNameOfInvokedMethodIncidence() == null) //2009-03-17 quick fix by ultbreit: added check for edge existence
                          ){
                            identifierFactory.createIdentifier( ( MethodInvocation )parentVertex, ( Expression )currentVertex, fieldName, currentBeginAST, currentEndAST );
                            currentVertex = parentVertex;
                        }
                        else{
							FieldAccess fieldAccessVertex = fieldFactory.createFieldAccess( ( Expression )currentVertex, fieldName, currentBeginAST, currentEndAST, currentScope );
							currentVertex = fieldAccessVertex;
						}
                        currentEndAST = fieldName;
                    }
                    |
                    arrayIndex
                    |
                    accessToOuterClass:"this"{
                        if( parentVertex instanceof MethodInvocation ){
                            identifierFactory.createIdentifier( ( MethodInvocation )parentVertex, ( Expression )currentVertex, accessToOuterClass, currentBeginAST, currentEndAST );
                            currentVertex = parentVertex;
                        }
                        else currentVertex = fieldFactory.createFieldAccess( ( Expression )currentVertex, accessToOuterClass, currentBeginAST, currentEndAST, currentScope );
                        currentEndAST = accessToOuterClass;
                    }
                    |
                    accessToMetaClass:"class"{
                    	if( currentVertex instanceof FieldAccess ){
							ArrayList<FieldAccess> verticesToDelete = new ArrayList<FieldAccess>();
                    		FieldAccess fieldAccessVertex = (FieldAccess)currentVertex;
                    		verticesToDelete.add(fieldAccessVertex);
                    		String name = ResolverUtilities.getNameOfAccessedField(fieldAccessVertex);
                    		this.symbolTable.removeFieldAccess(fieldAccessVertex);
                    		IsFieldContainerOf isFieldContainerOfEdge = fieldAccessVertex.getFirstIsFieldContainerOfIncidence(EdgeDirection.IN);

                    		while(isFieldContainerOfEdge != null){
								FieldAccess containerVertex = (FieldAccess)isFieldContainerOfEdge.getAlpha();
								name = ResolverUtilities.getNameOfAccessedField(containerVertex) + "." + name;
								this.symbolTable.removeFieldAccess(containerVertex);
								isFieldContainerOfEdge = containerVertex.getFirstIsFieldContainerOfIncidence(EdgeDirection.IN);
								verticesToDelete.add(containerVertex);
							}

							for(int i = 0; i < verticesToDelete.size(); i++){
								ResolverUtilities.deleteWithIdentifier(verticesToDelete.get(i));
								verticesToDelete.remove(i);
								i--;
							}

                    		ClassLiteral classLiteralVertex = this.programGraph.createClassLiteral();
                    		QualifiedType qualifiedTypeVertex = this.programGraph.createQualifiedType();
                    		qualifiedTypeVertex.set_fullyQualifiedName(name);
                    		this.symbolTable.addUnresolvedTypeSpecification(currentScope, qualifiedTypeVertex);
                    		IsSpecifiedTypeOf edge = this.programGraph.createIsSpecifiedTypeOf(qualifiedTypeVertex, classLiteralVertex);
                    		Utilities.fillEdgeAttributesFromASTDifference(edge,	currentBeginAST, currentEndAST);
							currentVertex = classLiteralVertex;
                    	}
                    	/*
                        if( parentVertex instanceof MethodInvocation ){
							// quick fix 2009-03-05 by ultbreit: replaced accesToMetaClass by currentVertex on cast to Expression and fieldName with accessToMetaClass in next line as it seems to be consistent with all other productions
                            identifierFactory.createIdentifier( ( MethodInvocation )parentVertex, ( Expression )currentVertex, accessToMetaClass, currentBeginAST, currentEndAST );
                            currentVertex = parentVertex;
                        }
                        else
                        	currentVertex = fieldFactory.createFieldAccess( ( Expression )currentVertex, accessToMetaClass, currentBeginAST, currentEndAST, currentScope );
                        */
                        currentEndAST = accessToMetaClass;
                    }
                    |
                    newExpression
                    |
                    accessToSuperClass:"super"{
                        if (parentVertex instanceof MethodInvocation){
                            identifierFactory.createIdentifier( ( MethodInvocation )parentVertex, ( Expression )currentVertex, accessToSuperClass, currentBeginAST, currentEndAST );
                            currentVertex = parentVertex;
                        }
                        else currentVertex = fieldFactory.createFieldAccess( ( Expression )currentVertex, accessToSuperClass, currentBeginAST, currentEndAST, currentScope );
                        currentEndAST = accessToSuperClass;
                    }
                    |
                    (
                        { System.out.println( "rule: primaryExpression unhandled case: " + currentVertex.getClass().getName() ); }
                        typeArguments // for generic methods calls
                    )?
                )
                |
                #(
                    ARRAY_DECLARATOR
                    typeSpecArray
                    RBRACK
                )
                |
                builtInType
                ( accessToMetaClass2:"class" )?{
					ClassLiteral classLiteralVertex = this.programGraph.createClassLiteral();
					IsSpecifiedTypeOf edge = this.programGraph.createIsSpecifiedTypeOf((BuiltInType)currentVertex, classLiteralVertex);
					Utilities.fillEdgeAttributesFromASTDifference(edge,	currentBeginAST, currentEndAST);
					currentEndAST = accessToMetaClass2;
					currentVertex = classLiteralVertex;
				}
            )
        )
        |
        arrayIndex
        |
        #(
            METHOD_CALL{
                MethodInvocation methodInvocationVertex = expressionFactory.createMethodInvocation( MethodInvocationTypes.METHOD );
                currentVertex = methodInvocationVertex;
                AST methodCallBeginAST = null;
            }
            primaryExpression{
		    	currentVertex = methodInvocationVertex;
		    	methodCallBeginAST = currentBeginAST;
		    }
            ( typeArguments )?{ currentVertex = methodInvocationVertex; }
            elist{ currentVertex = methodInvocationVertex; }
            methodCallEnd:RPAREN{
            	setBeginAndEndAST( methodCallBeginAST, methodCallEnd );
            	symbolTable.addMethodInvocation( methodInvocationVertex, currentScope );
            }
        )
        |
        ctorCall
        |
        #(
            castBegin:TYPECAST{
                Vertex castExpressionVertex;
                AST castTypeBeginAST = null;
                AST castTypeEndAST = null;
            }
            typeSpec{
                //let's save the type & its position informations for later
                castExpressionVertex = currentVertex;
                castTypeBeginAST = currentBeginAST;
                castTypeEndAST = currentEndAST;
            }
            expr{
                if( castExpressionVertex instanceof BuiltInType ){
                    BuiltInCast builtInCastVertex = expressionFactory.createBuiltInCast( ( BuiltInType )castExpressionVertex, castTypeBeginAST, castTypeEndAST );
                    expressionFactory.attachCastedValue( ( Expression )currentVertex, builtInCastVertex, currentBeginAST, currentEndAST );
                    currentVertex = builtInCastVertex;
                }
                else if( castExpressionVertex instanceof TypeSpecification ){
                    ClassCast classCastVertex = expressionFactory.createClassCast( ( TypeSpecification )castExpressionVertex, castTypeBeginAST, castTypeEndAST );
                    expressionFactory.attachCastedObject( ( Expression )currentVertex, classCastVertex, currentBeginAST, currentEndAST );
                    currentVertex = classCastVertex;
                }
                currentBeginAST = castBegin; // currentEndAST already set correctly.
            }
        )
        |
        newExpression
        |
        constant{
// added on 2009-03-03 as quick fix by abaldauf
            if( parentVertex instanceof VariableInitializer )
                expressionFactory.attachVariableInitializer( ( Expression )currentVertex, ( VariableInitializer )parentVertex, currentBeginAST, currentEndAST );
        }
        |
        superClass:"super"{
            currentVertex = fieldFactory.createFieldAccess( superClass, currentScope );
            setBeginAndEndAST( superClass );
        }
        |
        booleanValueTrue:"true"{
            currentVertex = constantFactory.createBooleanTrueConstant();
            setBeginAndEndAST( booleanValueTrue );
        }
        |
        booleanValueFalse:"false"{
            currentVertex = constantFactory.createBooleanFalseConstant();
            setBeginAndEndAST( booleanValueFalse );
        }
        |
        thisClass:"this"{
            currentVertex = fieldFactory.createFieldAccess( thisClass, currentScope );
            setBeginAndEndAST( thisClass );
        }
        |
        nullValue:"null"{
            currentVertex = expressionFactory.createNull();
            setBeginAndEndAST( nullValue );
        }
        |
        typeSpec // type name used as right hand side of an "instanceof" expression
    )
    ;

ctorCall{ MethodInvocation methodInvocationVertex = null; }
    :
    #(
        ccall:CTOR_CALL{
            methodInvocationVertex = expressionFactory.createMethodInvocation( MethodInvocationTypes.CONSTRUCTOR );
            identifierFactory.createIdentifier( methodInvocationVertex, ccall );
            currentVertex = methodInvocationVertex;
        }
        elist{ currentVertex = methodInvocationVertex; }
        ccallEnd:RPAREN{
        	setBeginAndEndAST( ccall, ccallEnd );
        	symbolTable.addMethodInvocation( methodInvocationVertex, currentScope );
        }
        ( sem1:SEMI{ currentEndAST = sem1; } )?
    )
    |
    #(
        sccall:SUPER_CTOR_CALL{
            methodInvocationVertex = expressionFactory.createMethodInvocation( MethodInvocationTypes.SUPERCONSTRUCTOR );
            identifierFactory.createIdentifier( methodInvocationVertex, sccall );
            currentVertex = methodInvocationVertex;
        }
        (
            elist{ currentVertex = methodInvocationVertex; }
            |
            primaryExpression{ currentVertex = methodInvocationVertex; }
            elist{ currentVertex = methodInvocationVertex; }
        )
        sccallEnd:RPAREN{
        	setBeginAndEndAST( sccall, sccallEnd );
        	symbolTable.addMethodInvocation( methodInvocationVertex, currentScope );
        }
        ( sem2:SEMI{ currentEndAST = sem2; } )?
    )
    ;

arrayIndex{
    Vertex parentVertex = currentVertex;
    FieldAccess fieldAccessVertex = null;
    AST arrayAccessBegin = null;
}
    :
    #(
        INDEX_OP
        expr{
			if( currentVertex instanceof FieldAccess )
            	fieldAccessVertex = ( FieldAccess )currentVertex; // save this vertex for later to connect it to the corresponding InfixExpression
            //@TODO else MethodInvocation
            arrayAccessBegin = currentBeginAST;
        }
        expression{
			if( fieldAccessVertex != null )
				expressionFactory.attachArrayElementIndex( ( Expression )currentVertex, fieldAccessVertex, currentBeginAST, currentEndAST );
		}
        arrayAccessEnd:RBRACK
    )
    {
        if( fieldAccessVertex != null ) currentVertex = fieldAccessVertex;
        setBeginAndEndAST( arrayAccessBegin, arrayAccessEnd );
    }
    ;

constant
    :
    integerConstant:NUM_INT{
        currentVertex = constantFactory.createIntegerConstant( integerConstant );
        setBeginAndEndAST( integerConstant );
    }
    |
    charConstant:CHAR_LITERAL{
        currentVertex = constantFactory.createCharConstant( charConstant );
        setBeginAndEndAST( charConstant );
    }
    |
    stringConstant:STRING_LITERAL{
        currentVertex = constantFactory.createStringConstant( stringConstant );
        setBeginAndEndAST( stringConstant );
    }
    |
    floatConstant:NUM_FLOAT{
        currentVertex = constantFactory.createFloatConstant( floatConstant );
        setBeginAndEndAST( floatConstant );
    }
    |
    doubleConstant:NUM_DOUBLE{
        currentVertex = constantFactory.createDoubleConstant( doubleConstant );
        setBeginAndEndAST( doubleConstant );
    }
    |
    longConstant:NUM_LONG{
        currentVertex = constantFactory.createLongConstant( longConstant );
        setBeginAndEndAST( longConstant );
    }
    ;

newExpression{
    QualifiedName qualifiedNameVertex = null;
    TypeSpecification typeSpecificationVertex = null;
    AST typeBeginAST = null;
    AST typeEndAST = null;
}
    :
    #(
        newObject:"new"
        ( typeArguments )?
        typeName:type{
            // check what has been created
            if( currentVertex instanceof QualifiedName ) qualifiedNameVertex = ( QualifiedName )currentVertex; //save it for later
            else typeSpecificationVertex = ( TypeSpecification )currentVertex;
            typeBeginAST = currentBeginAST;
            typeEndAST = currentEndAST;
        }
        (
            {
                ArrayCreation arrayCreationVertex = programGraph.createArrayCreation();
                currentVertex = arrayCreationVertex;
            }
            newArrayDeclarator{ //now we know that this is an array initializer
            // @TODO: Refine, as the following check is quick and dirty and just prevents the extractor from crashing...
                if( typeSpecificationVertex != null)
                    expressionFactory.attachElementType( typeSpecificationVertex, arrayCreationVertex, typeBeginAST, typeEndAST );
                else
                    expressionFactory.attachElementType( qualifiedNameVertex, arrayCreationVertex, typeBeginAST, typeEndAST );
                currentVertex = arrayCreationVertex;
            }
            ( arrayInitializer )?
            |
            {
                MethodInvocation methodInvocationVertex = programGraph.createMethodInvocation();
                currentVertex = methodInvocationVertex;
            }
            elist
            ccallEnd:RPAREN{ //now we know that this is a method call
                ObjectCreation objectCreationVertex = expressionFactory.createObjectCreation( typeName, methodInvocationVertex, typeBeginAST, ccallEnd );
                if( typeSpecificationVertex != null )
                    expressionFactory.attachObjectCreationType( objectCreationVertex, typeSpecificationVertex, typeBeginAST, typeEndAST );
                else
                    expressionFactory.attachObjectCreationType( objectCreationVertex, qualifiedNameVertex, typeBeginAST, typeEndAST );
                IsNameOf isNameOfEdge = qualifiedNameVertex.getFirstIsNameOfIncidence();
                //@TODO this should be a qualified name not an identifier
                if( isNameOfEdge != null ) {
                    Identifier ident = ( Identifier )isNameOfEdge.getAlpha();
                	expressionFactory.attachNameOfInvokedMethod( ident, methodInvocationVertex, typeEndAST, typeEndAST );
                }
                currentVertex = objectCreationVertex;
                currentEndAST = ccallEnd;
                symbolTable.addMethodInvocation( methodInvocationVertex, currentScope );
            }
            ( objBlock{ currentVertex = objectCreationVertex; } )?
        )
        { currentBeginAST = newObject; } // currentEndAST already set correctly
    )
    ;

newArrayDeclarator{ Vertex parentVertex = currentVertex; }
    :
    #(
        arrayBegin:ARRAY_DECLARATOR
        ( newArrayDeclarator )?
        ( expression{ expressionFactory.createArrayInitializer( ( Expression )currentVertex, ( ArrayCreation )parentVertex, currentBeginAST, currentEndAST ); } )?
        arrayEnd:RBRACK{ setBeginAndEndAST( arrayBegin, arrayEnd ); }
    )
    ;
