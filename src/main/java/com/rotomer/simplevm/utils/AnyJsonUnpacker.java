package com.rotomer.simplevm.utils;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.AnyJson;

public class AnyJsonUnpacker {
    private final JsonFormat.Parser jsonParser;
    private final MessageTypeLookup _messageTypeLookup;

    @Inject
    public AnyJsonUnpacker(final MessageTypeLookup messageTypeLookup) {
        _messageTypeLookup = messageTypeLookup;
        jsonParser = JsonFormat.parser();
    }

    public Message unpack(final AnyJson anyJsonMessage) throws InvalidProtocolBufferException {
        final var messageBuilder = _messageTypeLookup.getBuilderForTypeUrl(anyJsonMessage.getTypeUrl())
                .get();

        jsonParser.merge(anyJsonMessage.getJsonSerializedValue(), messageBuilder);

        return messageBuilder.build();
    }
}