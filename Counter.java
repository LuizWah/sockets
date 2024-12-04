package br.edu.ifsp.ppd.socket;

public class Counter {
    
    private int counter = 0;

    public synchronized int incrementAndGet() {
        this.counter++;
        return this.counter;
    }

}
