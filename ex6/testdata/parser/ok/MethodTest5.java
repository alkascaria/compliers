class MethodTest {
	public static void main(String[] a) {
		B x;
		A y;
		x = new B();
		y = x.m();
	}
}

class A {
	A m() {
		return new A();
	}

}

class B extends A {
}