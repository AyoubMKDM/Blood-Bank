package com.AyoubMKDM.github.bloodbank;

public class JaccardSimilarity {
    //The code is not prepared to work for any sQ value
    private static int sQ = 2;

    public static int getQ() {
        return sQ;
    }

    public static void setQ(int q) {
        sQ = q;
    }

    public static float jaccard(String[] s1Q_grqms, String[] s2Q_grams) {
        /* s1Q_grams and s2Q_grams must been checked if they are empty
            in this code the checking is in the calling function ²²01+*/
        int sizeOfIntersection = sizeOfIntersection(s1Q_grqms, s2Q_grams);
        int sizeOfUnion = s1Q_grqms.length + s2Q_grams.length - sizeOfIntersection;
        return (float) sizeOfIntersection / sizeOfUnion;
    }

    private static String  union(String[] theLargTable, String[] theSmallTable) {
        String union = "";
        for (int i = 0; i < theLargTable.length; i++) {
            if (i < theSmallTable.length) {
                if (!containsIgnoreCase(union, theLargTable[i]))    union += theLargTable[i];
                if (!containsIgnoreCase(union, theSmallTable[i]))   union += theSmallTable[i];
            } else {
                if (!containsIgnoreCase(union, theLargTable[i]))     union += theLargTable[i];
            }
        }
        return union;
    }

    public static boolean containsIgnoreCase(String src, String what) {
        if (src.length() == 0)
            return false;

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - sQ; i >= 0; i-=sQ) {
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;
            if (src.regionMatches(true, i, what, 0, sQ))
                return true;
        }
        return false;
    }

    public static String[] G(String s, int q) {
        //the function does not check
        // if the s.isEmpty() cuz this checking shall be in the calling function
        if (s.length() == q) {
            return new String[]{s};
        } else {
            String[] sQ_grqms = new String[s.length() - q + 1];
            for (int i = 0; i < s.length() - q + 1; i++) sQ_grqms[i] = s.substring(i, i + q);

            return sQ_grqms;
        }
    }
    //TODO send the G(of the query) cuz now it's calculated more than 11k time

    private static int sizeOfIntersection(String[] s1, String[] s2) {
        int common = 0;
        for (String subString1 : s1){
            for (String subString2 : s2) {
                if (subString1.equalsIgnoreCase(subString2)){
                    common++;
                    break;
                }
            }
        }
        //TODO optimize this loop
        return common;
    }
}