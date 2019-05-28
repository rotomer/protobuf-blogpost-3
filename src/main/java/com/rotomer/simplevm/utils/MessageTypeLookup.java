package com.rotomer.simplevm.utils;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.util.function.Supplier;

public class MessageTypeLookup {

    private final Map<String, Supplier<Message.Builder>> _messageBuilderSupplierLookupMap;

    private MessageTypeLookup(final Map<String, Supplier<Message.Builder>> messageBuilderSupplierLookupMap) {
        _messageBuilderSupplierLookupMap = messageBuilderSupplierLookupMap;
    }

    Supplier<Message.Builder> getBuilderForTypeUrl(final String fullProtobufTypeUrl) {
        return _messageBuilderSupplierLookupMap.get(getTypeUrlSuffix(fullProtobufTypeUrl))
                .getOrNull();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static String getTypeUrlSuffix(final String fullProtobufTypeUrl) {
        // removing the type url's prefix. see - https://developers.google.com/protocol-buffers/docs/proto3#any
        final var splits = fullProtobufTypeUrl.split("/");

        return splits[splits.length - 1];
    }

    public static class Builder {
        private Map<String, Supplier<Message.Builder>> _messageBuilderMap;

        public Builder() {
            _messageBuilderMap = HashMap.empty();
        }

        public Builder addMessageTypeMapping(final Descriptors.Descriptor messageTypeDescriptor,
                                             final Supplier<Message.Builder> messageBuilderSupplier) {
            _messageBuilderMap = _messageBuilderMap.put(messageTypeDescriptor.getFullName(), messageBuilderSupplier);

            return this;
        }

        public MessageTypeLookup build() {
            return new MessageTypeLookup(_messageBuilderMap);
        }
    }
}

