package tools.deeptrace;

public class EventCount{
    private long count;
    public EventCount(long count){
        this.count = count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public synchronized long inc(){
        return count++;
    }
}