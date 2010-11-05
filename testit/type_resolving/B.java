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
