package com.sainnt.files;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileRepresentation implements FileRepresentation {
    private final Path path;
    private boolean isDirectory;
    private boolean firstTimeLeaf = true;

    public LocalFileRepresentation(Path path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public String getName() {
        if(path.getFileName()==null)
            return "";
        return path.normalize().getFileName().toString();
    }

    @Override
    public boolean isDirectory() {
        if(firstTimeLeaf) {
            isDirectory = Files.isDirectory(path);
            firstTimeLeaf = false;
        }
        return isDirectory;
    }

    @Override
    public List<FileRepresentation> getChildren() {
        if (!isDirectory)
            return List.of();
        try {
            return
                    Files.list(path)
                            .filter(this::filterFiles)
                            .map(LocalFileRepresentation::new)
                            .collect(Collectors.toList());
        } catch (IOException e) {
            throw  new RuntimeException(e);
        }
    }

    @Override
    public File getFile() {
        return path.toFile();
    }


    @Override
    public void copyFileToDirectory(File file) {
        if(isDirectory){
            try {
                Files.copy(file.toPath(), path.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean filterFiles(Path file){
        try {
            return !Files.isHidden(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
