package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.*;

public class MathParser {
    public static Double eval(String expr) {
        if (expr == null || expr.isEmpty()) return null;
        try {
            // Only allow numbers, operators, parentheses, and spaces
            if (!expr.matches("[0-9+\-*/(). ]+")) return null;
            return evalExpr(expr);
        } catch (Exception e) { return null; }
    }
    // Simple recursive descent parser for +, -, *, /
    private static Double evalExpr(String expr) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < expr.length()) ? expr.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            Double parse() {
                nextChar();
                Double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            Double parseExpression() {
                Double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            Double parseTerm() {
                Double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            Double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                Double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
    public static List<String> extractMarkers(String s) {
        List<String> markers = new ArrayList<>();
        if (s == null) return markers;
        Matcher m = Pattern.compile("\\$\\{([^}]+)\\}").matcher(s);
        while (m.find()) markers.add(m.group(1));
        return markers;
    }
} 