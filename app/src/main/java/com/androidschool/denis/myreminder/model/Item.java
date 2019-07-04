package com.androidschool.denis.myreminder.model;

public interface Item {
    //данный интерфейс будем использовать в списках.

    //чтобы отличать таски от сепараторов(разделителей на экране).
    boolean isTask();
}
