
package com.allwinner.theatreplayer.launcher.util;

import android.util.Log;


public abstract class ThreadTask implements Runnable {

    public static final int NO_ERROR = 0;

    public static final int RUNNING_STATUS_UNSTART = 0;

    public static final int RUNNING_STATUS_RUNNING = 1;

    public static final int RUNNING_STATUS_PAUSE = 2;

    public static final int RUNNING_STATUS_FINISH = 3;

    protected Thread mThread;

    protected int mProgress = 0;

    protected int mErrorCode = NO_ERROR;
    
    protected int version  = 0;
    

    protected int mRunningStatus = RUNNING_STATUS_UNSTART;
    
    protected Object mResult;

    
    //----------------Runtime Method
    
    public void start() {
    	
//    	Log.i("jim", "父类start mRunningStatus ="+mRunningStatus);    
        if (mRunningStatus != RUNNING_STATUS_RUNNING) {
            mRunningStatus = RUNNING_STATUS_RUNNING;
            mThread = new Thread(this);
//            Log.i("jim", "mThread.start()");    
            mThread.start();
        }
        onStart();
    }
    
    protected void onStart(){
//    	Log.i("jim", "onStart");    
    }
    
    @Override
    public void run(){    	
//    	Log.i("jim", "run");    	
        onRunning();
        stop();
    }
    
    protected void onRunning(){    	
//    	Log.i("jim", "父类onRunning");    	
    }

    public void pause() {
//    	Log.i("jim", "父类pause");    	
        mRunningStatus = RUNNING_STATUS_PAUSE;
        onPause();
    }

    protected void onPause() {
//    	Log.i("jim", "父类onPause");    
    }

    public void resume() {
//    	Log.i("jim", "父类resume");    
        if(mThread == null || !mThread.isAlive()){
            start();
        }
        mRunningStatus = RUNNING_STATUS_RUNNING;
        onResume();
    }

    protected void onResume() {
//    	Log.i("jim", "父类onResume");    
    	
    }

    public void stop() {
//    	Log.i("jim", "父类stop mRunningStatus = "+mRunningStatus);  
    	if(mRunningStatus != RUNNING_STATUS_UNSTART){
            onStop();
            mRunningStatus = RUNNING_STATUS_FINISH;
    	}
    }
    
    public boolean reset(){
        if(mRunningStatus == RUNNING_STATUS_FINISH || 
                mRunningStatus == RUNNING_STATUS_PAUSE||
                mRunningStatus == RUNNING_STATUS_RUNNING){
//        	Log.i("jim", "父类reset 设置未启动"); 
        	mProgress = 0;
            mRunningStatus = RUNNING_STATUS_UNSTART;
            return true;
        }
        return false;
    }

    protected void onStop() {}
    
    
    //-----------------Public Method
    
    public int getErrorCode() {return mErrorCode;}

    public int getRunningStatus() {return mRunningStatus;}

    public int getProgress() {return mProgress;}
    
    public Object getResult() {return mResult;}
}
