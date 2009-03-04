package B;
/**
 * Demonstrates resolving of nested types, no resolving of external types is needed here.
 * Challenge of this class is that package name cannot be part of a fully qualified type
 * specification because class and package share same name.
 */
public class B{

	D nestedD; //specifies B.B.D

	C.D evenMoreNestedD; // specifies B.B.C.D

	class C{ // fully qualified name: B.B.C

		D evenMoreNestedD; // specifies B.B.C.D

		B.D dInParentScope; // specifies B.B.D

		class D{} // fully qualified name: B.B.C.D
	}

	class D{} // fully qualified name: B.B.D

}