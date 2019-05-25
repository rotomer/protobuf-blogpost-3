package com.rotomer.simplevm.services;

import com.google.protobuf.Message;

public interface Operation<C extends Message> {
    void processCommand(final C command);
}
