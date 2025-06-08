package com.yourcompany.docgen.formats;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class HelperUtils {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    public static String uniqueId() {
        return UUID.randomUUID().toString();
    }

    public static String randomId(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static String safeFilename(String filename) {
        if (filename == null) return null;
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public static Map<String, String> readDirectory(String dirPath, String extension) throws IOException {
        if (dirPath == null || extension == null) return new HashMap<>();
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) return new HashMap<>();
        
        return Files.walk(dir)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(extension))
            .collect(Collectors.toMap(
                p -> p.getFileName().toString(),
                p -> {
                    try {
                        return Files.readString(p);
                    } catch (IOException e) {
                        return "";
                    }
                }
            ));
    }

    public static void copyDirectory(String sourcePath, String targetPath) throws IOException {
        if (sourcePath == null || targetPath == null) return;
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);
        
        if (!Files.exists(source)) return;
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        Files.walk(source)
            .forEach(sourceFile -> {
                try {
                    Path targetFile = target.resolve(source.relativize(sourceFile));
                    if (Files.isDirectory(sourceFile)) {
                        if (!Files.exists(targetFile)) {
                            Files.createDirectories(targetFile);
                        }
                    } else {
                        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }

    public static boolean removeDirectory(String dirPath) {
        if (dirPath == null) return false;
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) return false;
        
        try {
            Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            return true;
        } catch (UncheckedIOException e) {
            return false;
        }
    }

    public static String cleanJsVar(String input) {
        if (input == null) return null;
        return input.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    public static String removeQuotes(String input) {
        if (input == null) return null;
        return input.replaceAll("^[\"']|[\"']$", "");
    }

    public static int stringDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return -1;
        if (s1.equals(s2)) return 0;
        
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    public static Object getPathValue(Map<String, Object> map, String path) {
        if (map == null || path == null) return null;
        String[] keys = path.split("\\.");
        Object current = map;
        
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current;
    }

    public static <T> List<T> deduplicate(List<T> list) {
        if (list == null) return null;
        return new ArrayList<>(new LinkedHashSet<>(list));
    }
} 