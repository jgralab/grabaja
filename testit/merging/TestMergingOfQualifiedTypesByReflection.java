/**
 * Demonstrates merging of type specifications denoting same type.
 * For java.lang.String merging only occurs if extraction mode is EAGER or COMPLETE.
 */
public class TestMergingOfQualifiedTypesByReflection{

	String aString; //type specifications for both field declarations are represented by only one QualifiedType in graph

	String anotherString; //as no fully qualified name is given for String no cascade of QualifiedName is created in graph
}