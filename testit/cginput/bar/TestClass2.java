/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package bar;

import bar.baz.TestClass;

public abstract class TestClass2 extends Object {
	private static final float CONST_FLOAT = -16.4f;
	private TestClass<Integer, Integer> tClass;

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
