package com.yourcompany.docgen.formats;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class HelperUtils {
    public static String uniqueId() {
        return UUID.randomUUID().toString();
    }
    public static String randomId(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
    public static String safeFilename(String s) {
        if (s == null) return null;
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    public static Map<String, String> readDirectory(String dir, String ext) throws IOException {
        Map<String, String> files = new HashMap<>();
        File d = new File(dir);
        if (!d.exists() || !d.isDirectory()) return files;
        for (File f : Objects.requireNonNull(d.listFiles())) {
            if (ext == null || f.getName().endsWith(ext)) {
                files.put(f.getName(), new String(Files.readAllBytes(f.toPath())));
            }
        }
        return files;
    }
    public static boolean removeDirectory(String dir) {
        File d = new File(dir);
        if (!d.exists()) return false;
        File[] files = d.listFiles();
        if (files != null) for (File f : files) f.delete();
        return d.delete();
    }
    public static void copyDirectory(String src, String dest) throws IOException {
        File srcDir = new File(src);
        File destDir = new File(dest);
        if (!destDir.exists()) destDir.mkdirs();
        for (File f : Objects.requireNonNull(srcDir.listFiles())) {
            Files.copy(f.toPath(), new File(destDir, f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    public static String cleanJsVar(String s) {
        if (s == null) return null;
        return s.replaceAll("[^a-zA-Z0-9_]", "_");
    }
    public static String removeQuotes(String s) {
        if (s == null) return null;
        if (s.startsWith("\"") && s.endsWith("\"")) return s.substring(1, s.length() - 1);
        if (s.startsWith("'") && s.endsWith("'")) return s.substring(1, s.length() - 1);
        return s;
    }
    public static int stringDistance(String a, String b) {
        if (a == null || b == null) return -1;
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
    public static Object getPathValue(Map<String, Object> map, String path) {
        if (map == null || path == null) return null;
        String[] parts = path.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (current instanceof Map) current = ((Map<?, ?>) current).get(part);
            else return null;
        }
        return current;
    }
    public static <T> List<T> deduplicate(List<T> arr) {
        if (arr == null) return null;
        return new ArrayList<>(new LinkedHashSet<>(arr));
    }
} 