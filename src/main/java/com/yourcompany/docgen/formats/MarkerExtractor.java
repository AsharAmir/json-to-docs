package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.*;

public class MarkerExtractor {
    private static final Pattern MARKER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("\\s*,\\s*");

    public static class Descriptor {
        public final List<String> markers;
        public final Map<String, List<String>> attributes;

        public Descriptor() {
            this.markers = new ArrayList<>();
            this.attributes = new HashMap<>();
        }
    }

    public static Descriptor extractDescriptor(String text) {
        Descriptor descriptor = new Descriptor();
        if (text == null) {
            return descriptor;
        }

        Matcher matcher = MARKER_PATTERN.matcher(text);
        while (matcher.find()) {
            String content = matcher.group(1).trim();
            if (!content.isEmpty()) {
                descriptor.markers.add(content);
                
                // Split attributes by comma and clean spaces
                String[] attributes = ATTRIBUTE_PATTERN.split(content);
                List<String> cleanAttributes = new ArrayList<>();
                for (String attr : attributes) {
                    String cleanAttr = attr.trim();
                    if (!cleanAttr.isEmpty()) {
                        cleanAttributes.add(cleanAttr);
                    }
                }
                
                descriptor.attributes.put(content, cleanAttributes);
            }
        }

        return descriptor;
    }
} 