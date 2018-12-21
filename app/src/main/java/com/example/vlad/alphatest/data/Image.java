package com.example.vlad.alphatest.data;

import android.support.annotation.NonNull;

import java.util.Objects;

public class Image {
    private String id;
    private String url;

    public Image() {
    }

    public Image(String id, String url) {
        this.url = url;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(id, image.id) &&
                Objects.equals(url, image.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, url);
    }
}
