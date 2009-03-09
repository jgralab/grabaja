package bar;

import bar.baz.TestClass;

public abstract class TestClass2<S, T> extends TestClass<S, T> {
	private static final float CONST_FLOAT = -16.4f;

	public enum Things {
		X {
			@Override
			float eval(int a, int b) {
				float f = CONST_FLOAT + 1;
				return a * b * f;
			}
		},
		Y {
			@Override
			float eval(int a, int b) {
				return (a % b) * CONST_FLOAT;
			}
		};

		abstract float eval(int a, int b);
	};

	public void varLenMeth(String... strings) {
		int i = 0;
		String s = null;
		l1: do {
			s = null;
			if (i < 3) {
				continue l1;
			} else if (i < strings.length) {
				s = strings[i];
			}
		} while (s != null);
		synchronized (myThing) {
			myThing = Things.Y;
		}
	}

	public Things myThing = Things.X;
}
