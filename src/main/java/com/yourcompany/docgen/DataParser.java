package com.yourcompany.docgen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.Map;

public class DataParser {
    public List<Map<String, Object>> parseJsonArray(String jsonPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonPath), List.class);
    }
} 