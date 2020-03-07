package test.sync;

public class Counter {
    private int count;
    public Counter(int value){
        this.count = value;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void inc() {
        synchronized(this) {
            count++;
        }
    }
}
