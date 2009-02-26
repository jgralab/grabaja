/**
 * Demonstrates merging of type specifications specifying same type.
 * In this case specified class is part of graph, so merging works with every extraction mode.
 */
public class TestMergingOfQualifiedTypes{

	EmptyClass aClassInGraph; //type specifications for both field declarations are represented by only one QualifiedType in graph

	EmptyClass anotherClassInGraph; //as no fully qualified name is given for EmptyClass no cascade of QualifiedName is created in graph
}