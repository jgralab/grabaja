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
/** A valid JSR-14 Java program, which illustrates some
 *  corner-cases in the 'smart lexer' lookahead implementation
 *  of the grammar.  It should compile correctly using a
 *  JSR-14 javac, as well as parse correctly (no syntax
 *  errors) using the java15.cup grammar in this package.
 */
public class Test15<X> {
    <T> Test15(T t) { }
    int a = 1, b = 2;
    C c1 = new C<Integer>(),
    c2 = new C<B>(),
    c3 = new C<B[]>();
    C<B> cc2 = c2;
    C<B[]> cc3 = c3;
    boolean d = a < b, e = a < b;
    int f[] = new int[5];
    boolean g = a < f[1];
    boolean h = ( a < f[1] );
    Object i0 = (A) cc3;
    Object  i = ( A < B[] > ) cc3;
    Object  j = ( A < B > ) cc2;
    Object  k = ( A < A < B[] > >) null;
    Object  kk= ( A < A < B[] >>) null;
    Test15<X>.H hh = null;
    {
	Test15<X>.H hhh = null;
	for (boolean l=a<b, m=a<b; a<b ; l=a<b, f[0]++)
	    a=a;
	for (;d;)
	    b=b;
	A<Integer> m = c1;
	if (m instanceof C<Integer>)
	    a=a;
	for (boolean n = m instanceof C<Integer>,
		 o = a<b,
		 p = cc3 instanceof C<B[]>;
	     cc3 instanceof C<B[]>;
	     n = m instanceof C<Integer>,
		 o = a<b,
		 p = cc3 instanceof C<B[]>)
	    b=b;
	for (;m instanceof C<Integer>;)
	    a=a;
	if (a < b >> 1)
	    ;
	Object o1 = new A<A<B>>(),
	    o2 = new A<A<A<B>>>(),
	    o3 = new A<A<D<B,A<B>>>>();

	// new, "explicit parameter" version of method
    // invocation.
	A<Integer> aa = Test15.<A<Integer>>foo();
	/* although the spec says this should work:
           A<Integer> aa_ = <A<Integer>>foo();
	 * Neal Gafter has assured me that this is a bug in the
     * spec.
	 * Type arguments are only valid after a dot. */

	// "explicit parameters" with constructor invocations.
    // prototype 2.2 chokes on this.
	new <String> K<Integer>("xh");
	this.new <String> K<Integer>("xh");
    }

    static class A<T> { T t; }
    static class B { }
    static class C<T> extends A<T> { }
    static class D<A,B> { }
    static class E<X,Y extends A<X>> { }
    static interface F { }
    // wildcard bounds.
    static class G{
        A<? extends F> a; A<? super C<Integer>> b;
    }
    class H { }
    static class I extends A<Object[]> { }
    static class J extends A<byte[]> { }
    class K<Y> { <T>K(T t) { Test15.<T>foo(); } }

    static <T> T foo() { return null; }
}
