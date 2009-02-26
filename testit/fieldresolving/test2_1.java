public class Test2_1 extends Test2_2{

	Test2_1(){
		this.i = 0;		// access to own class (this) and inherited field (i)
		super.i = 42;	// explicit access to superclass (super) and field inside superclass (i)
	}

}