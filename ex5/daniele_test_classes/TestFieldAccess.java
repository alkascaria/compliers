class TestFieldAccess {
	public static void main(String[] argv) 
	{
		AA aa;
		aa = new AA();
		
		int[] a;
		
		a = null;
		
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
	
	int h;
}

