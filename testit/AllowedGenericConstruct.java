/**
 *  Diese Klasse enth�lt zul�ssige Generics-Konstrukte.
 */
public class AllowedGenericConstruct<T>
{
    class B<S>
    {
        //empty
    }
    AllowedGenericConstruct<Integer>.B<Integer> c;
}
