package test.sync;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int thread_cnt = 4;
        final int itr_cnt = 100;
        RunnerThread[] threads = new RunnerThread[thread_cnt];
        Counter resource = new Counter(0);

        for(int i = 0; i< thread_cnt; i++){
            threads[i] = new RunnerThread("t"+i, itr_cnt, resource);
        }

        for(int i = 0; i< thread_cnt; i++){
            threads[i].start();
        }

        for(int i = 0; i< thread_cnt; i++){
            threads[i].join();
        }

        System.out.println("The count is " + resource.getCount());
    }
}
