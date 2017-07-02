class VarTest {
	public static void main(String[] args) {
		
	
	B b;
	b = null;
	
	b = new B();
	
	b.x = 10;
	
	System.out.println(b.x);	
	
		
		
	}
}

class B extends C
{	

	int x;
	
	int a;

	int b(int a, int b, int c)
	{
		int d;
		d = a + b;
		
		return d;
	}
	
}

class C
{
	int x;
	
}
