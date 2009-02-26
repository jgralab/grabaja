import java.io.*;

/**
 * Diese Klasse benutzt ein ForEach-Konstrukt, welches in der
 * endgueltigen Fassung von Java 5 erlaubt ist.
 */
public abstract class AllowedForEachConstructs{

    public void printArray()
    {
        int[][] iaa = new int[10][10];
        for (int ia[] : iaa)
        {
            for (int i : ia)
            {
                System.out.print(i);
            }
        }
    }
}