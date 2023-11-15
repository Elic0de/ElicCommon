package com.github.elic0de.eliccommon.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static <T> Optional<T> loadFromJsonFile(File file, Class<? super T> type) {
        Validate.isTrue(file.isDirectory(), "must be a file");

        final T object;
        if (!file.exists()) return Optional.empty();

        try {
            object = gson.fromJson(new JsonReader(new FileReader(file)), type);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(object);
    }


    public static <T> Iterable<T> loadFromJsonFileAll(File folder, Class<? super T> type) {
        Validate.isTrue(folder.isFile(), "must be a folder");

        final Set<T> iterator = new HashSet<>();
        for (File file : Optional.ofNullable(folder.listFiles()).orElse(new File[0])) {
            final String fileName = file.getName();
            final T object;

            try {
                object = gson.fromJson(new JsonReader(new FileReader(new File(folder, fileName))), type);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            iterator.add(object);
        }
        return iterator;
    }

    public static void saveJsonFile(File file, Object object) throws IOException {
        Validate.isTrue(file.isDirectory(), "file must be a file");

        // Create parent directories
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("Unable to create parent directories for file " + file.getAbsolutePath());
            }
        }
        // Create file
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Unable to create file " + file.getAbsolutePath());
            }
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
