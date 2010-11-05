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
@Deprecated( a = 15, b = 311 )
package batman.versus.joker;

import java.util.*;
import SomeClass;

public class TestClass{

	static{
		int someValue = 99;
	}

	String facadeName ="Rekoj's toy factory ltd.";

	double moschfred;

    int xcv, p = 42;

    //public <F> TestClass( F val ){
    //	Vector<F> f;

    //}
    //int[] ai = {1,2,3,4};

    //int[][] bi = {{1,2,3},{4,5,6}};

    public int me = 5;

	int capacity = 1000;

	/**/
    String deadlyToys = new String();

    //int[][] di = new int[6][7];

	public int j = 345;

	public TestClass() throws JokerException{

	}

	/**
	 * Searches the catalogue of deadly toys for the given one.
	 * @return true if toy figures in catalogue, else false.
	 */
	public boolean doWeHaveThisDeadlyToy( String nameOfToy ) throws SomeException{
		int i = 0;
		//let's search by using a while loop
		while( i < capacity ){
			if( deadlyToys[ i ].equals( nameOfToy ) )
				return true;
			i++;
		}
		return false;
	}

	/**
	 * @return true if there are still toys in the factory, else false
	 */
	public boolean doWeHaveAnyToysLeft() throws NoToysLeftException{
		{
			//a nested compound to test conversion process ast2tgraph
	    }
		if( deadlyToys.length > 0 )
			return true;
		else
			return false;
	}

	/**
	 * Holds the count of teddybears in jokers toy factory.
	 */
	public long teddys = 123;

	public void doll( final Object ... arg ){}

	public void mann()
	{

		long h = 8;

		try{

			int i = 23;


			boolean theTruth;

			theTruth = true;

        	; //that was an empty statement
		}
		catch( Exception someException ){
			//do something, whatever!
		}
		finally{
		    h = 5000;
		}




        int[] ghj = new int[Integer.parseInt(teddy)];

	    for(new String(); i<h; new Integer(3)){ h++; }
	    //String s = "hkshask" = "jajasg" = new String("doof");
	    //List<String> list = new ArrayList<String>( 9 );
        System.out.println();

        int l, jk, gh = 9;

        if( teddy instanceof String ){ int ggggg=9; }

/**        Vector v;
    	;

        for( Object o : getVec() ) ;

        float[] field;

        field = new float[6];

        //

        for( int w, t, r = 7;  ; ){}


*/
	}

}
