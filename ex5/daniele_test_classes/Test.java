/**
 * Created by alka on 6/29/2017.
 */

class Test {
    public static void main(String[] args) {
        A a;
        B b;
        a = new A();
        a.x = 2;
        System.out.println(a.x);
        int c;
        c = a.x;
        System.out.println(c);

    }
}

class A {
    int x;
    int y;

    int f(int d) {
        return d;
    }

}

class B extends A {
    int y;

    int f(int d) {
        return d;
    }
}

class C extends B {
    int x;


    int f(int d) {
        if (d == 0) {
            d = 7;
        } else {
            d = 10;
        }
        return d;

    }

    int g(int m, int n) {
        return (m + n);
    }
}
