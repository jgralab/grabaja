/**
 *  Diese Klasse enthält zulässige Generics-Konstrukte.
 */
public class AllowedGenericConstruct<T>
{
    class B<S>
    {
        //empty
    }
    AllowedGenericConstruct<Integer>.B<Integer> c;
}
