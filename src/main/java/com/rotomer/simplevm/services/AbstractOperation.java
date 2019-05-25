package com.rotomer.simplevm.services;

import com.google.protobuf.Message;
import com.rotomer.simplevm.sqs.SqsSender;

import static com.rotomer.simplevm.utils.ResponseWrapper.wrapResponseMessage;

public abstract class AbstractOperation<C extends Message, E extends Message> implements Operation<C> {
    private final SqsSender _sqsSender;
    private final ResponseSettings _responseSettings;

    protected AbstractOperation(final SqsSender sqsSender,
                                final ResponseSettings responseSettings) {
        _sqsSender = sqsSender;
        _responseSettings = responseSettings;
    }

    @Override
    public void processCommand(final C command) {
        final E event = doProcessing(command);

        final String encodedEvent = wrapResponseMessage(event);
        _sqsSender.sendMessage(_responseSettings.queueUrl(), encodedEvent);
    }

    protected abstract E doProcessing(final C command);
}
