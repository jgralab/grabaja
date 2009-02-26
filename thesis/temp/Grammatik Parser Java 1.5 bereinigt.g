compilationUnit
	::=	[ packageDefinition ]
		{ importDefinition }
		{ typeDefinition }
		EOF
	;

packageDefinition
	::=	annotations "package" identifier ";"
	;

importDefinition
	::=	"import" [ "static" ] identifierStar ";"
	;

typeDefinition
	::=	modifiers
		typeDefinitionInternal
	|	";"
	;

typeDefinitionInternal
	::=	classDefinition
	|	interfaceDefinition
	|	enumDefinition
	|	annotationDefinition
	;

declaration
	::=	modifiers typeSpec variableDefinitions
	;

typeSpec
	::=	classTypeSpec | builtInTypeSpec
	;

classTypeSpec
	::=	classOrInterfaceType
		{ "[" "]" }
	;

classOrInterfaceType
	::=	IDENT [typeArguments]
		{ "." IDENT [typeArguments] }
	;

typeArgumentSpec
	::=	classTypeSpec | builtInTypeArraySpec
	;

typeArgument
	::=	typeArgumentSpec | wildcardType
	;

wildcardType
	::=	"?" [typeArgumentBounds]
	;

typeArguments
	::=	"<"
		typeArgument
		{ "," typeArgument }
		[ typeArgumentsOrParametersEnd ]
	;

typeArgumentsOrParametersEnd
	::=	">"
	|	">>"
	|	">>>"
	;

typeArgumentBounds
	::=	( "extends" | "super" ) classOrInterfaceType
	;

builtInTypeArraySpec
	::=	builtInType "[" "]" { "[" "]" }
	;

builtInTypeSpec
	::=	builtInType { "[" "]" }
	;

type
	::=	classOrInterfaceType | builtInType
	;

builtInType
	::=	"void"
	|	"boolean"
	|	"byte"
	|	"char"
	|	"short"
	|	"int"
	|	"float"
	|	"long"
	|	"double"
	;

identifier
	::=	IDENT { "." IDENT }
	;

identifierStar
	::=	IDENT
		{ "." IDENT }
		[ "." "*" ]
	;

modifiers
	::=	{ modifier | annotation }
	;

modifier
	::=	"private"
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

annotation
	::=	"@" identifier [ "(" [ annotationArguments ] ")" ]
	;

annotations
	::=   {annotation}
	;

annotationArguments
	::=	annotationMemberValueInitializer | anntotationMemberValuePairs
	;

anntotationMemberValuePairs
	::=	annotationMemberValuePair { "," annotationMemberValuePair }
	;

annotationMemberValuePair
	::=	IDENT "=" annotationMemberValueInitializer
	;

annotationMemberValueInitializer
	::=
		conditionalExpression | annotation | annotationMemberArrayInitializer
	;

annotationMemberArrayInitializer
	::=	"{"
		[ annotationMemberArrayValueInitializer
		  { "," annotationMemberArrayValueInitializer }
		  [ "," ]
		]
		"}"
	;

annotationMemberArrayValueInitializer
	::=	conditionalExpression
	|	annotation
	;

superClassClause
	::=	[ "extends" classOrInterfaceType ]
	;

classDefinition
	::=	"class" IDENT
		[ typeParameters ]
		superClassClause
		implementsClause
		classBlock
	;

interfaceDefinition
	::=	"interface" IDENT
		[ typeParameters ]
		interfaceExtends
		interfaceBlock
	;

enumDefinition
	::=	"enum" IDENT
		implementsClause
		enumBlock
	;

annotationDefinition
	::=	"@" "interface" IDENT
		annotationBlock
	;

typeParameters
	::=
		"<"
		typeParameter { "," typeParameter }
		[ typeArgumentsOrParametersEnd ]
	;

typeParameter
	::=
		IDENT [ typeParameterBounds ]
	;

typeParameterBounds
	::=
		"extends" classOrInterfaceType
		{ "&" classOrInterfaceType }
	;

classBlock
	::=	"{"
			{ classField | ";" }
		"}"
	;

interfaceBlock
	::=	"{"
			{ interfaceField | ";" }
		"}"
	;
	
annotationBlock
	::=	"{"
			{ annotationField | ";" }
		"}"
	;

