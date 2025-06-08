package com.yourcompany.docgen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class DataParser {
    private final ObjectMapper mapper;

    public DataParser() {
        this.mapper = new ObjectMapper();
    }

    public List<Map<String, Object>> parseJsonArray(String filePath) throws Exception {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try {
            return mapper.readValue(file, List.class);
        } catch (Exception e) {
            throw new Exception("Failed to parse JSON array: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> parseJsonObject(String jsonPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonPath), Map.class);
    }
} 