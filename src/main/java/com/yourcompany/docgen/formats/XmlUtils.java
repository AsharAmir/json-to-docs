package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.*;

public class XmlUtils {
    public static List<String> flattenXml(String xml) {
        List<String> parts = new ArrayList<>();
        if (xml == null) return parts;
        Matcher m = Pattern.compile("(<[^>]+>|[^<]+)").matcher(xml);
        while (m.find()) parts.add(m.group());
        return parts;
    }
    public static String removeMarkers(String xml) {
        if (xml == null) return null;
        return xml.replaceAll("\\$\\{[^}]+\\}", "");
    }
    public static String removeXml(String xml) {
        if (xml == null) return null;
        return xml.replaceAll("<[^>]+>", "");
    }
} 