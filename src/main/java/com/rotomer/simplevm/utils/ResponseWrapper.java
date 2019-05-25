package com.rotomer.simplevm.utils;

import com.google.protobuf.Any;
import com.google.protobuf.Message;

import static com.rotomer.simplevm.utils.ProtobufEncoderDecoder.encodeMessageBase64;

public class ResponseWrapper {

    public static <T extends Message> String wrapResponseMessage(final T message) {
        final Any wrappedEvent = Any.pack(message);
        return encodeMessageBase64(wrappedEvent);
    }
}
