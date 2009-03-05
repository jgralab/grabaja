package bar;

@SampleAnnotation
public @interface SampleAnnotation {
	public int number() default -1;

	public String text() default "";

	public double doubleVal() default -0.0d;
}
