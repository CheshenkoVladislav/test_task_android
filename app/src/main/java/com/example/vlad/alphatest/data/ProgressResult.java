package com.example.vlad.alphatest.data;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ProgressResult<T> {
    private T result;
    private long progress;
    private String errorMessage;

    public ProgressResult(T result, long progress) {
        this.result = result;
        this.progress = progress;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProgressResult{" +
                "result=" + result +
                ", progress=" + progress +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgressResult<?> result1 = (ProgressResult<?>) o;
        return progress == result1.progress &&
                Objects.equals(result, result1.result) &&
                Objects.equals(errorMessage, result1.errorMessage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(result, progress, errorMessage);
    }
}
