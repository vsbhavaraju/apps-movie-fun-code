package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileStore implements BlobStore {
    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.getName());
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.getInputStream(), outputStream);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        Path blobPath = Paths.get(name);

        if (Files.exists(blobPath))
            return Optional.of(new Blob(name, Files.newInputStream(blobPath), new Tika().detect(blobPath)));
        else
            return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
