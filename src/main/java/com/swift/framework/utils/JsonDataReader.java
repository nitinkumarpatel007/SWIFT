package com.swift.framework.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Reads JSON test-data files so page data stays separate from test logic. */
public final class JsonDataReader {

    private static final Gson GSON = new Gson();
    private static final Path TEST_DATA_DIR = Paths.get("src", "test", "resources", "testdata");

    private JsonDataReader() {
    }

    public static JsonObject read(String fileName) {
        Path path = TEST_DATA_DIR.resolve(fileName);
        try (Reader reader = new FileReader(path.toFile())) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read test data file: " + path, e);
        }
    }
}
