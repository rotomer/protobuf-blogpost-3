package com.rotomer.simplevm.utils;

import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.*;

public class ProtobufAnyJsonPacker {

    // unless you host a schema repository like https://github.com/spotify/proto-registry then this prefix is just a
    // place holder:
    private static final String COMPANY_TYPE_URL_PREFIX = "type.cloudshareapis.com";
    private static final JsonFormat.Printer _jsonPrinter = JsonFormat.printer();

    public static AnyJson pack(final Message message) throws InvalidProtocolBufferException {
        final var typeUrl = getTypeUrl(message.getDescriptorForType());
        final var jsonSerializedValue = _jsonPrinter.print(message);

        return AnyJson.newBuilder()
                .setTypeUrl(typeUrl)
                .setJsonSerializedValue(jsonSerializedValue)
                .build();
    }

    private static String getTypeUrl(final Descriptors.Descriptor descriptor) {
        return COMPANY_TYPE_URL_PREFIX + "/" + descriptor.getFullName();
    }
}