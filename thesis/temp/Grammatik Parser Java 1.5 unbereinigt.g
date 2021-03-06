class JavaRecognizer extends Parser;

(�)compilationUnit
	:	// A compilation unit starts with an optional package definition
		(	(annotations "package")=> packageDefinition
		|	/* nothing */
		)

		// Next we have a series of zero or more import statements
		( importDefinition )*

		// Wrapping things up with any number of class or interface
		// definitions
		( typeDefinition )*

		EOF!
	;


// Package statement: optional annotations followed by "package" then the package identifier.
(�)packageDefinition
	options {defaultErrorHandler = true;} // let ANTLR handle errors
	:	annotations p:"package"^ {#p.setType(PACKAGE_DEF);} identifier SEMI!
	;


// Import statement: import followed by a package or class name
(�)importDefinition
	{ boolean isStatic = false; }
	:	i:"import"^ {#i.setType(IMPORT);} ( "static"! {#i.setType(STATIC_IMPORT);} )? identifierStar SEMI!
	;

// A type definition is either a class, interface, enum or annotation with possible additional semis.
(�)typeDefinition
	:	m:modifiers!
		typeDefinitionInternal[#m]
	|	SEMI!
	;

// Protected type definitions production for reuse in other productions
(�)protected typeDefinitionInternal[AST mods]
	:	classDefinition[#mods]		// inner class
	|	interfaceDefinition[#mods]	// inner interface
	|	enumDefinition[#mods]		// inner enum
	|	annotationDefinition[#mods]	// inner annotation
	;

// A declaration is the creation of a reference or primitive-type variable
// Create a separate Type/Var tree for each var in the var list.
(�)declaration!
	:	m:modifiers t:typeSpec[false] v:variableDefinitions[#m,#t]
		{#declaration = #v;}
	;

// A type specification is a type name with possible brackets afterwards
// (which would make it an array type).
(�)typeSpec[boolean addImagNode]
	:	classTypeSpec[addImagNode]
	|	builtInTypeSpec[addImagNode]
	;

// A class type specification is a class type with either:
// - possible brackets afterwards
//   (which would make it an array type).
// - generic type arguments after
(�)classTypeSpec[boolean addImagNode]
	:	classOrInterfaceType[false]
		(options{greedy=true;}: // match as many as possible
			lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);} RBRACK!
		)*
		{
			if ( addImagNode ) {
				#classTypeSpec = #(#[TYPE,"TYPE"], #classTypeSpec);
			}
		}
	;

// A non-built in type name, with possible type parameters
(�)classOrInterfaceType[boolean addImagNode]
	:	IDENT (typeArguments)?
		(options{greedy=true;}: // match as many as possible
			DOT^
			IDENT (typeArguments)?
		)*
		{
			if ( addImagNode ) {
				#classOrInterfaceType = #(#[TYPE,"TYPE"], #classOrInterfaceType);
			}
		}
	;

// A specialised form of typeSpec where built in types must be arrays
(�)typeArgumentSpec
	:	classTypeSpec[true]
	|	builtInTypeArraySpec[true]
	;

// A generic type argument is a class type, a possibly bounded wildcard type or a built-in type array
(�)typeArgument
	:	(	typeArgumentSpec
		|	wildcardType
		)
		{#typeArgument = #(#[TYPE_ARGUMENT,"TYPE_ARGUMENT"], #typeArgument);}
	;

// Wildcard type indicating all types (with possible constraint)
(�)wildcardType
	:	q:QUESTION^ {#q.setType(WILDCARD_TYPE);}
		(("extends" | "super")=> typeArgumentBounds)?
	;

// Type arguments to a class or interface type
(�)typeArguments
{int currentLtLevel = 0;}
	:
		{currentLtLevel = ltCounter;}
		LT! {ltCounter++;}
		typeArgument
		(options{greedy=true;}: // match as many as possible
			{inputState.guessing !=0 || ltCounter == currentLtLevel + 1}?
			COMMA! typeArgument
		)*

		(	// turn warning off since Antlr generates the right code,
			// plus we have our semantic predicate below
			options{generateAmbigWarnings=false;}:
			typeArgumentsOrParametersEnd
		)?

		// make sure we have gobbled up enough '>' characters
		// if we are at the "top level" of nested typeArgument productions
		{(currentLtLevel != 0) || ltCounter == currentLtLevel}?

		{#typeArguments = #(#[TYPE_ARGUMENTS, "TYPE_ARGUMENTS"], #typeArguments);}
	;

// this gobbles up *some* amount of '>' characters, and counts how many
// it gobbled.
(�)protected typeArgumentsOrParametersEnd
	:	GT! {ltCounter-=1;}
	|	SR! {ltCounter-=2;}
	|	BSR! {ltCounter-=3;}
	;

// Restriction on wildcard types based on super class or derrived class
(�)typeArgumentBounds
	{boolean isUpperBounds = false;}
	:
		( "extends"! {isUpperBounds=true;} | "super"! ) classOrInterfaceType[false]
		{
			if (isUpperBounds)
			{
				#typeArgumentBounds = #(#[TYPE_UPPER_BOUNDS,"TYPE_UPPER_BOUNDS"], #typeArgumentBounds);
			}
			else
			{
				#typeArgumentBounds = #(#[TYPE_LOWER_BOUNDS,"TYPE_LOWER_BOUNDS"], #typeArgumentBounds);
			}
		}
	;

// A builtin type array specification is a builtin type with brackets afterwards
(�)builtInTypeArraySpec[boolean addImagNode]
	:	builtInType
		(options{greedy=true;}: // match as many as possible
			lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);} RBRACK!
		)+

		{
			if ( addImagNode ) {
				#builtInTypeArraySpec = #(#[TYPE,"TYPE"], #builtInTypeArraySpec);
			}
		}
	;

// A builtin type specification is a builtin type with possible brackets
// afterwards (which would make it an array type).
(�)builtInTypeSpec[boolean addImagNode]
	:	builtInType
		(options{greedy=true;}: // match as many as possible
			lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);} RBRACK!
		)*
		{
			if ( addImagNode ) {
				#builtInTypeSpec = #(#[TYPE,"TYPE"], #builtInTypeSpec);
			}
		}
	;

// A type name. which is either a (possibly qualified and parameterized)
// class name or a primitive (builtin) type
(�)type
	:	classOrInterfaceType[false]
	|	builtInType
	;

// The primitive types.
(�)builtInType
	:	"void"
	|	"boolean"
	|	"byte"
	|	"char"
	|	"short"
	|	"int"
	|	"float"
	|	"long"
	|	"double"
	;

// A (possibly-qualified) java identifier. We start with the first IDENT
// and expand its name by adding dots and following IDENTS
(�)identifier
	:	IDENT ( DOT^ IDENT )*
	;

(�)identifierStar
	:	IDENT
		( DOT^ IDENT )*
		( DOT^ STAR )?
	;

// A list of zero or more modifiers. We could have used (modifier)* in
// place of a call to modifiers, but I thought it was a good idea to keep
// this rule separate so they can easily be collected in a Vector if
// someone so desires
(�)modifiers
	:
		(
			//hush warnings since the semantic check for "@interface" solves the non-determinism
			options{generateAmbigWarnings=false;}:

			modifier
			|
			//Semantic check that we aren't matching @interface as this is not an annotation
			//A nicer way to do this would be nice
			{LA(1)==AT && !LT(2).getText().equals("interface")}? annotation
		)*

		{#modifiers = #([MODIFIERS, "MODIFIERS"], #modifiers);}
	;

// modifiers for Java classes, interfaces, class/instance vars and methods
(�)modifier
	:	"private"
	|	"public"
	|	"protected"
	|	"static"
	|	"transient"
	|	"final"
	|	"abstract"
	|	"native"
	|	"threadsafe"
	|	"synchronized"
	|	"volatile"
	|	"strictfp"
	;

(�)annotation!
	:	AT! i:identifier ( LPAREN! ( args:annotationArguments )? RPAREN! )?
		{#annotation = #(#[ANNOTATION,"ANNOTATION"], i, args);}
	;

(�)annotations
    :   (annotation)*
		{#annotations = #([ANNOTATIONS, "ANNOTATIONS"], #annotations);}
    ;

(�)annotationArguments
	:	annotationMemberValueInitializer | anntotationMemberValuePairs
	;

(�)anntotationMemberValuePairs
	:	annotationMemberValuePair ( COMMA! annotationMemberValuePair )*
	;

(�)annotationMemberValuePair!
	:	i:IDENT ASSIGN! v:annotationMemberValueInitializer
		{#annotationMemberValuePair = #(#[ANNOTATION_MEMBER_VALUE_PAIR,"ANNOTATION_MEMBER_VALUE_PAIR"], i, v);}
	;

(�)annotationMemberValueInitializer
	:
		conditionalExpression | annotation | annotationMemberArrayInitializer
	;

// This is an initializer used to set up an annotation member array.
(�)annotationMemberArrayInitializer
	:	lc:LCURLY^ {#lc.setType(ANNOTATION_ARRAY_INIT);}
			(	annotationMemberArrayValueInitializer
				(
					// CONFLICT: does a COMMA after an initializer start a new
					// initializer or start the option ',' at end?
					// ANTLR generates proper code by matching
					// the comma as soon as possible.
					options {
						warnWhenFollowAmbig = false;
					}
				:
					COMMA! annotationMemberArrayValueInitializer
				)*
				(COMMA!)?
			)?
		RCURLY!
	;

// The two things that can initialize an annotation array element are a conditional expression
// and an annotation (nested annotation array initialisers are not valid)
(�)annotationMemberArrayValueInitializer
	:	conditionalExpression
	|	annotation
	;

(�)superClassClause!
	:	( "extends" c:classOrInterfaceType[false] )?
		{#superClassClause = #(#[EXTENDS_CLAUSE,"EXTENDS_CLAUSE"],c);}
	;

// Definition of a Java class
(�)classDefinition![AST modifiers]
	:	"class" IDENT
		// it _might_ have type paramaters
		(tp:typeParameters)?
		// it _might_ have a superclass...
		sc:superClassClause
		// it might implement some interfaces...
		ic:implementsClause
		// now parse the body of the class
		cb:classBlock
		{#classDefinition = #(#[CLASS_DEF,"CLASS_DEF"],
								modifiers,IDENT,tp,sc,ic,cb);}
	;

// Definition of a Java Interface
(�)interfaceDefinition![AST modifiers]
	:	"interface" IDENT
		// it _might_ have type paramaters
		(tp:typeParameters)?
		// it might extend some other interfaces
		ie:interfaceExtends
		// now parse the body of the interface (looks like a class...)
		ib:interfaceBlock
		{#interfaceDefinition = #(#[INTERFACE_DEF,"INTERFACE_DEF"],
									modifiers,IDENT,tp,ie,ib);}
	;

(�)enumDefinition![AST modifiers]
	:	"enum" IDENT
		// it might implement some interfaces...
		ic:implementsClause
		// now parse the body of the enum
		eb:enumBlock
		{#enumDefinition = #(#[ENUM_DEF,"ENUM_DEF"],
								modifiers,IDENT,ic,eb);}
	;

(�)annotationDefinition![AST modifiers]
	:	AT "interface" IDENT
		// now parse the body of the annotation
		ab:annotationBlock
		{#annotationDefinition = #(#[ANNOTATION_DEF,"ANNOTATION_DEF"],
									modifiers,IDENT,ab);}
	;

(�)typeParameters
{int currentLtLevel = 0;}
	:
		{currentLtLevel = ltCounter;}
		LT! {ltCounter++;}
		typeParameter (COMMA! typeParameter)*
		(typeArgumentsOrParametersEnd)?

		// make sure we have gobbled up enough '>' characters
		// if we are at the "top level" of nested typeArgument productions
		{(currentLtLevel != 0) || ltCounter == currentLtLevel}?

		{#typeParameters = #(#[TYPE_PARAMETERS, "TYPE_PARAMETERS"], #typeParameters);}
	;

(�)typeParameter
	:
		// I'm pretty sure Antlr generates the right thing here:
		(id:IDENT) ( options{generateAmbigWarnings=false;}: typeParameterBounds )?
		{#typeParameter = #(#[TYPE_PARAMETER,"TYPE_PARAMETER"], #typeParameter);}
	;

(�)typeParameterBounds
	:
		"extends"! classOrInterfaceType[false]
		(BAND! classOrInterfaceType[false])*
		{#typeParameterBounds = #(#[TYPE_UPPER_BOUNDS,"TYPE_UPPER_BOUNDS"], #typeParameterBounds);}
	;

// This is the body of a class. You can have classFields and extra semicolons.
(�)classBlock
	:	LCURLY!
			( classField | SEMI! )*
		RCURLY!
		{#classBlock = #([OBJBLOCK, "OBJBLOCK"], #classBlock);}
	;

// This is the body of an interface. You can have interfaceField and extra semicolons.
(�)interfaceBlock
	:	LCURLY!
			( interfaceField | SEMI! )*
		RCURLY!
		{#interfaceBlock = #([OBJBLOCK, "OBJBLOCK"], #interfaceBlock);}
	;
	
// This is the body of an annotation. You can have annotation fields and extra semicolons,
// That's about it (until you see what an annoation field is...)
(�)annotationBlock
	:	LCURLY!
		( annotationField | SEMI! )*
		RCURLY!
		{#annotationBlock = #([OBJBLOCK, "OBJBLOCK"], #annotationBlock);}
	;

// This is the body of an enum. You can have zero or more enum constants
// followed by any number of fields like a regular class
(�)enumBlock
	:	LCURLY!
			( enumConstant ( options{greedy=true;}: COMMA! enumConstant )* ( COMMA! )? )?
			( SEMI! ( classField | SEMI! )* )?
		RCURLY!
		{#enumBlock = #([OBJBLOCK, "OBJBLOCK"], #enumBlock);}
	;

// An annotation field
(�)annotationField!
	:	mods:modifiers
		(	td:typeDefinitionInternal[#mods]
			{#annotationField = #td;}
		|	t:typeSpec[false]		// annotation field
			(	i:IDENT				// the name of the field

				LPAREN! RPAREN!

				rt:declaratorBrackets[#t]

				( "default" amvi:annotationMemberValueInitializer )?

				SEMI

				{#annotationField =
					#(#[ANNOTATION_FIELD_DEF,"ANNOTATION_FIELD_DEF"],
						 mods,
						 #(#[TYPE,"TYPE"],rt),
						 i,amvi
						 );}
			|	v:variableDefinitions[#mods,#t] SEMI	// variable
				{#annotationField = #v;}
			)
		)
	;

//An enum constant may have optional parameters and may have a
//a class body
(�)enumConstant!
	:	an:annotations
		i:IDENT
		(	LPAREN!
			a:argList
			RPAREN!
		)?
		( b:enumConstantBlock )?
		{#enumConstant = #([ENUM_CONSTANT_DEF, "ENUM_CONSTANT_DEF"], an, i, a, b);}
	;

//The class-like body of an enum constant
(�)enumConstantBlock
	:	LCURLY!
		( enumConstantField | SEMI! )*
		RCURLY!
		{#enumConstantBlock = #([OBJBLOCK, "OBJBLOCK"], #enumConstantBlock);}
	;

//An enum constant field is just like a class field but without
//the posibility of a constructor definition or a static initializer
(�)enumConstantField!
	:	mods:modifiers
		(	td:typeDefinitionInternal[#mods]
			{#enumConstantField = #td;}

		|	// A generic method has the typeParameters before the return type.
			// This is not allowed for variable definitions, but this production
			// allows it, a semantic check could be used if you wanted.
			(tp:typeParameters)? t:typeSpec[false]		// method or variable declaration(s)
			(	IDENT									// the name of the method

				// parse the formal parameter declarations.
				LPAREN! param:parameterDeclarationList RPAREN!

				rt:declaratorBrackets[#t]

				// get the list of exceptions that this method is
				// declared to throw
				(tc:throwsClause)?

				( s2:compoundStatement | SEMI )
				{#enumConstantField = #(#[METHOD_DEF,"METHOD_DEF"],
							 mods,
							 tp,
							 #(#[TYPE,"TYPE"],rt),
							 IDENT,
							 param,
							 tc,
							 s2);}
			|	v:variableDefinitions[#mods,#t] SEMI
				{#enumConstantField = #v;}
			)
		)

	// "{ ... }" instance initializer
	|	s4:compoundStatement
		{#enumConstantField = #(#[INSTANCE_INIT,"INSTANCE_INIT"], s4);}
	;

// An interface can extend several other interfaces...
(�)interfaceExtends
	:	(
		e:"extends"!
		classOrInterfaceType[false] ( COMMA! classOrInterfaceType[false] )*
		)?
		{#interfaceExtends = #(#[EXTENDS_CLAUSE,"EXTENDS_CLAUSE"],
								#interfaceExtends);}
	;

// A class can implement several interfaces...
(�)implementsClause
	:	(
			i:"implements"! classOrInterfaceType[false] ( COMMA! classOrInterfaceType[false] )*
		)?
		{#implementsClause = #(#[IMPLEMENTS_CLAUSE,"IMPLEMENTS_CLAUSE"],
								 #implementsClause);}
	;

// Now the various things that can be defined inside a class
(�)classField!
	:	// method, constructor, or variable declaration
		mods:modifiers
		(	td:typeDefinitionInternal[#mods]
			{#classField = #td;}

		|	(tp:typeParameters)?
			(
				h:ctorHead s:constructorBody // constructor
				{#classField = #(#[CTOR_DEF,"CTOR_DEF"], mods, tp, h, s);}

				|	// A generic method/ctor has the typeParameters before the return type.
					// This is not allowed for variable definitions, but this production
					// allows it, a semantic check could be used if you wanted.
					t:typeSpec[false]		// method or variable declaration(s)
					(	IDENT				// the name of the method

						// parse the formal parameter declarations.
						LPAREN! param:parameterDeclarationList RPAREN!

						rt:declaratorBrackets[#t]

						// get the list of exceptions that this method is
						// declared to throw
						(tc:throwsClause)?

						( s2:compoundStatement | SEMI )
						{#classField = #(#[METHOD_DEF,"METHOD_DEF"],
									 mods,
									 tp,
									 #(#[TYPE,"TYPE"],rt),
									 IDENT,
									 param,
									 tc,
									 s2);}
					|	v:variableDefinitions[#mods,#t] SEMI
						{#classField = #v;}
					)
			)
		)

	// "static { ... }" class initializer
	|	"static" s3:compoundStatement
		{#classField = #(#[STATIC_INIT,"STATIC_INIT"], s3);}

	// "{ ... }" instance initializer
	|	s4:compoundStatement
		{#classField = #(#[INSTANCE_INIT,"INSTANCE_INIT"], s4);}
	;

// Now the various things that can be defined inside a interface
(�)interfaceField!
	:	// method, constructor, or variable declaration
		mods:modifiers
		(	td:typeDefinitionInternal[#mods]
			{#interfaceField = #td;}

		|	(tp:typeParameters)?
			// A generic method has the typeParameters before the return type.
			// This is not allowed for variable definitions, but this production
			// allows it, a semantic check could be used if you want a more strict
			// grammar.
			t:typeSpec[false]		// method or variable declaration(s)
			(	IDENT				// the name of the method

				// parse the formal parameter declarations.
				LPAREN! param:parameterDeclarationList RPAREN!

				rt:declaratorBrackets[#t]

				// get the list of exceptions that this method is
				// declared to throw
				(tc:throwsClause)?

				SEMI
				
				{#interfaceField = #(#[METHOD_DEF,"METHOD_DEF"],
							 mods,
							 tp,
							 #(#[TYPE,"TYPE"],rt),
							 IDENT,
							 param,
							 tc);}
			|	v:variableDefinitions[#mods,#t] SEMI
				{#interfaceField = #v;}
			)
		)
	;

(�)constructorBody
	:	lc:LCURLY^ {#lc.setType(SLIST);}
			( options { greedy=true; } : explicitConstructorInvocation)?
			(statement)*
		RCURLY!
	;

/** Catch obvious constructor calls, but not the expr.super(...) calls */
(�)explicitConstructorInvocation
	:	(typeArguments)?
		(	"this"! lp1:LPAREN^ argList RPAREN! SEMI!
			{#lp1.setType(CTOR_CALL);}
		|	"super"! lp2:LPAREN^ argList RPAREN! SEMI!
			{#lp2.setType(SUPER_CTOR_CALL);}
		)
	;

(�)variableDefinitions[AST mods, AST t]
	:	variableDeclarator[getASTFactory().dupTree(mods),
							getASTFactory().dupList(t)] //dupList as this also copies siblings (like TYPE_ARGUMENTS)
		(	COMMA!
			variableDeclarator[getASTFactory().dupTree(mods),
							getASTFactory().dupList(t)] //dupList as this also copies siblings (like TYPE_ARGUMENTS)
		)*
	;

/** Declaration of a variable. This can be a class/instance variable,
 *  or a local variable in a method
 *  It can also include possible initialization.
 */
(�)variableDeclarator![AST mods, AST t]
	:	id:IDENT d:declaratorBrackets[t] v:varInitializer
		{#variableDeclarator = #(#[VARIABLE_DEF,"VARIABLE_DEF"], mods, #(#[TYPE,"TYPE"],d), id, v);}
	;

(�)declaratorBrackets[AST typ]
	:	{#declaratorBrackets=typ;}
		(lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);} RBRACK!)*
	;

(�)varInitializer
	:	( ASSIGN^ initializer )?
	;

// This is an initializer used to set up an array.
(�)arrayInitializer
	:	lc:LCURLY^ {#lc.setType(ARRAY_INIT);}
			(	initializer
				(
					// CONFLICT: does a COMMA after an initializer start a new
					// initializer or start the option ',' at end?
					// ANTLR generates proper code by matching
					// the comma as soon as possible.
					options {
						warnWhenFollowAmbig = false;
					}
				:
					COMMA! initializer
				)*
				(COMMA!)?
			)?
		RCURLY!
	;


// The two "things" that can initialize an array element are an expression
// and another (nested) array initializer.
(�)initializer
	:	expression
	|	arrayInitializer
	;

// This is the header of a method. It includes the name and parameters
// for the method.
// This also watches for a list of exception classes in a "throws" clause.
(�)ctorHead
	:	IDENT // the name of the method

		// parse the formal parameter declarations.
		LPAREN! parameterDeclarationList RPAREN!

		// get the list of exceptions that this method is declared to throw
		(throwsClause)?
	;

// This is a list of exception classes that the method is declared to throw
(�)throwsClause
	:	"throws"^ identifier ( COMMA! identifier )*
	;

// A list of formal parameters
//	 Zero or more parameters
//	 If a parameter is variable length (e.g. String... myArg) it is the right-most parameter
(�)parameterDeclarationList
	// The semantic check in ( .... )* block is flagged as superfluous, and seems superfluous but
	// is the only way I could make this work. If my understanding is correct this is a known bug
	:	(	( parameterDeclaration )=> parameterDeclaration
			( options {warnWhenFollowAmbig=false;} : ( COMMA! parameterDeclaration ) => COMMA! parameterDeclaration )*
			( COMMA! variableLengthParameterDeclaration )?
		|
			variableLengthParameterDeclaration
		)?
		{#parameterDeclarationList = #(#[PARAMETERS,"PARAMETERS"],
									#parameterDeclarationList);}
	;

// A formal parameter.
(�)parameterDeclaration!
	:	pm:parameterModifier t:typeSpec[false] id:IDENT
		pd:declaratorBrackets[#t]
		{#parameterDeclaration = #(#[PARAMETER_DEF,"PARAMETER_DEF"],
									pm, #([TYPE,"TYPE"],pd), id);}
	;

(�)variableLengthParameterDeclaration!
	:	pm:parameterModifier t:typeSpec[false] TRIPLE_DOT! id:IDENT
		pd:declaratorBrackets[#t]
		{#variableLengthParameterDeclaration = #(#[VARIABLE_PARAMETER_DEF,"VARIABLE_PARAMETER_DEF"],
												pm, #([TYPE,"TYPE"],pd), id);}
	;

(�)parameterModifier
	//final can appear amongst annotations in any order - greedily consume any preceding
	//annotations to shut nond-eterminism warnings off
	:	(options{greedy=true;} : annotation)* (f:"final")? (annotation)*
		{#parameterModifier = #(#[MODIFIERS,"MODIFIERS"], #parameterModifier);}
	;

// Compound statement. This is used in many contexts:
// Inside a class definition prefixed with "static":
// it is a class initializer
// Inside a class definition without "static":
// it is an instance initializer
// As the body of a method
// As a completely indepdent braced block of code inside a method
// it starts a new scope for variable definitions

(�)compoundStatement
	:	lc:LCURLY^ {#lc.setType(SLIST);}
			// include the (possibly-empty) list of statements
			(statement)*
		RCURLY!
	;


(�)statement
	// A list of statements in curly braces -- start a new scope!
	:	compoundStatement

	// declarations are ambiguous with "ID DOT" relative to expression
	// statements. Must backtrack to be sure. Could use a semantic
	// predicate to test symbol table to see what the type was coming
	// up, but that's pretty hard without a symbol table ;)
	|	(declaration)=> declaration SEMI!

	// An expression statement. This could be a method call,
	// assignment statement, or any other expression evaluated for
	// side-effects.
	|	expression SEMI!

	//TODO: what about interfaces, enums and annotations
	// class definition
	|	m:modifiers! classDefinition[#m]

	// Attach a label to the front of a statement
	|	IDENT c:COLON^ {#c.setType(LABELED_STAT);} statement

	// If-else statement
	|	"if"^ LPAREN! expression RPAREN! statement
		(
			// CONFLICT: the old "dangling-else" problem...
			// ANTLR generates proper code matching
			// as soon as possible. Hush warning.
			options {
				warnWhenFollowAmbig = false;
			}
		:
			"else"! statement
		)?

	// For statement
	|	forStatement

	// While statement
	|	"while"^ LPAREN! expression RPAREN! statement

	// do-while statement
	|	"do"^ statement "while"! LPAREN! expression RPAREN! SEMI!

	// get out of a loop (or switch)
	|	"break"^ (IDENT)? SEMI!

	// do next iteration of a loop
	|	"continue"^ (IDENT)? SEMI!

	// Return an expression
	|	"return"^ (expression)? SEMI!

	// switch/case statement
	|	"switch"^ LPAREN! expression RPAREN! LCURLY!
			( casesGroup )*
		RCURLY!

	// exception try-catch block
	|	tryBlock

	// throw an exception
	|	"throw"^ expression SEMI!

	// synchronize a statement
	|	"synchronized"^ LPAREN! expression RPAREN! compoundStatement

	// asserts (uncomment if you want 1.4 compatibility)
	|	"assert"^ expression ( COLON! expression )? SEMI!

	// empty statement
	|	s:SEMI {#s.setType(EMPTY_STAT);}
	;

(�)forStatement
	:	f:"for"^
		LPAREN!
			(	(forInit SEMI)=>traditionalForClause
			|	forEachClause
			)
		RPAREN!
		statement					 // statement to loop over
	;

(�)traditionalForClause
	:
		forInit SEMI!	// initializer
		forCond SEMI!	// condition test
		forIter			// updater
	;

(�)forEachClause
	:
		p:parameterDeclaration COLON! expression
		{#forEachClause = #(#[FOR_EACH_CLAUSE,"FOR_EACH_CLAUSE"], #forEachClause);}
	;

(�)casesGroup
	:	(	// CONFLICT: to which case group do the statements bind?
			// ANTLR generates proper code: it groups the
			// many "case"/"default" labels together then
			// follows them with the statements
			options {
				greedy = true;
			}
			:
			aCase
		)+
		caseSList
		{#casesGroup = #([CASE_GROUP, "CASE_GROUP"], #casesGroup);}
	;

(�)aCase
	:	("case"^ expression | "default") COLON!
	;

(�)caseSList
	:	(statement)*
		{#caseSList = #(#[SLIST,"SLIST"],#caseSList);}
	;

// The initializer for a for loop
(�)forInit
		// if it looks like a declaration, it is
	:	((declaration)=> declaration
		// otherwise it could be an expression list...
		|	expressionList
		)?
		{#forInit = #(#[FOR_INIT,"FOR_INIT"],#forInit);}
	;

(�)forCond
	:	(expression)?
		{#forCond = #(#[FOR_CONDITION,"FOR_CONDITION"],#forCond);}
	;

(�)forIter
	:	(expressionList)?
		{#forIter = #(#[FOR_ITERATOR,"FOR_ITERATOR"],#forIter);}
	;

// an exception handler try/catch block
(�)tryBlock
	:	"try"^ compoundStatement
		(handler)*
		( finallyClause )?
	;

(�)finallyClause
	:	"finally"^ compoundStatement
	;

// an exception handler
(�)handler
	:	"catch"^ LPAREN! parameterDeclaration RPAREN! compoundStatement
	;


// expressions
// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//	   nextHigherPrecedenceExpression
//		   (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
// The operators in java have the following precedences:
//	lowest  (13)  = *= /= %= += -= <<= >>= >>>= &= ^= |=
//			(12)  ?:
//			(11)  ||
//			(10)  &&
//			( 9)  |
//			( 8)  ^
//			( 7)  &
//			( 6)  == !=
//			( 5)  < <= > >=
//			( 4)  << >>
//			( 3)  +(binary) -(binary)
//			( 2)  * / %
//			( 1)  ++ -- +(unary) -(unary)  ~  !  (type)
//				  []   () (method call)  . (dot -- identifier qualification)
//				  new   ()  (explicit parenthesis)
//
// the last two are not usually on a precedence chart; I put them in
// to point out that new has a higher precedence than '.', so you
// can validy use
//	 new Frame().show()
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
//   is usually very straightfoward



// the mother of all expressions
(�)expression
	:	assignmentExpression
		{#expression = #(#[EXPR,"EXPR"],#expression);}
	;


// This is a list of expressions.
(�)expressionList
	:	expression (COMMA! expression)*
		{#expressionList = #(#[ELIST,"ELIST"], expressionList);}
	;


// assignment expression (level 13)
(�)assignmentExpression
	:	conditionalExpression
		(	(	ASSIGN^
			|	PLUS_ASSIGN^
			|	MINUS_ASSIGN^
			|	STAR_ASSIGN^
			|	DIV_ASSIGN^
			|	MOD_ASSIGN^
			|	SR_ASSIGN^
			|	BSR_ASSIGN^
			|	SL_ASSIGN^
			|	BAND_ASSIGN^
			|	BXOR_ASSIGN^
			|	BOR_ASSIGN^
			)
			assignmentExpression
		)?
	;


// conditional test (level 12)
(�)conditionalExpression
	:	logicalOrExpression
		( QUESTION^ assignmentExpression COLON! conditionalExpression )?
	;


// logical or (||) (level 11)
(�)logicalOrExpression
	:	logicalAndExpression (LOR^ logicalAndExpression)*
	;


// logical and (&&) (level 10)
(�)logicalAndExpression
	:	inclusiveOrExpression (LAND^ inclusiveOrExpression)*
	;


// bitwise or non-short-circuiting or (|) (level 9)
(�)inclusiveOrExpression
	:	exclusiveOrExpression (BOR^ exclusiveOrExpression)*
	;


// exclusive or (^) (level 8)
(�)exclusiveOrExpression
	:	andExpression (BXOR^ andExpression)*
	;


// bitwise or non-short-circuiting and (&) (level 7)
(�)andExpression
	:	equalityExpression (BAND^ equalityExpression)*
	;


// equality/inequality (==/!=) (level 6)
(�)equalityExpression
	:	relationalExpression ((NOT_EQUAL^ | EQUAL^) relationalExpression)*
	;


// boolean relational expressions (level 5)
(�)relationalExpression
	:	shiftExpression
		(	(	(	LT^
				|	GT^
				|	LE^
				|	GE^
				)
				shiftExpression
			)*
		|	"instanceof"^ typeSpec[true]
		)
	;


// bit shift expressions (level 4)
(�)shiftExpression
	:	additiveExpression ((SL^ | SR^ | BSR^) additiveExpression)*
	;


// binary addition/subtraction (level 3)
(�)additiveExpression
	:	multiplicativeExpression ((PLUS^ | MINUS^) multiplicativeExpression)*
	;


// multiplication/division/modulo (level 2)
(�)multiplicativeExpression
	:	unaryExpression ((STAR^ | DIV^ | MOD^ ) unaryExpression)*
	;

(�)unaryExpression
	:	INC^ unaryExpression
	|	DEC^ unaryExpression
	|	MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
	|	PLUS^ {#PLUS.setType(UNARY_PLUS);} unaryExpression
	|	unaryExpressionNotPlusMinus
	;

(�)unaryExpressionNotPlusMinus
	:	BNOT^ unaryExpression
	|	LNOT^ unaryExpression
	|	(	// subrule allows option to shut off warnings
			options {
				// "(int" ambig with postfixExpr due to lack of sequence
				// info in linear approximate LL(k). It's ok. Shut up.
				generateAmbigWarnings=false;
			}
		:	// If typecast is built in type, must be numeric operand
			// Have to backtrack to see if operator follows

			(LPAREN builtInTypeSpec[true] RPAREN unaryExpression)=>
			lpb:LPAREN^ {#lpb.setType(TYPECAST);} builtInTypeSpec[true] RPAREN!
			unaryExpression

		// Have to backtrack to see if operator follows. If no operator
		// follows, it's a typecast. No semantic checking needed to parse.
		// if it _looks_ like a cast, it _is_ a cast; else it's a "(expr)"

		|	(LPAREN classTypeSpec[true] RPAREN unaryExpressionNotPlusMinus)=>
			lp:LPAREN^ {#lp.setType(TYPECAST);} classTypeSpec[true] RPAREN!
			unaryExpressionNotPlusMinus

		|	postfixExpression
		)
	;

// qualified names, array expressions, method invocation, post inc/dec
(�)postfixExpression
	:
		primaryExpression

		(
			/*
			options {
				// the use of postfixExpression in SUPER_CTOR_CALL adds DOT
				// to the lookahead set, and gives loads of false non-det
				// warnings.
				// shut them off.
				generateAmbigWarnings=false;
			}
		:	*/
			//type arguments are only appropriate for a parameterized method/ctor invocations
			//semantic check may be needed here to ensure that this is the case
			DOT^ (typeArguments)?
				(	IDENT
					(	lp:LPAREN^ {#lp.setType(METHOD_CALL);}
						argList
						RPAREN!
					)?
				|	"super"
					(	// (new Outer()).super() (create enclosing instance)
						lp3:LPAREN^ argList RPAREN!
						{#lp3.setType(SUPER_CTOR_CALL);}
					|	DOT^ (typeArguments)? IDENT
						(	lps:LPAREN^ {#lps.setType(METHOD_CALL);}
							argList
							RPAREN!
						)?
					)
				)
		|	DOT^ "this"
		|	DOT^ newExpression
		|	lb:LBRACK^ {#lb.setType(INDEX_OP);} expression RBRACK!
		)*

		(	// possibly add on a post-increment or post-decrement.
			// allows INC/DEC on too much, but semantics can check
			in:INC^ {#in.setType(POST_INC);}
	 	|	de:DEC^ {#de.setType(POST_DEC);}
		)?
 	;

// the basic element of an expression
(�)primaryExpression
	:	identPrimary ( options {greedy=true;} : DOT^ "class" )?
	|	constant
	|	"true"
	|	"false"
	|	"null"
	|	newExpression
	|	"this"
	|	"super"
	|	LPAREN! assignmentExpression RPAREN!
		// look for int.class and int[].class
	|	builtInType
		( lbt:LBRACK^ {#lbt.setType(ARRAY_DECLARATOR);} RBRACK! )*
		DOT^ "class"
	;

/** Match a, a.b.c refs, a.b.c(...) refs, a.b.c[], a.b.c[].class,
 *  and a.b.c.class refs. Also this(...) and super(...). Match
 *  this or super.
 */
(�)identPrimary
	:	(ta1:typeArguments!)?
		IDENT
		// Syntax for method invocation with type arguments is
		// <String>foo("blah")
		(
			options {
				// .ident could match here or in postfixExpression.
				// We do want to match here. Turn off warning.
				greedy=true;
				// This turns the ambiguity warning of the second alternative
				// off. See below. (The "false" predicate makes it non-issue)
				warnWhenFollowAmbig=false;
			}
			// we have a new nondeterminism because of
			// typeArguments... only a syntactic predicate will help...
			// The problem is that this loop here conflicts with
			// DOT typeArguments "super" in postfixExpression (k=2)
			// A proper solution would require a lot of refactoring...
		:	(DOT (typeArguments)? IDENT) =>
				DOT^ (ta2:typeArguments!)? IDENT
		|	{false}?	// FIXME: this is very ugly but it seems to work...
						// this will also produce an ANTLR warning!
				// Unfortunately a syntactic predicate can only select one of
				// multiple alternatives on the same level, not break out of
				// an enclosing loop, which is why this ugly hack (a fake
				// empty alternative with always-false semantic predicate)
				// is necessary.
		)*
		(
			options {
				// ARRAY_DECLARATOR here conflicts with INDEX_OP in
				// postfixExpression on LBRACK RBRACK.
				// We want to match [] here, so greedy. This overcomes
				// limitation of linear approximate lookahead.
				greedy=true;
			}
		:	(	lp:LPAREN^ {#lp.setType(METHOD_CALL);}
				// if the input is valid, only the last IDENT may
				// have preceding typeArguments... rather hacky, this is...
				{if (#ta2 != null) astFactory.addASTChild(currentAST, #ta2);}
				{if (#ta2 == null) astFactory.addASTChild(currentAST, #ta1);}
				argList RPAREN!
			)
		|	( options {greedy=true;} :
				lbc:LBRACK^ {#lbc.setType(ARRAY_DECLARATOR);} RBRACK!
			)+
		)?
	;

/** object instantiation.
 *  Trees are built as illustrated by the following input/tree pairs:
 *
 *  new T()
 *
 *  new
 *   |
 *   T --  ELIST
 *		   |
 *		  arg1 -- arg2 -- .. -- argn
 *
 *  new int[]
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *
 *  new int[] {1,2}
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR -- ARRAY_INIT
 *								  |
 *								EXPR -- EXPR
 *								  |	  |
 *								  1	  2
 *
 *  new int[3]
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *				|
 *			  EXPR
 *				|
 *				3
 *
 *  new int[1][2]
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *			   |
 *		 ARRAY_DECLARATOR -- EXPR
 *			   |			  |
 *			 EXPR			 1
 *			   |
 *			   2
 *
 */
(�)newExpression
	:	"new"^ (typeArguments)? type
		(	LPAREN! argList RPAREN! (classBlock)?

			//java 1.1
			// Note: This will allow bad constructs like
			//	new int[4][][3] {exp,exp}.
			//	There needs to be a semantic check here...
			// to make sure:
			//   a) [ expr ] and [ ] are not mixed
			//   b) [ expr ] and an init are not used together

		|	newArrayDeclarator (arrayInitializer)?
		)
	;

(�)argList
	:	(	expressionList
		|	/*nothing*/
			{#argList = #[ELIST,"ELIST"];}
		)
	;

(�)newArrayDeclarator
	:	(
			// CONFLICT:
			// newExpression is a primaryExpression which can be
			// followed by an array index reference. This is ok,
			// as the generated code will stay in this loop as
			// long as it sees an LBRACK (proper behavior)
			options {
				warnWhenFollowAmbig = false;
			}
		:
			lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);}
				(expression)?
			RBRACK!
		)+
	;

(�)constant
	:	NUM_INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	|	NUM_FLOAT
	|	NUM_LONG
	|	NUM_DOUBLE
	;

