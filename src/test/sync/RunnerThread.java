package test.sync;

public class RunnerThread extends Thread {
    private Thread t;
    private Counter resource;
    private int itr_count;
    private String thread_name;
    public RunnerThread(String name, int cnt, Counter obj){
        this.itr_count = cnt;
        this.resource = obj;
        this.thread_name = name;
        System.out.println("Create a thread ["+name+"]...");
    }
    @Override
    public void run() {
        System.out.println("Starting " +  thread_name );
        for(int i = 0; i < itr_count; i++)
            this.resource.inc();
    }
}
