package com.rotomer.simplevm.utils;

import java.util.UUID;

public class IdGenerator {
    public static String nextId() {
        return UUID.randomUUID().toString();
    }
}
