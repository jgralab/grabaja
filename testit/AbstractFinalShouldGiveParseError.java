/**
 * Diese Klasse kombiniert die Modifier abstract und final,
 * welche sich gegenseitig ausschlieﬂen.
 * Erzeugt beim Kompilieren mit javac folgenden Fehler:
 *
 * AbstractFinalShouldGiveParseError.java:20:
 * illegal combination of modifiers: abstract and final
 *     public abstract final void someAbstractMethod();
 *                                ^
 * 1 error
 *
 */
import java.io.*;

public abstract class AbstractFinalShouldGiveParseError{

    public abstract final void someAbstractMethod();
}