package t3_1;

import t3_2.Test3_2;
import t3_3.*;

public class Test3_1{

	Test3_1(){
		int j = Test3_2.i;		// access to class imported by explicit type and field inside that class
		j = Test3_3.i;			// access to class imported by wildcard type and field inside that class
	}

}