/**
 * Diese Klasse kombiniert die Modifier public und private,
 * welche sich gegenseitig ausschlieﬂen. Erzeugt beim
 * Kompilieren mit javac folgenden Fehler:
 *
 * PublicPrivateShouldGiveParseError.java:15:
 *  illegal combination of modifiers: public and private
 *     public private void someMethod();
 *                         ^
 * 1 error
 */
import java.io.*;

public class PublicPrivateShouldGiveParseError{

    public private void someMethod()
    {
        // do nothing
    }
}