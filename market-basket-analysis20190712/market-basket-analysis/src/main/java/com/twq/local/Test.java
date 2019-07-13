package com.twq.local;

import java.util.HashSet;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        Set<String> s1 = new HashSet<String>();
        s1.add("A");
        s1.add("B");
        s1.add("D");

        Set<String> s2 = new HashSet<String>();
        s2.add("A");
        s2.add("f");
        s2.add("B");

        s1.retainAll(s2);
        System.out.println(s1);

    }
}
