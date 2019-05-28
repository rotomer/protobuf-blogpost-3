package com.rotomer.simplevm.services;

import com.google.protobuf.InvalidProtocolBufferException;

public interface Service {
    void processMessage(final String sqsMessageBody) throws InvalidProtocolBufferException;
}
