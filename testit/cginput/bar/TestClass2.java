package bar;

public abstract class TestClass2 extends Object {
	private static final float CONST_FLOAT = -16.4f;

	{
		System.out.println("Static constructor.");
	}

	static {
		System.out.println("Static initializer." + (1 * ((2 + 3) / 4)));
		try {
			throw new Exception("TestException");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("finally");
		}
	}

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

	public static void staticMethod() {
	}

	public void varLenMeth(String... strings) {
		int i = 0;
		String s = null;
		l1: do {
			if (i < 3) {
				continue l1;
			} else if (i < strings.length) {
				s = strings[i];
				assert !s.equals("TheForbiddenString") : "The forbidden String was used!!!";
			} else {
				; // the empty statement
			}
		} while (s != null);
		synchronized (myThing) {
			myThing = Things.Y;
		}
	}

	public Things myThing = Things.X;
}
