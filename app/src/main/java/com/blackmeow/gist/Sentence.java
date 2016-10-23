package com.blackmeow.gist.activities;

public class Sentence {
    String sen;
    String normal;
    int pos;
    int score;

    Sentence(String s, int p) {
        normal = s;
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            char x = s.charAt(i);
            if (x != 32) {
                str = str + x;
            }
        }
        sen = str;
        pos = p;
        score = 0;
    }
}
