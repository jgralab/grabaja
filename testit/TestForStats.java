public class ForTests{

	public ForTests(){
		a.b.c++;
		system.out.println("x");
		for (int i=0; i < 3; i++)
			system.out.println(".");
		for (int j=0; j < 4; j++){
			system.out.println("!");
		}
		for (int k: b)
			system.out.println(":");
		for (int l: a){
			system.out.println("?");
		}
		int x = 0;
		switch (x){
			case 0:
			case 1: break;
			default: break;
		}
	}

}