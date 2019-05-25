package com.rotomer.simplevm.services.vm;

import com.google.inject.Inject;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.rotomer.simplevm.messages.EditSpecCommand;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.StopVmCommand;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.util.NoSuchElementException;

public class VmMessageUnpacker {

    private final Map<String, Class<? extends Message>> _messageMap;

    @Inject
    public VmMessageUnpacker() {
        // (this initialization can be generalized by using reflection)
        _messageMap = HashMap.of(
                ProvisionVmCommand.getDescriptor().getFullName(), ProvisionVmCommand.class,
                EditSpecCommand.getDescriptor().getFullName(), EditSpecCommand.class,
                StopVmCommand.getDescriptor().getFullName(), StopVmCommand.class);
    }

    Message unpack(final Any anyMessage) {
        final String messageTypeName = extractMessageTypeName(anyMessage);
        final Class<? extends Message> clazz = _messageMap.get(messageTypeName)
                .getOrElseThrow(() -> new NoSuchElementException("Message type not recognized."));

        return unpack(anyMessage, clazz);
    }

    private String extractMessageTypeName(final Any anyMessage) {
        final String typeUrl = anyMessage.getTypeUrl();
        final String[] splits = typeUrl.split("/"); // removing the type url's prefix. see - https://developers.google.com/protocol-buffers/docs/proto3#any

        return splits[splits.length - 1];
    }

    private <M extends Message> M unpack(final Any anyMessage, final Class<M> clazz) {
        try {
            return anyMessage.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            throw new ProtobufUnpackingException(e);
        }
    }

    static class ProtobufUnpackingException extends RuntimeException {
        ProtobufUnpackingException(final Exception e) {
            super(e);
        }
    }

}
