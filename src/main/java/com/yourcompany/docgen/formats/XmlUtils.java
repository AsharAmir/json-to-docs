package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.*;

public class XmlUtils {
    private static final Pattern MARKER_PATTERN = Pattern.compile("\\$\\{[^}]+\\}");
    private static final Pattern XML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    public static List<String> flattenXml(String xml) {
        if (xml == null) {
            return new ArrayList<>();
        }

        List<String> parts = new ArrayList<>();
        Matcher matcher = XML_TAG_PATTERN.matcher(xml);
        int lastEnd = 0;

        while (matcher.find()) {
            // Add text before tag if any
            if (matcher.start() > lastEnd) {
                parts.add(xml.substring(lastEnd, matcher.start()));
            }
            // Add the tag
            parts.add(matcher.group());
            lastEnd = matcher.end();
        }

        // Add remaining text if any
        if (lastEnd < xml.length()) {
            parts.add(xml.substring(lastEnd));
        }

        return parts;
    }

    public static String removeMarkers(String text) {
        if (text == null) {
            return null;
        }
        return MARKER_PATTERN.matcher(text).replaceAll("");
    }

    public static String removeXml(String text) {
        if (text == null) {
            return null;
        }
        return XML_TAG_PATTERN.matcher(text).replaceAll("");
    }
} 