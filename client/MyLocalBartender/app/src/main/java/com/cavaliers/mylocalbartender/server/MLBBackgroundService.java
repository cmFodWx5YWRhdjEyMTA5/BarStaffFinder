package com.cavaliers.mylocalbartender.server;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.util.Pair;

import java.util.concurrent.LinkedBlockingQueue;

public class MLBBackgroundService extends Thread{

    /**
     * state for when thread has never been started
     */
    public final static int NOT_STARTED = 0x00;

    /**
     * state for when this event thread is running
     */
    public final static int RUNNING = 0x01;

    /**
     * ste for when the thread is stopped
     */
    public final static int STOPPED = 0x02;

    /**
     * state for when thread is wating
     */
    public final static int WAITING = 0x03;

    private final Object monitor = new Object();
    private final LinkedBlockingQueue<Pair<Runnable,Runnable>> mProcessQueue;

    private int status = NOT_STARTED;

    private Handler handler = new Handler();

    private final static String TAG = "MLBBackgroundService";

    MLBBackgroundService(){

        super(TAG);
        mProcessQueue = new LinkedBlockingQueue<>();
    }

    public void run(){

        status = RUNNING;

        while (status == RUNNING){

            synchronized (monitor){

                try {

                    final Pair<Runnable,Runnable> task = mProcessQueue.take();

                    handleTask(task.first);

                    if(task.second != null) {
                        monitor.wait();
                        status = WAITING;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                task.second.run();
                                status = RUNNING;
                                monitor.notify();
                            }
                        });
                    }

                }catch (InterruptedException e){

                    Log.e(TAG,"Was interrupted");
                    continue;
                }
            }
        }
    }

    synchronized void submitTask(@NonNull Runnable request, Runnable afterRequest){

        if(request != null) {

            mProcessQueue.add(new Pair<Runnable, Runnable>(request,afterRequest));
        }
    }

    private void handleTask(Runnable runnable){

        runnable.run();
    }

    public void stopService() throws InterruptedException{

        status = STOPPED;
        join();
    }

    public int getStatus(){

        if(super.getState() == State.WAITING) return WAITING;

        return status;
    }
}
