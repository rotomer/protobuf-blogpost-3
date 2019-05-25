package com.rotomer.simplevm.services;

public interface Service {
    void processMessage(final String sqsMessageBody);
}
