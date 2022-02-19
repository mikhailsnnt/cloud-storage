package com.sainnt.files;


import com.sainnt.exception.FileAlreadyExistsException;
import com.sainnt.exception.FileRenamingFailedException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalFileRepresentation implements FileRepresentation {
    private final Path path;
    private boolean isDirectory;
    private boolean firstTimeLeaf = true;
    private boolean firstTimeLoad = true;
    private final ObservableList<FileRepresentation> children;

    public LocalFileRepresentation(Path path) {
        this.path = path;
        this.children = FXCollections.observableArrayList();
    }

    @Override
    public String getPath() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public String getName() {
        if (path.getFileName() == null)
            return "";
        return path.normalize().getFileName().toString();
    }

    @Override
    public boolean isFile() {
        if (firstTimeLeaf) {
            isDirectory = Files.isDirectory(path);
            firstTimeLeaf = false;
        }
        return !isDirectory;
    }

    @Override
    public ObservableList<FileRepresentation> getChildren() {
        return children;
    }


    public File getFile() {
        return path.toFile();
    }


    @Override
    public void copyFileToDirectory(File file) {
        if (isDirectory) {
            try {
                Files.copy(file.toPath(), path.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void loadContent() {
        if (firstTimeLoad && isDirectory)
            loadChildren();
    }

    @Override
    public void setName(String name) throws FileAlreadyExistsException, FileRenamingFailedException {
        Path newPath = path.getParent().resolve(name);
        if (Files.exists(newPath))
            throw new FileAlreadyExistsException(newPath.toString());
        if (!path.toFile().renameTo(newPath.toFile()))
            throw new FileRenamingFailedException(newPath.toString());
    }

    private boolean filterFiles(Path file) {
        try {
            return !Files.isHidden(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void loadChildren() {
        try {
            children.clear();
            Files.list(path)
                    .filter(this::filterFiles)
                    .map(LocalFileRepresentation::new).forEach(children::add);
            firstTimeLoad = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
