package com.sainnt.views;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.function.Function;

public class CustomListBinder {
    public static <SRC, DEST> void bindLists(
            ObservableList<DEST> dest, ObservableList<SRC> src, Function<SRC, DEST> transformer) {
        for (SRC a : src) {
            dest.add(transformer.apply(a));
        }
        src.addListener((ListChangeListener.Change<? extends SRC> c) -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (SRC a : c.getRemoved()) {
                        int from = c.getFrom();
                        dest.remove(from);
                    }
                }
                if (c.wasAdded()) {
                    for (SRC a : c.getAddedSubList()) {
                        int indexAdded = src.indexOf(a);
                        dest.add(indexAdded, transformer.apply(a));
                    }
                }
            }
        });
    }

}
