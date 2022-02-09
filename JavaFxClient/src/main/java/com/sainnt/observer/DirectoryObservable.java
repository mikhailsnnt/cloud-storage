package com.sainnt.observer;

public abstract class DirectoryObservable {


    public void addObserver(DirectoryObserver observer) {
        startObserving(observer);
    }

    public void removeObserver(DirectoryObserver observer) {
        stopObserving(observer);
    }

    protected abstract void startObserving(DirectoryObserver observer);

    protected abstract void stopObserving(DirectoryObserver observer);


}
