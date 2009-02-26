import java.io.*;

/**
 * Diese Klasse benutzt ein ForEach Konstrukt, welches in der
 * endgueltigen Fassung von Java 5 erlaubt ist. seihe:
 * http://java.sun.com/developer/technicalArticles/releases/
 * j2se15langfeat/
 */
public abstract class AnotherAllowedForEachConstruct{

    public void newFor(Collection<String> c)
    {
        for(String str : c)
        {
            sb.append(str);
        }
    }
}