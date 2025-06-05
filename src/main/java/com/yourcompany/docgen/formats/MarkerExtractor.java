package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkerExtractor {
    public static class Descriptor {
        public List<String> markers = new ArrayList<>();
        public Map<String, List<String>> attributes = new HashMap<>();
    }

    public static Descriptor extractDescriptor(String template) {
        Descriptor desc = new Descriptor();
        if (template == null || template.isEmpty()) return desc;
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String raw = matcher.group(1).trim();
            // Clean unwanted spaces
            raw = raw.replaceAll("\\s+", " ").trim();
            desc.markers.add(raw);
            // Detect multiple attributes (comma separated)
            String[] parts = raw.split(",");
            List<String> attrs = new ArrayList<>();
            for (String part : parts) {
                String cleaned = part.trim();
                attrs.add(cleaned);
            }
            desc.attributes.put(raw, attrs);
        }
        return desc;
    }
} 