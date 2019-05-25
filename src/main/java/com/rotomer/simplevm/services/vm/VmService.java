package com.rotomer.simplevm.services.vm;

import com.google.inject.Inject;
import com.rotomer.simplevm.messages.VmCommandEnvelope;
import com.rotomer.simplevm.services.Service;
import com.rotomer.simplevm.sqs.SqsSender;

import static com.rotomer.simplevm.utils.ProtobufEncoderDecoder.decodeMessageBase64;
import static com.rotomer.simplevm.utils.ResponseWrapper.wrapResponseMessage;

public class VmService implements Service {

    private final VmMessageUnpacker _vmMessageUnpacker;
    private final SqsSender _sqsSender;
    private final VmOperationLookup _vmOperationLookup;

    @Inject
    VmService(final VmMessageUnpacker vmMessageUnpacker,
              final SqsSender sqsSender,
              final VmOperationLookup vmOperationLookup) {
        _vmMessageUnpacker = vmMessageUnpacker;
        _sqsSender = sqsSender;
        _vmOperationLookup = vmOperationLookup;
    }

    @Override
    public void processMessage(final String sqsMessageBody) {
        final var vmCommandEnvelope = decodeMessageBase64(sqsMessageBody, VmCommandEnvelope.newBuilder())
                .build();

        final var innerMessage = _vmMessageUnpacker.unpack(vmCommandEnvelope.getInnerMessage());
        final var operation = _vmOperationLookup.getOperationByMessage(innerMessage);

        //noinspection unchecked
        var event = operation.processCommand(innerMessage);

        final var encodedEvent = wrapResponseMessage(event);
        _sqsSender.sendMessage(vmCommandEnvelope.getResponseQueueUrl(), encodedEvent);
    }

}
