public class TestVarArgs {
// valid method
	void testmethod1 (Object ... args) {
		// tu erst mal nix!
	}

// invalid method
//	void testmethod2 (Object ... args1, int ti, Object ... args2) {
//		// tu immer noch nix!
//	}

	// valid method
	void testmethod3 (int ti1, int ti2, int ti3, Object ... args1) 
	{
		// tu immer noch nix!
	}
}