package com.genymobile.pr.ui;

public interface ItemClickListener<T> {
    void onClick(T item);

    void onLongClick(T item);
}
