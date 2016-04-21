package com.allwinner.theatreplayer.launcher;



/**
 * @author trim
 *
 */
public class Log {

    static final String TAG = "Trim";
    
    public static void i(String msg){
        android.util.Log.i(TAG, msg);
    }
    
    public static void d(String msg){
        android.util.Log.d(TAG, msg);
    }
    
    public static void e(String msg){
        android.util.Log.e(TAG, msg);
    }
}
