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
package bar.baz;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import bar.SampleAnnotation;
import bar.TestClass2;
import bar.TestClass2.Things;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema.CGExpression;

public class TestClass<S, T> {
	private TestClass2 testClassField2;

	@SuppressWarnings("unused")
	@Inti
	private class LocalClass {
		S theEss = null;
	}

	public T mBar(T foo) {
		return foo;
	}

	public char[] mBaz(T foo, Graph g) {
		// the one array syntax
		char[][][] x = new char[1][2][3];
		CGExpression e = (CGExpression) g.getFirstVertex();
		// the other array syntax
		char y[][] = { { 'a', 'b', 'c' }, { 'd', 'e', 'f' }, { 'g', 'h', 'i' } };
		return ((foo.hashCode() % (y.length * x.length) == 0) ? x[0][1] : y[2]);
	}

	@SuppressWarnings("unused")
	@Inti
	@SampleAnnotation(number = 17, text = "I annotate getSet()", doubleVal = 0.1)
	private static Set<? super Integer> getSet(
			Map<Integer, Map<? extends Integer, ? super ArrayList<Integer>>> foo) {
		return foo.keySet();
	}

	@SampleAnnotation
	public void rotateThings(TestClass2[] tcs2) {
		for (TestClass2 tc2 : tcs2) {
			tc2.varLenMeth(new String[] { "a", "b", "c" });
			switch (tc2.myThing) {
			case X:
				TestClass2.staticMethod();
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
