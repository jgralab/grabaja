package bar.baz;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import bar.SampleAnnotation;
import bar.TestClass2;
import bar.TestClass2.Things;

public class TestClass<S, T> {
	@SuppressWarnings("unused")
	@Inti
	private class LocalClass {
		S theEss = null;
	}

	public T mBar(T foo) {
		return foo;
	}

	public char[] mBaz(T foo) {
		// the one array syntax
		char[][][] x = new char[1][2][3];
		// the other array syntax
		char y[][] = { { 'a', 'b', 'c' }, { 'd', 'e', 'f' }, { 'g', 'h', 'i' } };
		return ((foo.hashCode() % y.length * x.length == 0) ? x[0][1] : y[2]);
	}

	@SuppressWarnings("unused")
	@Inti
	@SampleAnnotation(number = 17, text = "I annotate getSet()", doubleVal = 0.1)
	private static Set<? super Integer> getSet(
			Map<Integer, Map<? extends Integer, ? super ArrayList<Integer>>> foo) {
		return foo.keySet();
	}

	@SampleAnnotation
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

@interface Inti {

}