package bar.baz;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import bar.SampleAnnotation;
import bar.TestClass2;
import bar.TestClass2.Things;

public class TestClass<S, T> {
	public T bar(T foo) {
		return foo;
	}

	public S baz(T foo) {
		char[][][] x = new char[1][2][3]; // the one array syntax
		//char y[][][] = new char[1][2][3]; // the other array syntax
		return (foo.hashCode() % 17 == 0) ? null : null;
	}

	@SampleAnnotation(number = 17, text = "I annotate getSet()", doubleVal = 0.1)
	public static Set<? super Integer> getSet(
			Map<Integer, Map<? extends Integer, ? super ArrayList<Integer>>> foo) {
		return null;
	}

	public void rotateThings(TestClass2<S, T>[] tcs2) {
		for (TestClass2<S, T> tc2 : tcs2) {
			switch (tc2.myThing) {
			case X:
				tc2.myThing = Things.Y;
				break;
			case Y:
				tc2.myThing = Things.X;
				break;
			default:
				throw new RuntimeException("Unknown thing...");
			}
		}
	}
}
