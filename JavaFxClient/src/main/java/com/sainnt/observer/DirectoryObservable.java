package com.sainnt.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class DirectoryObservable {


    private final List<DirectoryObserver> observerList = new ArrayList<>();



    public void addObserver(DirectoryObserver observer){
        observerList.add(observer);
        startObserving(observer);
    }

    public void removeObserver(DirectoryObserver observer){
        observerList.remove(observer);
        stopObserving(observer);
    }

    protected abstract void startObserving(DirectoryObserver observer);
    protected abstract void stopObserving(DirectoryObserver observer);


}
