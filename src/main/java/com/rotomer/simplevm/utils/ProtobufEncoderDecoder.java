package com.rotomer.simplevm.utils;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class ProtobufEncoderDecoder {
    public static String encodeMessageBase64(final Message protobufMessage) {
        final byte[] binarySer = protobufMessage.toByteArray();
        return BaseEncoding.base64().encode(binarySer);
    }

    public static <B extends Message.Builder> B decodeMessageBase64(final String encodedMessage, final B messageBuilder) {
        final byte[] binarySer = BaseEncoding.base64().decode(encodedMessage);

        try {
            messageBuilder.mergeFrom(binarySer);
        } catch (InvalidProtocolBufferException e) {
            throw new ProtobufDecodingException(e);
        }

        return messageBuilder;
    }

    static class ProtobufDecodingException extends RuntimeException {
        ProtobufDecodingException(final Exception e) {
            super(e);
        }
    }
}
