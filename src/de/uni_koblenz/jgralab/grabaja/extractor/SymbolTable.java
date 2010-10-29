package de.uni_koblenz.jgralab.grabaja.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.java5schema.AnnotationDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.BooleanConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInType;
import de.uni_koblenz.jgralab.grabaja.java5schema.BuiltInTypes;
import de.uni_koblenz.jgralab.grabaja.java5schema.CharConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.ClassDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.ConstructorDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoubleConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EmptyStatement;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.EnumDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldAccess;
import de.uni_koblenz.jgralab.grabaja.java5schema.FieldDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.FloatConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.IntegerConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.InterfaceDefinition;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsModifierOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.JavaPackage;
import de.uni_koblenz.jgralab.grabaja.java5schema.Label;
import de.uni_koblenz.jgralab.grabaja.java5schema.LongConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.MethodInvocation;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.Modifiers;
import de.uni_koblenz.jgralab.grabaja.java5schema.Null;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedName;
import de.uni_koblenz.jgralab.grabaja.java5schema.QualifiedType;
import de.uni_koblenz.jgralab.grabaja.java5schema.StringConstant;
import de.uni_koblenz.jgralab.grabaja.java5schema.Type;
import de.uni_koblenz.jgralab.grabaja.java5schema.TypeParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.VariableDeclaration;

