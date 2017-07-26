package com.arialyy.frame.util.show;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class APPLog {
    public static final int INFO = 0x03;
    public static final int DEBUG = 0x02;
    public static final int WARN = 0x01;
    public static final int ERROR = 0x00;
    private static final String TAG = "UT_LOG";
    private static final String SEPARATE = " --- ";
    private static int currentLevel = INFO;

    public static void setLevel(int level) {
        currentLevel = level;
    }

    private static String getPrefix() {
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        long threadId = currentThread.getId();
        String prefix = threadName + "(" + threadId + "):";
        return prefix;
    }

    private static String getTrace() {
        try {
            Throwable t = new Throwable();
            StackTraceElement trace = t.getStackTrace()[2];
            String method = trace.getClassName() + "." + trace.getMethodName() + "[" + trace.getLineNumber() + "]";
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getSimpleTrace() {
        try {
            Throwable t = new Throwable();
            StackTraceElement trace = t.getStackTrace()[2];
            String method = trace.getFileName() + "[" + trace.getLineNumber() + "]";
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void printStackTrace() {
        Throwable t = new Throwable();
        StackTraceElement[] traces = t.getStackTrace();
        Log.i(TAG, "__________________________________________________________");
        for (int i = 1; i < traces.length; i++) {
            StackTraceElement traceElement = traces[i];
            String method = traceElement.getClassName() + "." + traceElement.getMethodName() + "[" + traceElement.getLineNumber() + "]";
            String log = "| " + getPrefix() + method;
            Log.i(TAG, log);
        }
        Log.i(TAG, "|___________________________________________________________");
    }

    public static void printInfo(String msg) {
        try {
            if (currentLevel >= INFO) {
                String log = getPrefix() + getTrace() + SEPARATE + msg;
                Log.i(TAG, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printSimpleInfo(String msg) {
        try {
            if (currentLevel >= INFO) {
                String log = getSimpleTrace() + SEPARATE + msg;
                Log.i(TAG, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printDebug(String msg) {
        try {
            if (currentLevel >= DEBUG) {
                String log = getPrefix() + getTrace() + SEPARATE + msg;
                Log.d(TAG, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printSimpleDebug(String msg) {
        try {
            if (currentLevel >= DEBUG) {
                String log = getSimpleTrace() + SEPARATE + msg;
                Log.d(TAG, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printWarn(String msg) {
        try {
            if (currentLevel >= WARN) {
                String log = getPrefix() + getTrace() + SEPARATE + msg;
                Log.w(TAG, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printSimpleWarn(String msg) {
        try {
            if (currentLevel >= WARN) {
                String log = getSimpleTrace() + SEPARATE + msg;
                Log.w(TAG, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printError(Exception e) {
        try {
            if (currentLevel >= ERROR) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(out);
                e.printStackTrace(printStream);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String time = sf.format(new Date());
                String specingString = "--------------" + time + "--------------\n";
                Log.e(TAG, specingString);
                Log.e(TAG, out.toString());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static File logFileDir = new File(Environment.getExternalStorageDirectory().toString() + "/UT/Logs/");

    public static void printThrowableToDisk(Throwable throwable) {
        try {
            if (currentLevel >= ERROR) {
                if (!logFileDir.exists()) {
                    logFileDir.mkdirs();
                }
                SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String time = sf.format(new Date());
                String specingString = "--------------" + time + "--------------\n";

                File logs = new File(logFileDir, "UT_Error_Log.log");
                FileOutputStream out;
                if (logs.length() >= 10 * 1024 * 1024) {
                    out = new FileOutputStream(logs);
                } else {
                    out = new FileOutputStream(logs, true);
                }
                PrintStream printStream = new PrintStream(out);
                printStream.write(specingString.getBytes());
                throwable.printStackTrace(printStream);
                printStream.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printThrowable(Throwable throwable) {
        try {
            if (currentLevel >= ERROR) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(out);
                throwable.printStackTrace(printStream);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String time = sf.format(new Date());
                String specingString = "--------------" + time + "--------------\n";
                Log.e(TAG, specingString);
                Log.e(TAG, out.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
