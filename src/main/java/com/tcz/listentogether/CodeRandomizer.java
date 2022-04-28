package com.tcz.listentogether;

public class CodeRandomizer {

    private static int startCode = 65; //A
    private static int endCode = 90; //Z
    private static int codeSize = 6;

    public static String random() {
        String code = "";

        for (int i = 0; i < codeSize; i++) {
            char sym = (char) ((Math.random() * (endCode - startCode)) + startCode);
            code+=sym;
        }

        return code;
    }
}
