package com.sainnt.observer;

import com.sainnt.files.LocalFileRepresentation;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LocalDirectoryObservable extends DirectoryObservable{

    //Singletone:
    public static LocalDirectoryObservable getInstance(){
        return instance;
    }

    private static final LocalDirectoryObservable instance;

    static {
        instance = new LocalDirectoryObservable();
        Thread observeThread = new Thread(instance::observeLoop);
        observeThread.setDaemon(true);
        observeThread.start();
    }

    private LocalDirectoryObservable() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final WatchService watchService;
    private final HashSet<DirectoryObserver> observersToBeRemoved = new HashSet<>();
    private final Map<WatchKey,DirectoryObserver> mapKeysToObservers = new HashMap<>();

    @Override
    protected void startObserving(DirectoryObserver observer) {
        try {
            File file = observer
                    .getDirectory()
                    .getFile();
            WatchKey key = file
                    .toPath()
                    .register(watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
            mapKeysToObservers.put(key,observer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void stopObserving(DirectoryObserver observer) {
        observersToBeRemoved.add(observer);
    }

    @SneakyThrows
    private void observeLoop(){
        while(true){
            WatchKey key = watchService.take();
            key.pollEvents().forEach(event -> processEvent(key,event));
            if(!key.reset()) {
                System.out.println("Key not valid " + key.watchable());
            }
            if(keyIsRemoved(key))
                key.cancel();
        }
    }
    private void processEvent(WatchKey key ,WatchEvent<?> event){
        DirectoryObserver observer = mapKeysToObservers.get(key);
        LocalFileRepresentation repr = new LocalFileRepresentation(relativeToAbsolutePath(observer,(Path)event.context()));
        if(event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
            observer.fileAdded(repr);
        else if(event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))
            observer.fileRemoved(repr);
        else if(event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
            observer.fileModified(repr);
    }
    private Path relativeToAbsolutePath(DirectoryObserver observer, Path path){
        return observer.getDirectory().getFile().toPath().resolve(path);
    }


    private boolean keyIsRemoved(WatchKey key){
        DirectoryObserver o = mapKeysToObservers.get(key);
        if(observersToBeRemoved.contains(o))
        {
            observersToBeRemoved.remove(o);
            mapKeysToObservers.remove(key);
            return true;
        }
        return  false;
    }




}
