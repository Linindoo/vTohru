package cn.vtohru.util;

import cn.vtohru.context.VerticleApplicationContext;

public class VTohruUtil {
    private static VerticleApplicationContext context;

    public static void setContext(VerticleApplicationContext context) {
        VTohruUtil.context = context;
    }

    public static VerticleApplicationContext getContext() {
        return context;
    }
}
