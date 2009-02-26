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