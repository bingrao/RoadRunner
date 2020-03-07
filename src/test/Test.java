package test;

public class Test extends Thread{

    static final int ITERS = 100;

    static int y;

    public void inc() {
        y++;
    }

    @Override
    public void run() {
        for (int i = 0; i < ITERS; i++) {
            inc();
        }
    }

    public static void main(String args[]) throws Exception {
        final Test t1 = new Test();
        final Test t2 = new Test();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Is it " + (ITERS * 2) + "? " + y);
    }
}
