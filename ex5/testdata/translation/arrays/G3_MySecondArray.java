class ArrayDoSomeThing1{
    public static void main(String[] args) {
        // declares an array of integers
        int[] anArray;

        // allocates memory for 10 integers
        anArray = new int[1000];

        // initialize first element
        anArray[0] = 100000;
        // initialize second element
        anArray[1] = 20000;
        // and so forth
        anArray[2] = 30000;
        anArray[3] = 40000;
        anArray[4] = 50000;
        anArray[5] = 60000;

        System.out.println(anArray[0]);
        System.out.println(anArray[1]);
        System.out.println(anArray[2]);
        System.out.println(anArray[3]);
        System.out.println(anArray[4]);
        System.out.println(anArray[5]);

    }
}