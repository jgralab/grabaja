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
/**
 * Diese Klasse definiert ein abstraktes enum, was nicht
 * erlaubt ist. Erzeugt beim Kompilieren mit javac
 * folgenden Fehler:
 *
 * AbstractEnumShouldGiveParseError.java:3:
 *     modifier abstract not allowed here
 *     public static abstract enum Operation
 *                            ^
 * 1 error
 *
 */
class AbstractEnumShouldGiveParseError{

    public static abstract enum Operation
    {
        plus
        {
            double eval(double x, double y)
            {
                return x + y;
            }
        },
        minus
        {
            double eval(double x, double y)
            {
                return x - y;
            }
        },
        times
        {
            double eval(double x, double y)
            {
                return x * y;
            }
        },
        divided_by
        {
            double eval(double x, double y)
            {
                return x / y;
            }
        };

// Perform arithmetic operation represented by this constant
        abstract double eval(double x, double y);

        public static void main(String args[])
        {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
        }
    }
}
