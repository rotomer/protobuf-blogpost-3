package com.rotomer.simplevm.services.vm;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.VmJsonEnvelope;
import com.rotomer.simplevm.services.Service;
import com.rotomer.simplevm.sqs.SqsSender;
import com.rotomer.simplevm.utils.AnyJsonPacker;
import com.rotomer.simplevm.utils.AnyJsonUnpacker;


public class VmService implements Service {

    private final JsonFormat.Parser _jsonParser;
    private final JsonFormat.Printer _jsonPrinter;
    private final AnyJsonUnpacker _anyJsonUnpacker;
    private final SqsSender _sqsSender;
    private final VmOperationLookup _vmOperationLookup;

    @Inject
    VmService(final AnyJsonUnpacker anyJsonUnpacker,
              final SqsSender sqsSender,
              final VmOperationLookup vmOperationLookup) {
        _jsonParser = JsonFormat.parser();
        _jsonPrinter = JsonFormat.printer();
        _anyJsonUnpacker = anyJsonUnpacker;
        _sqsSender = sqsSender;
        _vmOperationLookup = vmOperationLookup;
    }

    @Override
    public void processMessage(final String sqsMessageBody) throws InvalidProtocolBufferException {
        final var envelopeBuilder = VmJsonEnvelope.newBuilder();
        _jsonParser.merge(sqsMessageBody, envelopeBuilder);
        final var vmCommandEnvelope = envelopeBuilder.build();
        final var innerMessage = _anyJsonUnpacker.unpack(vmCommandEnvelope.getInnerMessage());

        final var operation = _vmOperationLookup.getOperationByMessage(innerMessage);
        //noinspection unchecked
        var event = operation.processCommand(innerMessage);

        final var wrappedEvent = AnyJsonPacker.pack(event);
        final var jsonResponse = _jsonPrinter.print(wrappedEvent);

        _sqsSender.sendMessage(vmCommandEnvelope.getResponseQueueUrl(), jsonResponse);
    }

}
