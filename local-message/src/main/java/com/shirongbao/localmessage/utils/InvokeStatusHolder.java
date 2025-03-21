package com.shirongbao.localmessage.utils;

public class InvokeStatusHolder {

    private static final ThreadLocal<Boolean> inInvoke = ThreadLocal.withInitial(() -> false);

    public static void startInvoke() {
        inInvoke.set(true);
    }

    public static void endInvoke() {
        inInvoke.set(false);
    }

    public static boolean inInvoke() {
        return inInvoke.get();
    }
}