enumBlock
	::=	"{"
			[ enumConstant { "," enumConstant } [ "," ] ]
			[ ";" { classField | ";" } ]
		"}"
	;

annotationField
	::=	modifiers
		(	typeDefinitionInternal
		|	typeSpec
			(	IDENT
				"(" ")"
				declaratorBrackets
				[ "default" annotationMemberValueInitializer ]
				";"
			|	variableDefinitions ";"
			)
		)
	;

enumConstant
	::=	annotations
		IDENT
		[ "(" argList ")" ]
		[ enumConstantBlock ]
	;

enumConstantBlock
	::=	"{" { enumConstantField | ";" } "}"
	;

enumConstantField
	::=	modifiers
		(	typeDefinitionInternal
		|	[ typeParameters ] typeSpec
			(	IDENT
				"(" parameterDeclarationList ")"
				declaratorBrackets
				[ throwsClause ]
				( compoundStatement | ";" )
			|	variableDefinitions ";"
			)
		)
	|	compoundStatement
	;

interfaceExtends
	::=	[ "extends" classOrInterfaceType { "," classOrInterfaceType } ]
	;

implementsClause
	::=	[ "implements" classOrInterfaceType { "," classOrInterfaceType } ]
	;

classField
	::=	modifiers
		(	typeDefinitionInternal
		|	(typeParameters]
			(
				ctorHead constructorBody
			|	typeSpec
				(	IDENT
					"(" parameterDeclarationList ")"
					declaratorBrackets
					[ throwsClause ]
				|	variableDefinitions ";"
				)
			)
		)
	|	[ "static" ] compoundStatement
	;

interfaceField
	::=	modifiers
		(	typeDefinitionInternal
		|	[ typeParameters ]
			typeSpec
			(	IDENT
				"(" parameterDeclarationList ")"
				declaratorBrackets
				[ throwsClause ]
				";"
			|	variableDefinitions ";"
			)
		)
	;

constructorBody
	::=	"{" [ explicitConstructorInvocation ] {statement} "}"
	;

explicitConstructorInvocation
	::=	[ typeArguments ]
		(	"this" "(" argList ")" ";"
		|	"super" "(" argList ")" ";"
		)
	;

variableDefinitions
	::=	variableDeclarator { "," variableDeclarator }
	;

variableDeclarator
	::=	IDENT declaratorBrackets varInitializer
	;

declaratorBrackets
	::=	{ "[" "]" }
	;

varInitializer
	::=	[ "=" initializer ]
	;

arrayInitializer
	::=	"{"
			[	initializer
				{ "," initializer }
				[","]
			]
		"}"
	;


initializer
	::=	expression | arrayInitializer
	;

ctorHead
	::=	IDENT
		"(" parameterDeclarationList ")"
		[ throwsClause ]
	;

throwsClause
	::=	"throws" identifier { "," identifier }
	;

parameterDeclarationList
	::=	[	parameterDeclaration
			{ "," parameterDeclaration }
			[ "," variableLengthParameterDeclaration ]
		|
			variableLengthParameterDeclaration
		]
	;

parameterDeclaration
	::=	parameterModifier typeSpec IDENT declaratorBrackets
	;

variableLengthParameterDeclaration
	::=	parameterModifier typeSpec "..." IDENT declaratorBrackets
	;

parameterModifier
	::=	{ annotation } [ "final" ] { annotation }
	;

compoundStatement
	::=	"{" { statement } "}"
	;

statement
	::=	compoundStatement
	|	declaration ";"
	|	expression ";"
	|	modifiers classDefinition
	|	IDENT ":" statement
	|	"if" "(" expression ")" statement [ "else" statement ]
	|	forStatement
	|	"while" "(" expression ")" statement
	|	"do" statement "while" "(" expression ")" ";"
	|	"break" [ IDENT ] ";"
	|	"continue" [ IDENT ] ";"
	|	"return" [ expression ] ";"
	|	"switch" "(" expression ")" "{" { casesGroup } "}"
	|	tryBlock
	|	"throw" expression ";"
	|	"synchronized" "(" expression ")" compoundStatement
	|	"assert" expression [ ":" expression ] ";"
	|	";"
	;

forStatement
	::=	"for" "(" ( traditionalForClause | forEachClause ) ")" statement
	;

