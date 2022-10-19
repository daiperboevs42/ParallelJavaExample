import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Main {
    //creates exception handler for threads
    static Thread.UncaughtExceptionHandler
            h = (th, ex) -> System.out.println("Uncaught exception: " + ex);

    public static void main(String[] args) {
        //runs 5 threads of a simple task
        //runThread();

        //finds primes
        //runPrimes();

        //uses executor to increment number
        synchronizedIncrement();
    }
    private static void runThread(){
        for (int i = 0; i < 5; i++) {
            SleepThread sleepThread = new SleepThread(i);
            sleepThread.setUncaughtExceptionHandler(h);
            sleepThread.start();
        }
    }
    private static void runPrimes(){
        for (int i = 0; i < 5; i++) {
            PrimesThread primesThread = new PrimesThread(i);
            primesThread.setUncaughtExceptionHandler(h);
            primesThread.start();
        }
    }
    private static void synchronizedIncrement(){
        for (int i = 0; i < 5; i++) {
            ExecutorClass executor = new ExecutorClass();
            executor.synchronizedIncrement();
        }
    }
}

//class for incrementing count
class ExecutorClass{
    int count = 0;
    public void synchronizedIncrement(){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        IntStream.range(0, 10000)
                .forEach(i -> executor.submit(this::incrementSync));

        executor.shutdown();
        try { executor.awaitTermination(10,TimeUnit.SECONDS);}
        catch (InterruptedException e) { throw new RuntimeException(e);}
        System.out.println("Incremented count by: " +count);
    }
    void incrementSync(){
        synchronized(this) {
            count = count + 1;
        }
    }
    ReentrantLock lock = new ReentrantLock();
    void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}

// thread for sleeping
class SleepThread extends Thread {
    private int id;
    public SleepThread(int id){
        this.id = id;
    }
    @Override
    public void run(){
        System.out.println("Task " +id + " is beginning");
        if (id ==3){
            //throws an error being handled in Main
            throw new RuntimeException();
        }
        try {
            sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("Task " +id + " is completed");
    }
}

// thread to print prime numbers
class PrimesThread extends Thread {
    private int id;
    public PrimesThread(int id){
        this.id = id;
    }
    public synchronized void run()
    {
        long startTime = System.nanoTime();
        int start = 0;
        int i = 0;
        int num = 0;
        int end = 10000;
        String primeNumbers = "";
        for (i = 1; i <= end; i++) {
            int counter = 0;
            for (num = i; num >= 1; num--) {
                // condition to check if the number is prime
                if (i % num == 0) {
                    // increment counter
                    counter = counter + 1;
                }
            }
            if (counter == 2) {
                primeNumbers = primeNumbers + i + " ";
            }
        }
        long endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        System.out.println("\n Thread: " +id +" Prime numbers from " +start + " to " +end + " : \n"
                + primeNumbers + "\n Time to complete:"+endTime +"ms");
        System.out.println();
    }
}