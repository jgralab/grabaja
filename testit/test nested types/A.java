package a;

import java.util.Vector;
import javax.swing.*;
import someClass;

class A{

	A a0;

	a.A a1;

	B b0;

	A.B b1;

	a.A.B b2;

	B.D d3;

	String s;



	class B{  // a.A.B (fully qualified)

		D d0;

		B.D d1;

		A ax;


		class D{}	// a.A.B.D (fully qualified)
	}

	class C{ // a.A.C  (fully qualified)

		D d0;

		C.D d1;

		class D{}	// a.A.C.D (fully qualified)
	}
}