traditionalForClause
	::=	forInit ";" forCond ";" forIter
	;

forEachClause
	::=	parameterDeclaration ":" expression
	;

casesGroup
	::=	aCase { aCase } caseSList
	;

aCase
	::=	("case" expression | "default") ":"
	;

caseSList
	::=	{ statement }
	;

forInit
	::=	[ declaration | expressionList ]
	;

forCond
	::=	[ expression ]
	;

forIter
	::=	[ expressionList ]
	;

tryBlock
	::=	"try" compoundStatement { handler } [ finallyClause ]
	;

finallyClause
	::=	"finally" compoundStatement
	;

handler
	::=	"catch" "(" parameterDeclaration ")" compoundStatement
	;

expressionList
	::=	expression { "," expression }
	;

expression
	::=	assignmentExpression
	;

assignmentExpression
	::=	conditionalExpression
		[	(	"="
			|	"+="
			|	"-="
			|	"*="
			|	"/="
			|	"%="
			|	">>="
			|	">>>="
			|	"<<="
			|	"&="
			|	"^="
			|	"|="
			)
			assignmentExpression
		]
	;

conditionalExpression
	::=	logicalOrExpression [ "?" assignmentExpression ":" conditionalExpression ]
	;

logicalOrExpression
	::=	logicalAndExpression { "||" logicalAndExpression }
	;

logicalAndExpression
	::=	inclusiveOrExpression { "&&" inclusiveOrExpression }
	;

inclusiveOrExpression
	::=	exclusiveOrExpression { "|" exclusiveOrExpression }
	;

exclusiveOrExpression
	::=	andExpression { "^" andExpression }
	;

andExpression
	::=	equalityExpression { "&" equalityExpression }
	;

equalityExpression
	::=	relationalExpression { ( "!=" | "==" ) relationalExpression }
	;

relationalExpression
	::=	shiftExpression
		(	{	(	"<"
				|	">"
				|	"<="
				|	">="
				)
				shiftExpression
			}
		|	"instanceof" typeSpec
		)
	;

shiftExpression
	::=	additiveExpression { ( "<<" | ">>" | ">>>" ) additiveExpression }
	;

additiveExpression
	::=	multiplicativeExpression { ( "+" | "-" ) multiplicativeExpression }
	;

multiplicativeExpression
	::=	unaryExpression { ( "*" | "/" | "%" ) unaryExpression }
	;

unaryExpression
	::=	"++" unaryExpression
	|	"--" unaryExpression
	|	"-" unaryExpression
	|	"+" unaryExpression
	|	unaryExpressionNotPlusMinus
	;

unaryExpressionNotPlusMinus
	::=	"~" unaryExpression
	|	"!" unaryExpression
	|	"(" builtInTypeSpec ")" unaryExpression
	|	"(" classTypeSpec ")" unaryExpressionNotPlusMinus
	|	postfixExpression
	;

postfixExpression
	::=	primaryExpression
		{ 	"." [ typeArguments ]
				(	IDENT [ "(" argList ")" ]
				|	"super"
					(	"(" argList ")"
					|	"." [ typeArguments ] IDENT [ "(" argList ")" ]
					)
				)
		|	"." "this"
		|	"." newExpression
		|	"[" expression "]"
		}
		[ "++" | "--" ]
 	;

primaryExpression
	::=	identPrimary [ "." "class" ]
	|	constant
	|	"true"
	|	"false"
	|	"null"
	|	newExpression
	|	"this"
	|	"super"
	|	"(" assignmentExpression ")"
	|	builtInType { "[" "]" } "." "class"
	;

identPrimary
	::=	[ typeArguments ]
		IDENT
		{ "." [ typeArguments ] IDENT }
		[	"(" argList ")"
		|	"[" "]" { "[" "]" }
		]
	;

newExpression
	::=	"new" [ typeArguments ] type
		(	"(" argList ")" [ classBlock ]
		|	newArrayDeclarator [ arrayInitializer ]
		)
	;

argList
	::=	expressionList | /*nothing*/
	;

newArrayDeclarator
	::=	"[" [ expression ] "]" { "[" [ expression ] "]" }
	;

constant
	::=	NUM_INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	|	NUM_FLOAT
	|	NUM_LONG
	|	NUM_DOUBLE
	;
