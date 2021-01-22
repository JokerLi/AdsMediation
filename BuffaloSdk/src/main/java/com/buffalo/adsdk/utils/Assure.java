package com.buffalo.adsdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.buffalo.adsdk.BuildConfig;
import com.buffalo.utils.ThreadHelper;

import java.util.Collection;

public class Assure {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean DEBUG_STACK = false;

    static public class AssureException extends RuntimeException {
        private static final long serialVersionUID = 874757892066603343L;

        public AssureException() {
            super();
        }

        public AssureException(String msg) {
            super(msg);
        }
    }

    public static void throwMessage(String msg) {
        if (DEBUG) {
            throw new AssureException(msg);
        } else {
            Log.e("ASSURE fail", msg + getCurrentStackMsg());
        }
    }

    private static String getCurrentStackMsg() {
        if (!DEBUG_STACK) {
            return "";
        }

        String stackMsg = "\n";
        try {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            for (int i = 0; i < stacks.length; ++i) {
                stackMsg += stacks[i].toString() + "\n";
            }
        } catch (Exception e) {
        }
        return stackMsg;
    }

    public static void NOTREACHED() {
        throwMessage("NOTREACHED");
    }

    public static void NOT_IMPLEMENTED() {
        throwMessage("NOT_IMPLEMENTED");
    }

    public static void checkTrue(boolean value) {
        if (!value) {
            throwMessage("AssureTrue");
        }
    }

    public static void checkFalse(boolean value) {
        if (value) {
            throwMessage("AssureFalse");
        }
    }

    public static void checkNull(Object obj) {
        if (obj != null) {
            throwMessage("AssureNull");
        }
    }

    public static void checkNotNull(Object obj) {
        if (obj == null) {
            throwMessage("AssureNotNull");
        }
    }

    public static void checkNotEqual(int expectNot, int real) {
        if (expectNot == real) {
            throwMessage("AssureNotEqual");
        }
    }

    public static void checkNotEqual(Object expectNot, Object real) {
        if (expectNot == real) {
            throwMessage("AssureNotEqual");
        }

        if (expectNot != null && real != null) {
            if (expectNot.equals(real)) {
                throwMessage("AssureNotEqual");
            }
        }
    }

    public static void checkEqual(Object expect, Object real) {
        if (expect != real) {
            throwMessage("AssureEqual");
        }
    }

    public static void checkEqual(int expect, int real) {
        if (expect != real) {
            throwMessage("AssureEqual");
        }
    }

    public static void checkEqual(long expect, long real) {
        if (expect != real) {
            throwMessage("AssureEqual");
        }
    }

    public static void checkEqualNoCase(String expect, String real) {
        if (expect == null && real == null) {
            return;
        }

        if (expect == null || real == null) {
            throwMessage("AssureEqual");
            return;
        }

        if (!expect.equalsIgnoreCase(real)) {
            throwMessage("AssureEqual");
        }
    }

    public static void checkNotEmptyString(String webUrl) {
        if (TextUtils.isEmpty(webUrl)) {
            throwMessage("AssureNotEmptyString");
        }
    }

    public static <E> void checkNotEmptyCollection(Collection<E> collection) {
        if (collection == null || collection.isEmpty()) {
            throwMessage("AssureNotEmptyCollection");
        }
    }

    public static <E> void checkNotEmptyArray(E[] collection) {
        if (collection == null || collection.length <= 0) {
            throwMessage("checkNotEmptyArray");
        }
    }

    public static void DCHECK(boolean value) {
        if (!value && DEBUG) {
            throwMessage("DCHECK ERROR");
        }
    }

    public static void checkRunningOnUIThread() {
        DCHECK(ThreadHelper.runningOnUiThread());
    }

    public static void checkNotOnUIThread() {
        DCHECK(!ThreadHelper.runningOnUiThread());
    }
}
