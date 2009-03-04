/**
 * Demonstrates resolving of nested type if it hides a type parameter and
 * if it is hidden by one.
 */
public class TestCombinationOfNestedTypesAndTypeParameters< A >{

	A a; // specifies A.A

	class A{} // fully qualified name A.A

	class B< B >{ // fully qualified name A.B

		B b; // specifies type parameter B
	}
}