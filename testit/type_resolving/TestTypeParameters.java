/**
 * Demonstrates resolving of type parameters.
 * This case is challenging because declared type parameters share same name.
 */

import java.util.*;

public class TestTypeParameters< A extends Collection >{ // type parameter has upper bound to demnostrate that creation of Identifier vertex is not dispensible

	A a; // field has type of type parameter of class

	public < A > void someMethod( A differentA ){} // hides type parameter of class so paramter has type of type paramter of method
}