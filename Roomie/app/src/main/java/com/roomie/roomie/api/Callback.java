package com.roomie.roomie.api;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public interface Callback<T> {
    void onResult(T result);
}
