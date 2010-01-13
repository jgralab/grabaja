package bar;

@SampleAnnotation
public @interface SampleAnnotation {
	public long number() default -1l;

	public String text() default "";

	public double doubleVal() default -0.0d;
}
