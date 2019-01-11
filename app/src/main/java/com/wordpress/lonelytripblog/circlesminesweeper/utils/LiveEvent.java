package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import androidx.annotation.Nullable;

public class LiveEvent<T> {
    private final T data;
    private boolean wasObserved;

    public LiveEvent(T data) {
        this.data = data;
    }

    @Nullable
    public T getValueOrNull() {
        if (wasObserved) return null;
        wasObserved = true;
        return data;
    }
}
