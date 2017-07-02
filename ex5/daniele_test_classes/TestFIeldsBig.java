class TestFieldAccess {
	public static void main(String[] argv) 
	{
		AA aa;
			
		aa = new AA();
		
		int test;
		
		test = new AA().x;
		

		System.out.println(test);
		
		aa.x = 10;
		
		aa.c = 50;
		
		System.out.println(aa.x);
		System.out.println(aa.c);
			
		
		
		
		
		
		
	}
}

class AA extends BB
{	
	int x;
	
	
	
	boolean e;
	
	int[] a;
	
}

class BB extends CC
{
	
	int y;
	int d;
}

class CC
{
	int c;
	
	int x;
	
	int h;
}

