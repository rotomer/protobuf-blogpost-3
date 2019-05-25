package com.rotomer.simplevm.services;

import com.google.protobuf.Message;

public interface Operation<C extends Message, E extends Message> {
    E processCommand(final C command);
}
