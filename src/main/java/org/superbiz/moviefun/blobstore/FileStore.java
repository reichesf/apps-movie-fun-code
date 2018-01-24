package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File(getCoverFileName(blob.getName()));

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile))
        {
            outputStream.write(IOUtils.toByteArray(blob.getInputStream()));
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        // ...
        Path coverFilePath = getExistingCoverPath(name);
        FileInputStream stream = new FileInputStream(coverFilePath.toFile());
        String contentType = new Tika().detect(coverFilePath);

        Blob blob = new Blob(name,stream,contentType);

        return Optional.of(blob);

    }

    @Override
    public void deleteAll() {
        // ...
    }
    private Path getExistingCoverPath(String albumId) throws IOException {
        File coverFile = new File(getCoverFileName(albumId));
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {

            try {
                coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new IOException("Couldn't read file", e);
            }
        }
        return coverFilePath;
    }


    private String getCoverFileName(String albumId) {
        return "covers/" + albumId;
    }

 }
