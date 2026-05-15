package com.trainingplatform.util;

public final class PaginationUtil {
    private PaginationUtil() {}
    public static int safePage(Integer page) {
        return page == null || page < 0 ? 0 : page;
    }
}
