/**
 * Diese Klasse nutzt ein for each ( in ) Konstrukt, welches
 * in den Previews zu Java 5 Tiger noch unterstützt wurde, es
 * allerdings nicht in die endgültige Fassung geschafft hat.
 * Erzeugt beim Kompilieren mit javac folgenden Fehler:
 *
 * ForEachInShouldGiveParseError.java:13: '(' expected
 *         for each (int ia[] in iaa)
 *             ^
 * ForEachInShouldGiveParseError.java:20:
 *  illegal start of expression
 *     }
 *     ^
 * 2 errors
 *
 */
import java.io.*;

public class ForEachInShouldGiveParseError{

    public void printArray()
    {
        int[][] iaa = new int[10][10];
        for each (int ia[] in iaa)
        {
            for each (int i in ia)
            {
             	System.out.println(i);
            }
        }
    }
}