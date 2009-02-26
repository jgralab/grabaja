public class Test6_1{

	private Object o = new Test6_2();

	Test6_1(){
		((Test6_2)o).i = 42;		// access to a field (i) inside a class that has been casted to
	}

}