/**
 * Holds references to all vertices needed to resolve types in TGraph and to
 * make sure that certain vertices are not created more than once (this
 * functionality is implemented by factories in package
 * <code>javaextractor.factories</code>).<br>
 * 
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class SymbolTable {

	/**
	 * Reference to the TGraph this symbol table belongs to.
	 */
	Java5 programGraph;

	/**
	 * Creates a new symbol table.
	 * 
	 * @param programGraph
	 *            TGraph the symbol table should belong to.
	 */
	public SymbolTable(Java5 programGraph) {
		this.programGraph = programGraph;
	}

	/**
	 * @return TGraph symbol table belongs to.
	 */
	public Java5 getGraph() {
		return programGraph;
	}

	// --- type definitions
	// ---------------------------------------------------------------------------------------

	/**
	 * Holds vertices representing a type definition (class, interface etc.).
	 */
	private HashMap<String, Type> typeDefinitions = new HashMap<String, Type>();

	/**
	 * Adds a vertex representing a type definition to symbol table.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of type definition to add.
	 * @param typeVertex
	 *            Vertex representing type definition.
	 */
	public void addTypeDefinition(String fullyQualifiedName, Type typeVertex) {
		typeDefinitions.put(fullyQualifiedName, typeVertex);
	}

	/**
	 * Checks if a vertex representing type definition with given name is stored
	 * in symbol table.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of type definition to check for.
	 * @return true if vertex is stored in symbol table, false otherwise.
	 */
	public boolean hasTypeDefinition(String fullyQualifiedName) {
		return typeDefinitions.containsKey(fullyQualifiedName);
	}

	/**
	 * @param fullyQualifiedName
	 *            Fully qualified name of type definition to get.
	 * @return Vertex representing type definition.
	 */
	public Type getTypeDefinition(String fullyQualifiedName) {
		return typeDefinitions.get(fullyQualifiedName);
	}

	// --- packages (unique in graph)
	// ---------------------------------------------------------------------------------------

	/**
	 * Holds vertices representing a package definition.
	 */
	private HashMap<String, JavaPackage> javaPackages = new HashMap<String, JavaPackage>();

	/**
	 * Checks if a vertex representing package definition with given name is
	 * stored in symbol table.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of package definition to check for.
	 * @return true if vertex is stored in symbol table, false otherwise.
	 */
	public boolean hasJavaPackage(String fullyQualifiedName) {
		return javaPackages.containsKey(fullyQualifiedName);
	}

	/**
	 * Adds a vertex representing package definition to symbol table.
	 * 
	 * @param fullyQualifiedName
	 * @param javaPackageVertex
	 */
	public void addJavaPackage(String fullyQualifiedName,
			JavaPackage javaPackageVertex) {
		javaPackages.put(fullyQualifiedName, javaPackageVertex);
	}

	/**
	 * @param fullyQualifiedName
	 *            Fully qualified name of package definition to get.
	 * @return Vertex representing package definition given by fully qualified
	 *         name.
	 */
	public JavaPackage getJavaPackage(String fullyQualifiedName) {
		return javaPackages.get(fullyQualifiedName);
	}

	// --- integer constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private HashMap<String, IntegerConstant> integerConstants = new HashMap<String, IntegerConstant>();

	public void addIntegerConstant(String literal,
			IntegerConstant integerConstantVertex) {
		integerConstants.put(literal, integerConstantVertex);
	}

	public boolean hasIntegerConstant(String literal) {
		return integerConstants.containsKey(literal);
	}

	public IntegerConstant getIntegerConstant(String literal) {
		return integerConstants.get(literal);
	}

	// --- char constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private HashMap<String, CharConstant> charConstants = new HashMap<String, CharConstant>();

	public void addCharConstant(String literal, CharConstant charConstantVertex) {
		charConstants.put(literal, charConstantVertex);
	}

	public boolean hasCharConstant(String literal) {
		return charConstants.containsKey(literal);
	}

	public CharConstant getCharConstant(String literal) {
		return charConstants.get(literal);
	}

	// --- string constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private HashMap<String, StringConstant> stringConstants = new HashMap<String, StringConstant>();

	public void addStringConstant(String literal,
			StringConstant stringConstantVertex) {
		stringConstants.put(literal, stringConstantVertex);
	}

	public boolean hasStringConstant(String literal) {
		return stringConstants.containsKey(literal);
	}

	public StringConstant getStringConstant(String literal) {
		return stringConstants.get(literal);
	}

	// --- float constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private HashMap<String, FloatConstant> floatConstants = new HashMap<String, FloatConstant>();

	public void addFloatConstant(String literal,
			FloatConstant floatConstantVertex) {
		floatConstants.put(literal, floatConstantVertex);
	}

	public boolean hasFloatConstant(String literal) {
		return floatConstants.containsKey(literal);
	}

	public FloatConstant getFloatConstant(String literal) {
		return floatConstants.get(literal);
	}

	// --- double constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private HashMap<String, DoubleConstant> doubleConstants = new HashMap<String, DoubleConstant>();

	public void addDoubleConstant(String literal,
			DoubleConstant doubleConstantVertex) {
		doubleConstants.put(literal, doubleConstantVertex);
	}

	public boolean hasDoubleConstant(String literal) {
		return doubleConstants.containsKey(literal);
	}

	public DoubleConstant getDoubleConstant(String literal) {
		return doubleConstants.get(literal);
	}

	// --- long constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private HashMap<String, LongConstant> longConstants = new HashMap<String, LongConstant>();

	public void addLongConstant(String literal, LongConstant longConstantVertex) {
		longConstants.put(literal, longConstantVertex);
	}

	public boolean hasLongConstant(String literal) {
		return longConstants.containsKey(literal);
	}

	public LongConstant getLongConstant(String literal) {
		return longConstants.get(literal);
	}

	// --- boolean constants (unique in graph)
	// ---------------------------------------------------------------------------------------

	private BooleanConstant booleanTrueConstant = null;

	public void setBooleanTrueConstant(BooleanConstant booleanConstantVertex) {
		booleanTrueConstant = booleanConstantVertex;
	}

	public boolean hasBooleanTrueConstant() {
		if (booleanTrueConstant == null) {
			return false;
		}
		return true;
	}

	public BooleanConstant getBooleanTrueConstant() {
		return booleanTrueConstant;
	}

	private BooleanConstant booleanFalseConstant = null;

	public void setBooleanFalseConstant(BooleanConstant booleanConstantVertex) {
		booleanFalseConstant = booleanConstantVertex;
	}

	public boolean hasBooleanFalseConstant() {
		if (booleanFalseConstant == null) {
			return false;
		}
		return true;
	}

	public BooleanConstant getBooleanFalseConstant() {
		return booleanFalseConstant;
	}

	// ------ null expression (unique in graph) -------------------

	/**
	 * Holds the unique vertex in graph, which represents a null expression.
	 */
	private Null nullVertex;

	public boolean hasNull() {
		return nullVertex != null;
	}

	public void setNull(Null nullVertex) {
		this.nullVertex = nullVertex;
	}

	public Null getNull() {
		return nullVertex;
	}

	// ----- modifiers (unique in graph) ----------------------------

	private HashMap<Modifiers, Modifier> modifiers = new HashMap<Modifiers, Modifier>();

	public void addModifier(Modifiers type, Modifier modifierVertex) {
		modifiers.put(type, modifierVertex);
	}

	public boolean hasModifier(Modifiers type) {
		return modifiers.containsKey(type);
	}

	public Modifier getModifier(Modifiers type) {
		return modifiers.get(type);
	}

	// ---- labels (locally unique ) ------------------

	private HashMap<String, Label> labels;

	// ---- empty statement (unique in graph ) ------------------

	private EmptyStatement emptyStatementVertex;

	public boolean hasEmptyStatement() {
		if (emptyStatementVertex == null) {
			return false;
		}
		return true;
	}

	public void setEmptyStatement(EmptyStatement emptyStatementVertex) {
		this.emptyStatementVertex = emptyStatementVertex;
	}

	public EmptyStatement getEmptyStatement() {
		return emptyStatementVertex;
	}

	// --- scopes (for type resolving)
	// ---------------------------------------------------------------------------------------

	/**
	 * A structure holding all scope-to-parent-scope relationships. Key in the
	 * hashmap is the scope vertex, whereas the data for the key is the
	 * respective parent scope. This information only exists during treewalk of
	 * a file and is resetted with every new file that is parsed.
	 */
	private HashMap<Vertex, Vertex> parentScopes = new HashMap<Vertex, Vertex>();

	/**
	 * Adds scope hierarchy info.
	 * 
	 * @param currentScope
	 *            A vertex which represents an element that has its own scope.
	 * @param parentScope
	 *            The vertex representing the element with the parent scope of
	 *            the current.
	 */
	public void addScopeInfo(Vertex currentScope, Vertex parentScopeOfCurrent) {
		if (currentScope != null) {
			parentScopes.put(currentScope, parentScopeOfCurrent);
		}
	}

	/**
	 * Gets the parent scope of a scope.
	 * 
	 * @param currentScope
	 *            A vertex which represents an element that has its own scope.
	 * @return The vertex representing the element with the parent scope; null
	 *         if there is none or the element cannot be found.
	 */
	public Vertex getParentScope(Vertex currentScope) {
		if ((currentScope != null) && parentScopes.containsKey(currentScope)) {
			return parentScopes.get(currentScope);
		}
		return null;
	}

	// --- fields (locally visible)
	// ---------------------------------------------------------------------------------------

	/**
	 * A structure holding references to all found field declarations in their
	 * according scopes. Key vertex in the outer hashmap is the scope of the
	 * field declaration. Data for every key is a hashmap, where the key string
	 * is the name of the field. Data for every key in the inner hasmap is the
	 * reference to the according field declaration. This information only
	 * exists during treewalk of a file and is resetted with every new file that
	 * is parsed.
	 */
	private HashMap<Vertex, HashMap<String, FieldDeclaration>> declaredLocalFieldsInScopes = new HashMap<Vertex, HashMap<String, FieldDeclaration>>();

	/**
	 * Adds a local field declaration to the current class (or interface, enum,
	 * etc.) and scope.
	 * 
	 * @param declaredField
	 *            The vertex representing the field.
	 * @param scopeOfField
	 *            The scope the field is declared in.
	 */
	public void addLocalFieldDeclaration(FieldDeclaration declaredField,
			Vertex scopeOfField) {
		String nameOfField;
		if (declaredField instanceof VariableDeclaration) {
			nameOfField = ((Identifier) ((VariableDeclaration) declaredField)
					.getFirstIsVariableNameOf(EdgeDirection.IN).getAlpha())
					.get_name();
		} else {
			nameOfField = ((Identifier) ((ParameterDeclaration) declaredField)
					.getFirstIsParameterNameOf(EdgeDirection.IN).getAlpha())
					.get_name();
		}
		if (declaredLocalFieldsInScopes.containsKey(scopeOfField)) {
			declaredLocalFieldsInScopes.get(scopeOfField).put(nameOfField,
					declaredField);
		} else {
			HashMap<String, FieldDeclaration> fieldsInCurrentScope = new HashMap<String, FieldDeclaration>();
			fieldsInCurrentScope.put(nameOfField, declaredField);
			declaredLocalFieldsInScopes.put(scopeOfField, fieldsInCurrentScope);
		}
	}

	/**
	 * Gets a local field declaration from the current class (or interface,
	 * enum, etc.) and scope. If there is none in the current scope, parent
	 * scopes are being searched recursively.
	 * 
	 * @param fieldName
	 *            The name of the field.
	 * @param scopeToSearch
	 *            The initial scope to search in.
	 * @return The vertex representing the field declaration; null if there is
	 *         none or the element cannot be found.
	 */
	public FieldDeclaration getFieldDeclarationFromScope(String fieldName,
			Vertex scopeToSearch) {
		if ((scopeToSearch == null) || (fieldName == null)) {
			return null;
		}
		HashMap<String, FieldDeclaration> declaredFieldsInSameScope = declaredLocalFieldsInScopes
				.get(scopeToSearch);
		if (declaredFieldsInSameScope == null) {
			return getFieldDeclarationFromScope(fieldName,
					getParentScope(scopeToSearch));
		}
		if (declaredFieldsInSameScope.containsKey(fieldName)) {
			return declaredFieldsInSameScope.get(fieldName);
		}
		return getFieldDeclarationFromScope(fieldName,
				getParentScope(scopeToSearch));
	}

	// --- fields (globally visible)
	// ---------------------------------------------------------------------------------------

	/**
	 * Holds all global variable declarations from the current class (or
	 * interface, enum, etc.). This information only exists during treewalk of a
	 * file and is resetted with every new file that is parsed.
	 */
	private HashMap<String, VariableDeclaration> declaredGlobalVariables = new HashMap<String, VariableDeclaration>();

	/**
	 * Adds a global variable declaration from the current class (or interface,
	 * enum, etc.).
	 * 
	 * @param nameOfVariable
	 *            The name of the variable.
	 * @param variableDeclarationVertex
	 *            The vertex representing the variable declaration.
	 */
	public void addGlobalVariableDeclaration(String nameOfVariable,
			VariableDeclaration variableDeclarationVertex) {
		if ((nameOfVariable != null) && (variableDeclarationVertex != null)) {
			declaredGlobalVariables.put(nameOfVariable,
					variableDeclarationVertex);
		}
	}

	/**
	 * Gets a global variable declaration from the current class (or interface,
	 * enum, etc.).
	 * 
	 * @param nameOfVariable
	 *            The name of the variable.
	 * @return The vertex representing the variable declaration; null if there
	 *         is none or the element cannot be found.
	 */
	public VariableDeclaration getGlobalVariableDeclaration(
			String nameOfVariable) {
		if ((nameOfVariable != null)
				&& declaredGlobalVariables.containsKey(nameOfVariable)) {
			return declaredGlobalVariables.get(nameOfVariable);
		}
		return null;
	}

	// --- field access (to resolve)
	// ---------------------------------------------------------------------------------------

	/**
	 * An arraylist of field access vertices containing those vertices that have
	 * not yet been linked to the according definition.
	 */
	private ArrayList<FieldAccess> accessOfUndeclaredInternalVariables = new ArrayList<FieldAccess>();

	/**
	 * Adds an element to the list of field access vertices.
	 * 
	 * @param accessOfVariable
	 *            The field access element to be added.
	 */
	public void addAccessOfUndeclaredInternalVariable(
			FieldAccess accessOfVariable) {
		if (accessOfVariable != null) {
			accessOfUndeclaredInternalVariables.add(accessOfVariable);
		}
	}

	/**
	 * Get the amount of elements in the list of field access vertices.
	 * 
	 * @return The amount of elements.
	 */
	public int getAmountOfAccessesOfUndeclaredInternalVariables() {
		return accessOfUndeclaredInternalVariables.size();
	}

	/**
	 * Gets an element from the list of field access vertices.
	 * 
	 * @param index
	 *            The index of the element to get from the list.
	 * @return The field access vertex; null if the index is invalid.
	 */
	public FieldAccess getAccessOfUndeclaredInternalVariable(int index) {
		if ((index >= 0)
				&& (index < accessOfUndeclaredInternalVariables.size())) {
			return accessOfUndeclaredInternalVariables.get(index);
		}
		return null;
	}

	/**
	 * Removes an element from the list of field access vertices.
	 * 
	 * @param index
	 *            The index of the element to remove from the list.
	 */
	public void removeAccessOfUndeclaredInternalVariable(int index) {
		if ((index >= 0)
				&& (index < accessOfUndeclaredInternalVariables.size())) {
			accessOfUndeclaredInternalVariables.remove(index);
		}
	}

	/**
	 * A structure holding references to all found variable declarations. Key
	 * string in the outer hashmap is the fully qualified name of the class.
	 * Data for every key is a hashmap, where the key string is the name of the
	 * variable. Data for every key in the inner hasmap is the reference to the
	 * according variable declaration.
	 */
	private HashMap<String, HashMap<String, VariableDeclaration>> declaredVariablesInClasses = new HashMap<String, HashMap<String, VariableDeclaration>>();

	/**
	 * Adds a variable declaration to the symbol table.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the class which contains the
	 *            variable.
	 * @param variableDeclaration
	 *            The vertex of the method declaration.
	 */
	public void addVariableDeclaration(
			String fullyQualifiedNameOfContainingType,
			VariableDeclaration variableDeclaration) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (variableDeclaration != null)) {
			String variableName = ((Identifier) variableDeclaration
					.getFirstIsVariableNameOf(EdgeDirection.IN).getAlpha())
					.get_name();
			if (declaredVariablesInClasses
					.containsKey(fullyQualifiedNameOfContainingType)) {
				declaredVariablesInClasses.get(
						fullyQualifiedNameOfContainingType).put(variableName,
						variableDeclaration);
			} else {
				HashMap<String, VariableDeclaration> declaredVariablesInCurrentClass = new HashMap<String, VariableDeclaration>();
				declaredVariablesInCurrentClass.put(variableName,
						variableDeclaration);
				declaredVariablesInClasses.put(
						fullyQualifiedNameOfContainingType,
						declaredVariablesInCurrentClass);
			}
		}
	}

	/**
	 * Gets a variable declaration from the symbol table.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the class which contains the
	 *            variable.
	 * @param variableName
	 *            The name of the variable in the class.
	 * @return The variable; null if there is none or the class cannot be found.
	 */
	public VariableDeclaration getVariableDeclaration(
			String fullyQualifiedNameOfContainingType, String variableName) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (variableName != null)
				&& declaredVariablesInClasses
						.containsKey(fullyQualifiedNameOfContainingType)
				&& declaredVariablesInClasses.get(
						fullyQualifiedNameOfContainingType).containsKey(
						variableName)) {
			return declaredVariablesInClasses.get(
					fullyQualifiedNameOfContainingType).get(variableName);
		}
		return null;
	}

	/**
	 * A structure holding references to all found enum constant declarations in
	 * the current file. Key string in the hashmap is the name of the constant.
	 * Data for every key is the reference to the according enum constant.
	 */
	private HashMap<String, EnumConstant> currentEnumConstants = new HashMap<String, EnumConstant>();

	/**
	 * A structure holding references to all found enum constant declarations.
	 * Key string in the outer hashmap is the fully qualified name of the type
	 * (should be an enum) containing the constant. Data for every key is a
	 * hashmap, where the key string is the name of the constant. Data for every
	 * key in the inner hasmap is the reference to the according enum constant.
	 */
	private HashMap<String, HashMap<String, EnumConstant>> enumConstants = new HashMap<String, HashMap<String, EnumConstant>>();

	/**
	 * Adds an enum constant to the symbol table.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the enum which contains the
	 *            constant.
	 * @param enumConstantToAdd
	 *            The vertex of the enum constant.
	 */
	public void addEnumConstant(String fullyQualifiedNameOfContainingType,
			EnumConstant enumConstantToAdd) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (enumConstantToAdd != null)) {
			String constantName = ((Identifier) (enumConstantToAdd
					.getFirstIsEnumConstantNameOf(EdgeDirection.IN).getAlpha()))
					.get_name();
			currentEnumConstants.put(constantName, enumConstantToAdd);
			if (enumConstants.containsKey(fullyQualifiedNameOfContainingType)) {
				enumConstants.get(fullyQualifiedNameOfContainingType).put(
						constantName, enumConstantToAdd);
			} else {
				HashMap<String, EnumConstant> currentInnerMap = new HashMap<String, EnumConstant>();
				currentInnerMap.put(constantName, enumConstantToAdd);
				enumConstants.put(fullyQualifiedNameOfContainingType,
						currentInnerMap);
			}
		}
	}

	/**
	 * Gets an enum constant from the symbol table.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the enum which contains the
	 *            constant.
	 * @param constantName
	 *            The name of the constant in the enum.
	 * @return The constant; null if there is none or the enum cannot be found.
	 */
	public EnumConstant getEnumConstant(
			String fullyQualifiedNameOfContainingType, String constantName) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (constantName != null)
				&& enumConstants
						.containsKey(fullyQualifiedNameOfContainingType)
				&& enumConstants.get(fullyQualifiedNameOfContainingType)
						.containsKey(constantName)) {
			return enumConstants.get(fullyQualifiedNameOfContainingType).get(
					constantName);
		}
		return null;
	}

	/**
	 * Gets an enum constant from the currently parsed file.
	 * 
	 * @param constantName
	 *            The name of the constant in the enum.
	 * @return The constant; null if there is none with the given name.
	 */
	public EnumConstant getEnumConstantFromCurrent(String constantName) {
		if ((constantName != null)
				&& currentEnumConstants.containsKey(constantName)) {
			return currentEnumConstants.get(constantName);
		}
		return null;
	}

	/**
	 * Holds references to all field access vertices. Data for every key denotes
	 * if the field has already been processed by the global resolver.
	 */
	private HashMap<FieldAccess, Boolean> fieldAccessMap = new HashMap<FieldAccess, Boolean>();

	/**
	 * @return The total amount of field access vertices stored in the symbol
	 *         table.
	 */
	public int amountOfFieldAccesses() {
		return fieldAccessMap.size();
	}

	/**
	 * Adds a field access to the symbol table.
	 * 
	 * @param fieldAccess
	 *            The field access vertex.
	 */
	public void addFieldAccess(FieldAccess fieldAccess) {
		fieldAccessMap.put(fieldAccess, Boolean.valueOf(false));
	}
	
	public void removeFieldAccess(FieldAccess fieldAccess){
		this.fieldAccessMap.remove(fieldAccess);
		this.scopesOfFieldAccessVertices.remove(fieldAccess);
	}

	/**
	 * @return All field accesses that have not been resolved locally.
	 */
	public Set<FieldAccess> getFieldAccessVertices() {
		return fieldAccessMap.keySet();
	}

	/**
	 * Sets a field access vertex as processed by the global resolver.
	 * 
	 * @param fieldAccess
	 *            The field access vertex.
	 */
	public void setFieldAccessProcessed(FieldAccess fieldAccess) {
		if ((fieldAccess != null) && (fieldAccessMap.containsKey(fieldAccess))) {
			fieldAccessMap.put(fieldAccess, Boolean.valueOf(true));
		}
	}

	/**
	 * Checks if a field access vertex was already processed by the global
	 * resolver.
	 * 
	 * @param fieldAccess
	 *            The field access vertex.
	 * @return boolean wrapper class with true if processed, false if not. null
	 *         if the field access is not within the symbol table.
	 */
	public Boolean isProcessedFieldAccess(FieldAccess fieldAccess) {
		if ((fieldAccess != null) && (fieldAccessMap.containsKey(fieldAccess))) {
			return fieldAccessMap.get(fieldAccess);
		}
		return null;
	}
	
	

	/**
	 * A structure holding references to the scopes of field access vertices.
	 * The data (Vertex) for every key (FieldAccess) is the scope of the field
	 * access.
	 */
	private HashMap<FieldAccess, Vertex> scopesOfFieldAccessVertices = new HashMap<FieldAccess, Vertex>();

	/**
	 * Adds field access / scope pair to the symbol table.
	 * 
	 * @param fieldAccessVertex
	 *            The field access vertex.
	 * @param scopeOfFieldAccess
	 *            The scope of the field access.
	 */
	public void addScopeOfFieldAccess(FieldAccess fieldAccessVertex,
			Vertex scopeOfFieldAccess) {
		if ((fieldAccessVertex != null) && (scopeOfFieldAccess != null)) {
			scopesOfFieldAccessVertices.put(fieldAccessVertex,
					scopeOfFieldAccess);
		}
	}

	/**
	 * Gets the scope of a field access.
	 * 
	 * @param fieldAccessVertex
	 *            The field access.
	 * @return The scope of the field access; null if it cannot be found.
	 */
	public Vertex getScopeOfFieldAccess(FieldAccess fieldAccessVertex) {
		if ((fieldAccessVertex != null)
				&& (scopesOfFieldAccessVertices.containsKey(fieldAccessVertex))) {
			return scopesOfFieldAccessVertices.get(fieldAccessVertex);
		}
		return null;
	}

	/**
	 * Variable required for counting the amount of field access vertices that
	 * could not be resolved.
	 */
	private int amountOfUnresolvedFieldAccesses = 0;

	/**
	 * Increases the amount of unresolvable field access vertices by one.
	 */
	public void increaseAmountOfUnresolvedFieldAccesses() {
		amountOfUnresolvedFieldAccesses++;
	}

	/**
	 * @return The amount of unresolvable field access vertices.
	 */
	public int getAmountOfUnresolvedFieldAccesses() {
		return amountOfUnresolvedFieldAccesses;
	}

	/**
	 * Variable required for counting the amount of field access vertices
	 * treated by the resolver.
	 */
	private int amountOfFieldAccessesTreatedByResolver = 0;

	/**
	 * Increases the amount of field access vertices treated by the resolver.
	 */
	public void increaseAmountOfFieldAccessesTreatedByResolver() {
		amountOfFieldAccessesTreatedByResolver++;
	}

	/**
	 * @return The amount of field access vertices treated by the resolver.
	 */
	public int getAmountOfFieldAccessesTreatedByResolver() {
		return amountOfFieldAccessesTreatedByResolver;
	}

	// --- methods
	// ---------------------------------------------------------------------------------------

	/**
	 * A structure holding references to all found method declarations /
	 * definitions Key string in the outer hashmap is the fully qualified name
	 * of the class. Data for every key is a hashmap, where the key string is
	 * the name of the method. Data for every key in the inner hasmap is an
	 * arraylist of method declarations / definitions, as there may be
	 * overloaded methods in every class.
	 */
	private HashMap<String, HashMap<String, ArrayList<MethodDeclaration>>> declaredMethodsInClasses = new HashMap<String, HashMap<String, ArrayList<MethodDeclaration>>>();

	/**
	 * Adds a method declaration or definition to the symbol table.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the class which contains the
	 *            method.
	 * @param constructorDefinition
	 *            The vertex of the method declaration (or definition).
	 */
	public void addMethodDeclaration(String fullyQualifiedNameOfContainingType,
			MethodDeclaration methodDeclaration) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (methodDeclaration != null)) {
			String methodName = ((Identifier) methodDeclaration
					.getFirstIsNameOfMethod(EdgeDirection.IN).getAlpha())
					.get_name();
			if (declaredMethodsInClasses
					.containsKey(fullyQualifiedNameOfContainingType)) {
				if (declaredMethodsInClasses.get(
						fullyQualifiedNameOfContainingType).containsKey(
						methodName)) {
					declaredMethodsInClasses.get(
							fullyQualifiedNameOfContainingType).get(methodName)
							.add(methodDeclaration);
				} else {
					ArrayList<MethodDeclaration> methodsList = new ArrayList<MethodDeclaration>();
					methodsList.add(methodDeclaration);
					declaredMethodsInClasses.get(
							fullyQualifiedNameOfContainingType).put(methodName,
							methodsList);
				}
			} else {
				ArrayList<MethodDeclaration> methodsList = new ArrayList<MethodDeclaration>();
				methodsList.add(methodDeclaration);
				HashMap<String, ArrayList<MethodDeclaration>> methodsHashMap = new HashMap<String, ArrayList<MethodDeclaration>>();
				methodsHashMap.put(methodName, methodsList);
				declaredMethodsInClasses.put(
						fullyQualifiedNameOfContainingType, methodsHashMap);
			}
		}
	}

	/**
	 * Gets the methods (declarations as well as definitions) with a certain
	 * name defined in a class.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the class which contains the
	 *            method.
	 * @param methodName
	 *            The name of the method in the class.
	 * @return An arraylist of method declarations; null if there are none or
	 *         the class cannot be found.
	 */
	public ArrayList<MethodDeclaration> getMethodDeclarations(
			String fullyQualifiedNameOfContainingType, String methodName) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (methodName != null)
				&& declaredMethodsInClasses
						.containsKey(fullyQualifiedNameOfContainingType)
				&& declaredMethodsInClasses.get(
						fullyQualifiedNameOfContainingType).containsKey(
						methodName)) {
			return declaredMethodsInClasses.get(
					fullyQualifiedNameOfContainingType).get(methodName);
		}
		return null;
	}

	/**
	 * A structure holding references to all found constructor definitions Key
	 * string is the fully qualified name of the class. Data for every key is an
	 * arraylist of constructor definitions, as there may be overloaded
	 * constructors in every class.
	 */
	private HashMap<String, ArrayList<ConstructorDefinition>> definedConstructorsInClasses = new HashMap<String, ArrayList<ConstructorDefinition>>();

	/**
	 * Adds a constructor definition to the symbol table.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the class which contains the
	 *            constructor.
	 * @param constructorDefinition
	 *            The vertex of the constructor definition.
	 */
	public void addConstructorDefinition(
			String fullyQualifiedNameOfContainingType,
			ConstructorDefinition constructorDefinition) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& (constructorDefinition != null)) {
			if (definedConstructorsInClasses
					.containsKey(fullyQualifiedNameOfContainingType)) {
				definedConstructorsInClasses.get(
						fullyQualifiedNameOfContainingType).add(
						constructorDefinition);
			} else {
				ArrayList<ConstructorDefinition> constructorsInCurrentClass = new ArrayList<ConstructorDefinition>();
				constructorsInCurrentClass.add(constructorDefinition);
				definedConstructorsInClasses.put(
						fullyQualifiedNameOfContainingType,
						constructorsInCurrentClass);
			}
		}
	}

	/**
	 * Gets the Constructors (explicitely) defined in a class.
	 * 
	 * @param fullyQualifiedNameOfContainingType
	 *            The fully qualified Name of the class which contains the
	 *            constructor.
	 * @return An arraylist of constructor definitions; null if there are none
	 *         or the class cannot be found.
	 */
	public ArrayList<ConstructorDefinition> getConstructorDefinitions(
			String fullyQualifiedNameOfContainingType) {
		if ((fullyQualifiedNameOfContainingType != null)
				&& definedConstructorsInClasses
						.containsKey(fullyQualifiedNameOfContainingType)) {
			return definedConstructorsInClasses
					.get(fullyQualifiedNameOfContainingType);
		}
		return null;
	}

	/**
	 * Holds references to all method invocation vertices. Data for every key
	 * denotes if the field has already been processed by the resolver.
	 */
	private HashMap<MethodInvocation, Boolean> methodInvocationMap = new HashMap<MethodInvocation, Boolean>();

	/**
	 * @return The total amount of method invocations stored in the symbol
	 *         table.
	 */
	public int amountOfMethodInvocations() {
		if (methodInvocationMap == null) {
			return 0;
		}
		return methodInvocationMap.size();
	}

	/**
	 * Adds a method invocation to the symbol table.
	 * 
	 * @param methodInvocation
	 *            The method invocation vertex.
	 * @param scopeOfMethodInvocation
	 *            The scope of the method invocation.
	 */
	public void addMethodInvocation(MethodInvocation methodInvocation,
			Vertex scopeOfMethodInvocation) {
		if (methodInvocation != null) {
			methodInvocationMap.put(methodInvocation, Boolean.valueOf(false));
			if (scopeOfMethodInvocation != null) {
				scopesOfMethodInvocations.put(methodInvocation,
						scopeOfMethodInvocation);
			}
		}
	}

	/**
	 * @return All method invocations which are not connected to the structure
	 *         representing their declaration.
	 */
	public Set<MethodInvocation> getMethodInvocations() {
		return methodInvocationMap.keySet();
	}

	/**
	 * Sets a method invocation vertex as processed by the global resolver.
	 * 
	 * @param methodInvocation
	 *            The method invocation vertex.
	 */
	public void setMethodInvocationProcessed(MethodInvocation methodInvocation) {
		if ((methodInvocation != null)
				&& (methodInvocationMap.containsKey(methodInvocation))) {
			methodInvocationMap.put(methodInvocation, Boolean.valueOf(true));
		}
	}

	/**
	 * Checks if a method invocation vertex was already processed by the global
	 * resolver.
	 * 
	 * @param methodInvocation
	 *            The method invocation vertex.
	 * @return boolean wrapper class with true if processed, false if not. null
	 *         if the method invocation is not within the symbol table.
	 */
	public Boolean isProcessedMethodInvocation(MethodInvocation methodInvocation) {
		if ((methodInvocation != null)
				&& (methodInvocationMap.containsKey(methodInvocation))) {
			return methodInvocationMap.get(methodInvocation);
		}
		return null;
	}

	/**
	 * A structure holding references to the scopes of method invocations. The
	 * data (Vertex) for every key (MethodInvocation) is the scope of the method
	 * invocation.
	 */
	private HashMap<MethodInvocation, Vertex> scopesOfMethodInvocations = new HashMap<MethodInvocation, Vertex>();

	/**
	 * Gets the scope of a method invocation.
	 * 
	 * @param methodInvocation
	 *            The method invocation.
	 * @return The scope of the method invocation; null if it cannot be found.
	 */
	public Vertex getScopeOfMethodInvocation(MethodInvocation methodInvocation) {
		if ((methodInvocation != null)
				&& (scopesOfMethodInvocations.containsKey(methodInvocation))) {
			return scopesOfMethodInvocations.get(methodInvocation);
		}
		return null;
	}

	/**
	 * Variable required for counting the amount of method invocation vertices
	 * that could not be resolved.
	 */
	private int amountOfUnresolvedMethodInvocations = 0;

	/**
	 * Increases the amount of unresolvable method invocation vertices by one.
	 */
	public void increaseAmountOfUnresolvedMethodInvocations() {
		amountOfUnresolvedMethodInvocations++;
	}

	/**
	 * @return The amount of unresolvable method invocation vertices.
	 */
	public int getAmountOfUnresolvedMethodInvocations() {
		return amountOfUnresolvedMethodInvocations;
	}

	/**
	 * Variable required for counting the amount of method invocation vertices
	 * treated by the resolver.
	 */
	private int amountOfMethodInvocationsTreatedByResolver = 0;

	/**
	 * Increases the amount of method invocation vertices treated by the
	 * resolver.
	 */
	public void increaseAmountOfMethodInvocationsTreatedByResolver() {
		amountOfMethodInvocationsTreatedByResolver++;
	}

	/**
	 * @return The amount of method invocation vertices treated by the resolver.
	 */
	public int getAmountOfMethodInvocationsTreatedByResolver() {
		return amountOfMethodInvocationsTreatedByResolver;
	}

	// --- first type definition in currently parsed file
	// ------------------------------------------------

	/**
	 * Reference to vertex representing first type definition of currently
	 * parsed file.
	 */
	private Type supremeTypeInFileVertex = null;

	/**
	 * Sets vertex representing first type definition of currently parsed file.
	 * 
	 * @param typeVertex
	 *            Vertex representing type definition.
	 */
	public void setSupremeTypeInFile(Type typeVertex) {
		supremeTypeInFileVertex = typeVertex;
	}

	/**
	 * @return Vertex representing first type definition of currently parsed
	 *         file.
	 */
	public Type getSupremeTypeInFile() {
		return supremeTypeInFileVertex;
	}

	// ----- type parameters in currently parsed file
	// -------------------------------------------------------

	private HashMap<Vertex, HashMap<String, TypeParameterDeclaration>> typeParameters;

	public void addTypeParameter(Vertex scope, String name,
			TypeParameterDeclaration typeParameterDeclarationVertex) {
		if (typeParameters == null) {
			typeParameters = new HashMap<Vertex, HashMap<String, TypeParameterDeclaration>>();
		}
		if (typeParameters.containsKey(scope)) {
			HashMap<String, TypeParameterDeclaration> temp = typeParameters
					.get(scope);
			temp.put(name, typeParameterDeclarationVertex);
		} else {
			HashMap<String, TypeParameterDeclaration> newTypeParameters = new HashMap<String, TypeParameterDeclaration>();
			newTypeParameters.put(name, typeParameterDeclarationVertex);
			typeParameters.put(scope, newTypeParameters);
		}
	}

	public boolean hasTypeParameters() {
		if (typeParameters == null) {
			return false;
		}
		if (typeParameters.isEmpty()) {
			return false;
		}
		Set<Vertex> keySet = typeParameters.keySet();
		Iterator<Vertex> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			Vertex scope = iterator.next();
			HashMap<String, TypeParameterDeclaration> temp = typeParameters
					.get(scope);
			if (!temp.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTypeParameter(Vertex scope, String name) {
		if (typeParameters == null) {
			return false;
		}
		if (typeParameters.containsKey(scope)) {
			HashMap<String, TypeParameterDeclaration> typeParametersInScope = typeParameters
					.get(scope);
			if ((typeParametersInScope != null)
					&& typeParametersInScope.containsKey(name)) {
				return true;
			}
		}
		return false;
	}

	public TypeParameterDeclaration getTypeParameter(Vertex scope, String name) {
		if (typeParameters == null) {
			return null;
		}
		HashMap<String, TypeParameterDeclaration> typeParametersInScope = typeParameters
				.get(scope);
		return typeParametersInScope.get(name);
	}

	// ----- nested types in currently parsed file
	// ---------------------------------------------------------------

	/**
	 * Holds references to nested types in a file, collected by scope and fully
	 * qualified name.
	 */
	private HashMap<String, Type> nestedTypes;

	public void addNestedClassDefinition(String fullyQualifiedName,
			ClassDefinition classDefinitionVertex) {
		addNestedType(fullyQualifiedName, classDefinitionVertex);
		// Check if it is protected, so it is accessible in same package.
		if (searchProtected(classDefinitionVertex
				.getIsModifierOfClassIncidences(EdgeDirection.IN))) {
			addTypeDefinition(fullyQualifiedName, classDefinitionVertex);
			//
			// Iterable< EdgeVertexPair< ? extends IsModifierOfClass, ? extends
			// Vertex > > isModifierOfClassIncidences =
			// classDefinitionVertex.getIsModifierOfClassIncidences(
			// EdgeDirection.IN );
			// ArrayList< IsModifierOf> isModifierOfEdges = new ArrayList<
			// IsModifierOf >();
			// for( EdgeVertexPair< ? extends IsModifierOfClass, ? extends
			// Vertex >
			// pair : isModifierOfClassIncidences )
			// isModifierOfEdges.add( ( IsModifierOf )pair.getEdge() );
			// if( searchProtected( isModifierOfEdges ) ) addTypeDefinition(
			// fullyQualifiedName, classDefinitionVertex );
		}
	}

	public void addNestedInterfaceDefinition(String fullyQualifiedName,
			InterfaceDefinition interfaceDefinitionVertex) {
		addNestedType(fullyQualifiedName, interfaceDefinitionVertex);
		// Check if it is protected, so it is accessible in same package.
		if (searchProtected(interfaceDefinitionVertex
				.getIsModifierOfInterfaceIncidences(EdgeDirection.IN))) {
			addTypeDefinition(fullyQualifiedName, interfaceDefinitionVertex);
		}

		// Iterable< EdgeVertexPair< ? extends IsModifierOfInterface, ? extends
		// Vertex > > isModifierOfInterfaceIncidences =
		// interfaceDefinitionVertex.getIsModifierOfInterfaceIncidences(
		// EdgeDirection.IN );
		// ArrayList< IsModifierOf> isModifierOfEdges = new ArrayList<
		// IsModifierOf >();
		// for( EdgeVertexPair< ? extends IsModifierOfInterface, ? extends
		// Vertex > pair : isModifierOfInterfaceIncidences )
		// isModifierOfEdges.add( ( IsModifierOf )pair.getEdge() );
		// if( searchProtected( isModifierOfEdges ) ) addTypeDefinition(
		// fullyQualifiedName, interfaceDefinitionVertex );
	}

	public void addNestedAnnotationDefinition(String fullyQualifiedName,
			AnnotationDefinition annotationDefinitionVertex) {
		addNestedType(fullyQualifiedName, annotationDefinitionVertex);
		// Check if it is protected, so it is accessible in same package.
		if (searchProtected(annotationDefinitionVertex
				.getIsModifierOfAnnotationIncidences(EdgeDirection.IN))) {
			addTypeDefinition(fullyQualifiedName, annotationDefinitionVertex);
		}

		// Iterable< EdgeVertexPair< ? extends IsModifierOfAnnotation, ? extends
		// Vertex > > isModifierOfAnnotationIncidences =
		// annotationDefinitionVertex.getIsModifierOfAnnotationIncidences(
		// EdgeDirection.IN );
		// ArrayList< IsModifierOf> isModifierOfEdges = new ArrayList<
		// IsModifierOf >();
		// for( EdgeVertexPair< ? extends IsModifierOfAnnotation, ? extends
		// Vertex > pair : isModifierOfAnnotationIncidences )
		// isModifierOfEdges.add( ( IsModifierOf )pair.getEdge() );
		// if( searchProtected( isModifierOfEdges ) ) addTypeDefinition(
		// fullyQualifiedName, annotationDefinitionVertex );
	}

	public void addNestedEnumDefinition(String fullyQualifiedName,
			EnumDefinition enumDefinitionVertex) {
		addNestedType(fullyQualifiedName, enumDefinitionVertex);
		// Check if it is protected, so it is accessible in same package.
		if (searchProtected(enumDefinitionVertex
				.getIsModifierOfEnumIncidences(EdgeDirection.IN))) {
			addTypeDefinition(fullyQualifiedName, enumDefinitionVertex);
		}

		// Iterable< EdgeVertexPair< ? extends IsModifierOfEnum, ? extends
		// Vertex > > isModifierOfEnumIncidences =
		// enumDefinitionVertex.getIsModifierOfEnumIncidences( EdgeDirection.IN
		// );
		// ArrayList< IsModifierOf> isModifierOfEdges = new ArrayList<
		// IsModifierOf >();
		// for( EdgeVertexPair< ? extends IsModifierOfEnum, ? extends Vertex >
		// pair : isModifierOfEnumIncidences )
		// isModifierOfEdges.add( ( IsModifierOf )pair.getEdge() );
		// if( searchProtected( isModifierOfEdges ) ) addTypeDefinition(
		// fullyQualifiedName, enumDefinitionVertex );
	}

	/**
	 * Searches for modifier protected in a given list.
	 * 
	 * @param isModifierOfEdges
	 *            List of edges attached to a vertex representing modifiers.
	 * @return true if no visibility modifier or at least modifier protected was
	 *         found, otherwise false.
	 */
	private boolean searchProtected(
			Iterable<? extends IsModifierOf> isModifierOfEdges) {
		for (IsModifierOf isModifierOfEdge : isModifierOfEdges) {
			Modifier modifierVertex = (Modifier) isModifierOfEdge.getAlpha();
			Modifiers modifierType = modifierVertex.get_type();
			if (modifierType == Modifiers.PROTECTED) {
				return true;
			}
			if (modifierType == Modifiers.PRIVATE) {
				return false; // It cannot be public because it is nested.
			}
		}
		return true; // No visibility specified, so it is protected.
	}

	/**
	 * Adds a nested type to symbol table.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of type.
	 * @param typeVertex
	 *            Vertex representing nested type definition.
	 */
	private void addNestedType(String fullyQualifiedName, Type typeVertex) {
		if (nestedTypes == null) {
			nestedTypes = new HashMap<String, Type>();
		}
		nestedTypes.put(fullyQualifiedName, typeVertex);
	}

	/**
	 * @return true if symbol table knows at leastone nested type, false
	 *         otherwise.
	 */
	public boolean hasNestedTypes() {
		if (nestedTypes == null) {
			return false;
		}
		return !nestedTypes.isEmpty();
	}

	/**
	 * @param fullyQualifiedName
	 *            A fully qualified name of a type.
	 * @return true if symbol table knows the nested type given by fully
	 *         qualified name.
	 */
	public boolean hasNestedType(String fullyQualifiedName) {
		if (nestedTypes == null) {
			return false;
		}
		return nestedTypes.containsKey(fullyQualifiedName);
	}

	/**
	 * @param fullyQualifiedName
	 *            Fuly qualified name of nested typ definition to get.
	 * @return Vertex representing definition of nested type, null if none
	 *         exists for given name.
	 */
	public Type getNestedType(String fullyQualifiedName) {
		if (hasNestedType(fullyQualifiedName)) {
			return nestedTypes.get(fullyQualifiedName);
		}
		return null;
	}

	// ----- type specifications in currently parsed file
	// ------------------------------------------------

	/**
	 * Holds vertices respresenting type specifications of the currently parsed
	 * file. Before next file is parsed vertices have to be put to collection
	 * unresolvedTypeSpecifications.
	 */
	private HashMap<Vertex, ArrayList<QualifiedType>> unresolvedTypeSpecificationsInCurrentlyParsedFile = new HashMap<Vertex, ArrayList<QualifiedType>>();

	/**
	 * @return true if symbol table stores at least one type specififcation.
	 */
	public boolean hasUnresolvedTypeSpecificationsInCurrentlyParsedFile() {
		return !unresolvedTypeSpecificationsInCurrentlyParsedFile.isEmpty();
	}

	/**
	 * Adds a vertex representing a type specification of curently parsed file
	 * to symbol table.
	 * 
	 * @param qualifiedTypeVertex
	 *            A vertex representing a type specification.
	 */
	public void addUnresolvedTypeSpecification(Vertex scope,QualifiedType qualifiedTypeVertex) {
		if (!unresolvedTypeSpecificationsInCurrentlyParsedFile.containsKey(scope)) {
			ArrayList<QualifiedType> qualifiedTypes = new ArrayList<QualifiedType>();
			qualifiedTypes.add(qualifiedTypeVertex);
			unresolvedTypeSpecificationsInCurrentlyParsedFile.put(scope,
					qualifiedTypes);
		} else {
			ArrayList<QualifiedType> qualifiedTypes = unresolvedTypeSpecificationsInCurrentlyParsedFile
					.get(scope);
			qualifiedTypes.add(qualifiedTypeVertex);
		}
		amountOfUnresolvedTypeSpecifications++;
	}

	public Set<Vertex> getScopesOfUnresolvedTypeSpecificationsInCurrentlyParsedFile() {
		return unresolvedTypeSpecificationsInCurrentlyParsedFile.keySet();
	}

	public ArrayList<QualifiedType> getUnresolvedTypeSpecificationsInCurrentlyParsedFile(
			Vertex scope) {
		return unresolvedTypeSpecificationsInCurrentlyParsedFile.get(scope);
	}

	public void putUnresolvedTypeSpecificationsInCurrentlyParsedFileToGlobalOnes() {
		putToUnresolvedTypeSpecifications(unresolvedTypeSpecificationsInCurrentlyParsedFile);
	}

	// ----- type specifications which could not be resolved locally thus are
	// saved for a global approach -----

	/**
	 * Collects vertices representing a type specification which could not be
	 * resolved locally.
	 */
	public HashMap<Vertex, ArrayList<QualifiedType>> unresolvedTypeSpecifications = new HashMap<Vertex, ArrayList<QualifiedType>>();

	public boolean hasUnresolvedTypeSpecifications() {
		// if( unresolvedTypeSpecifications == null ) return false;
		Set<Vertex> keySet = unresolvedTypeSpecifications.keySet();
		Iterator<Vertex> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			Vertex scope = iterator.next();
			ArrayList<QualifiedType> qualifiedTypes = unresolvedTypeSpecifications
					.get(scope);
			if (!qualifiedTypes.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Counts unresolved type specifications.
	 */
	private int amountOfUnresolvedTypeSpecifications = 0;

	/**
	 * @return Amount of unresolved type specifications in symbol table.
	 */
	public int getAmountOfUnresolvedTypeSpecifications() {
		return amountOfUnresolvedTypeSpecifications;
	}

	/**
	 * Decreases amount of unresolved type specifications in symbol table by
	 * given value. Only affects counter not size of collection in which
	 * vertices who represent a type specification are stored.
	 * 
	 * @param amout
	 *            Amount by which to decrease.
	 * @TODO relace by method which automatically decreases amount, when a type
	 *       specification is resolved. Hardly to encapsulate !!!
	 */
	public void decreaseAmountOfUnresolvedTypeSpecificationsBy(int amount) {
		amountOfUnresolvedTypeSpecifications -= amount;
	}

	private void putToUnresolvedTypeSpecifications(
			HashMap<Vertex, ArrayList<QualifiedType>> locallyUndefinedTypes) {
		// if( unresolvedTypeSpecifications == null )
		// unresolvedTypeSpecifications = new HashMap< Vertex, ArrayList<
		// QualifiedType > >();
		Set<Vertex> keySet = locallyUndefinedTypes.keySet();
		Iterator<Vertex> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			Vertex scope = iterator.next();
			ArrayList<QualifiedType> qualifiedTypes = locallyUndefinedTypes
					.get(scope);
			if (unresolvedTypeSpecifications.containsKey(scope)) {
				// amountOfUnresolvedTypeSpecifications +=
				// qualifiedTypes.size();
				unresolvedTypeSpecifications.get(scope).addAll(qualifiedTypes);
				qualifiedTypes.clear();
			} else if (!qualifiedTypes.isEmpty()) {
				ArrayList<QualifiedType> temp = new ArrayList<QualifiedType>();
				// amountOfUnresolvedTypeSpecifications +=
				// qualifiedTypes.size();
				temp.addAll(qualifiedTypes);
				unresolvedTypeSpecifications.put(scope, temp);
			}
		}
		locallyUndefinedTypes.clear();
	}

	public Set<Vertex> getScopesOfUnresolvedTypeSpecifications() {
		return unresolvedTypeSpecifications.keySet();
	}

	public ArrayList<QualifiedType> getUnresolvedTypeSpecifications(Vertex scope) {
		return unresolvedTypeSpecifications.get(scope);
	}

	public void addUnresolvedTypeSpecificationAfterParsing(Vertex scope,
			QualifiedType qualifiedTypeVertex) {
		unresolvedTypeSpecifications.get(scope).add(qualifiedTypeVertex);
	}

	// --- type specifications which have been resolved
	// --------------------------------

	private HashMap<String, QualifiedType> resolvedTypeSpecifications = new HashMap<String, QualifiedType>();

	public boolean hasResolvedTypeSpecifications() {
		return !resolvedTypeSpecifications.isEmpty();
	}

	public boolean hasResolvedTypeSpecification(String fullyQualifiedName) {
		return resolvedTypeSpecifications.containsKey(fullyQualifiedName);
	}

	public void addResolvedTypeSpecification(QualifiedType qualifiedTypeVertex) {
		resolvedTypeSpecifications.put(qualifiedTypeVertex
				.get_fullyQualifiedName(), qualifiedTypeVertex);
	}

	public QualifiedType getResolvedTypeSpecification(String fullyQualifiedName) {
		return resolvedTypeSpecifications.get(fullyQualifiedName);
	}

	// --- qualified names
	// ---------------------------------------------------------------------------------------

	private HashMap<String, QualifiedName> qualifiedNames = new HashMap<String, QualifiedName>();

	public boolean hasQualifiedNames() {
		return !qualifiedNames.isEmpty();
	}

	public boolean hasQualifiedName(String fullyQualifiedName) {
		return qualifiedNames.containsKey(fullyQualifiedName);
	}

	public void addQualifiedName(String fullyQualifiedName,
			QualifiedName qualifiedNameVertex) {
		qualifiedNames.put(fullyQualifiedName, qualifiedNameVertex);
	}

	public QualifiedName getQualifiedName(String fullyQualifiedName) {
		return qualifiedNames.get(fullyQualifiedName);
	}

	// --- primitive types -------------------------

	private HashMap<BuiltInTypes, BuiltInType> builtInTypes = new HashMap<BuiltInTypes, BuiltInType>();

	public boolean hasBuiltInType(BuiltInTypes type) {
		return builtInTypes.containsKey(type);
	}

	public BuiltInType getBuiltInType(BuiltInTypes type) {
		return builtInTypes.get(type);
	}

	public void addBuiltInType(BuiltInTypes type, BuiltInType builtInTypeVertex) {
		builtInTypes.put(type, builtInTypeVertex);
	}

	/**
	 * Clears references to vertices which represent elements that have a local
	 * scope. These are:
	 * <ul>
	 * <li>class and package imports</li>
	 * <li>labels</li>
	 * <li>local variables</li>
	 * <li>fields</li>
	 * <li>access to local variables & fields</li>
	 * <li>nested types</li>
	 * </ul>
	 */
	public void nextFile() {
		// @TODO find shorter name for method
		if (hasUnresolvedTypeSpecificationsInCurrentlyParsedFile()) {
			putUnresolvedTypeSpecificationsInCurrentlyParsedFileToGlobalOnes(); // Save
		}
		// unresolved
		// type
		// specifications
		// for
		// later.
		// packageImportVertex = null;
		// classImportVertex = null;
		if (labels != null) {
			labels.clear();
		}
		declaredLocalFieldsInScopes.clear();
		declaredGlobalVariables.clear();
		currentEnumConstants.clear();
		for (int counter = 0; counter < accessOfUndeclaredInternalVariables
				.size(); counter++) {
			fieldAccessMap.put(
					accessOfUndeclaredInternalVariables.get(counter), Boolean
							.valueOf(false));
		}
		accessOfUndeclaredInternalVariables.clear();
		if (nestedTypes != null) {
			nestedTypes.clear(); // @TODO clear maps in this hash map, too.
		}
		if (typeParameters != null) {
			typeParameters.clear();
		}
	}
}