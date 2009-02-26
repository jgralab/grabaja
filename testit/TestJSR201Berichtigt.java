/**
 * Diese Klasse ist ein Testquelltext f�r die Java 1.5
 * Grammatik von CUP. Das Original wird von CUP fehlerfrei
 * geparst, ist jedoch nicht mit javac kompilierbar.
 * Nachdem wir einige Auskommentierungen vorgenommen
 * hatten, wurde diese auch kommentarlos von javac
 * kompiliert. Da diese Klasse recht komplexe Statements
 * enthaelt, wird diese auch f�r die Parsingtests benutzt.
 * Dies ist jedoch mehr ein statistischer Test.
 */
import static java.lang.Math.*; // test of static import
import static java.lang.System.out; // ditto
import java.util.*;

class TestJSR201Berichtigt
{
    enum Color { red, green, blue ; };

    public static void main(String... args/* varargs */)
    {
        /* for each on multi-dimensional array */
        int[][] iaa = new int[10][10];
        for (int ia[] : iaa)
        {
            for (int i : ia)
            	out.print(i); // use static import.
            out.println();
        }
        /* */
//////////////////////////////////////////////////////////
// Auskommentiert da der Compiler Color.VALUES nicht kennt
//////////////////////////////////////////////////////////
//        for (Color c : Color.VALUES)
//        {
//            switch(c)
//            {
//                case Color.red: out.print("R");
//                break;
//                case Color.green: out.print("G");
//                break;
//                case Color.blue: out.print("B");
//                break;
//                default: assert false;
//            }
//        }
//////////////////////////////////////////////////////////
        out.println();
    }// end of main

    // complex enum declaration, from JSR-201
    public static enum Coin
    {
        penny(1), nickel(5), dime(10), quarter(25);
        Coin(int value)
        {
            this.value = value;
        }
        private final int value;
        public int value()
        {
            return value;
        }
    }

    public static class Card implements Comparable,
                                        java.io.Serializable
    {
        public enum Rank
        {
            deuce, three, four, five, six, seven,
            eight, nine, ten, jack, queen, king, ace
        }

        public enum Suit
        {
            clubs, diamonds, hearts, spades
        }

        private final Rank rank;
        private final Suit suit;

        private Card(Rank rank, Suit suit)
        {
            if (rank == null || suit == null)
                throw new NullPointerException(rank + ", " +
                                               suit);
            this.rank = rank;
            this.suit = suit;
        }

        public Rank rank()
        {
            return rank;
        }

        public Suit suit()
        {
            return suit;
        }

        public String toString()
        {
            return rank + " of " + suit;
        }

        public int compareTo(Object o)
        {
            Card c = (Card)o;
            int rankCompare = rank.compareTo(c.rank);
            return rankCompare != 0 ? rankCompare :
            suit.compareTo(c.suit);
        }

        private static List sortedDeck = new ArrayList(52);

        /* BROKEN IN PROTOTYPE 2.0 */
//////////////////////////////////////////////////////////
// Auskommentiert da in Compiler Rank.VALUES und
// Suit.VALUES nicht kennt
//////////////////////////////////////////////////////////
//        static
//        {
//            for (Rank rank : Rank.VALUES)
//            for (Suit suit : Suit.VALUES)
//                sortedDeck.add(new Card(rank, suit));
//        }
//////////////////////////////////////////////////////////
        /* */

        // Returns a shuffled deck
        public static List newDeck()
        {
            List result = new ArrayList(sortedDeck);
            Collections.shuffle(result);
            return result;
        }
    } // end of class Card

    // sophisticated example:
//////////////////////////////////////////////////////////
// abstract auskommentiert da f�r enum in zul�ssig
//////////////////////////////////////////////////////////
    public static /*abstract*/ enum Operation
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
//////////////////////////////////////////////////////////
// Auskommentiert da in Compiler VALUES nicht kennt
//////////////////////////////////////////////////////////
//            for (Operation op : VALUES)
//                out.println(x + " " + op + " " +
//                            y + " = " + op.eval(x, y));
//////////////////////////////////////////////////////////
        }
    }
}// end of class TestJSR201Berichtigt
