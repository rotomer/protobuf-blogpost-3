package com.rotomer.simplevm.sqs;

import com.google.inject.Inject;
import com.rotomer.simplevm.services.Service;
import io.vavr.collection.List;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import static com.rotomer.simplevm.sqs.SqsClientFactory.createSqsClient;

public class SqsListener implements AutoCloseable {

    private final SqsClient _sqsClient;
    private final ListenerSettings _listenerSettings;
    private final Service _service;

    private volatile boolean _stopFlag = false;

    @Inject
    public SqsListener(final SqsSettings sqsSettings,
                       final AwsCredentialsProvider awsCredentialsProvider,
                       final ListenerSettings listenerSettings,
                       final Service service) {
        _sqsClient = createSqsClient(sqsSettings, awsCredentialsProvider);
        _listenerSettings = listenerSettings;
        _service = service;
    }

    public void start() {
        while (!_stopFlag) {
            receiveMessages()
                    .forEach(this::processMessage);
        }

        _sqsClient.close();
    }

    @Override
    public void close() {
        _stopFlag = true;
    }

    private List<Message> receiveMessages() {
        final var receiveMessageResponse = askForMessages();

        return handleReceiveMessageResponse(receiveMessageResponse);
    }

    private ReceiveMessageResponse askForMessages() {
        return _sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                        .queueUrl(_listenerSettings.queueUrl())
                        .waitTimeSeconds(_listenerSettings.waitTimeSeconds())
                        .build());
    }

    private List<Message> handleReceiveMessageResponse(final ReceiveMessageResponse receiveMessageResponse) {
        return List.ofAll(receiveMessageResponse.messages());
    }

    private void processMessage(final Message sqsMessage) {
        _service.processMessage(sqsMessage.body());
        deleteMessage(sqsMessage);
    }

    private void deleteMessage(final Message sqsMessage) {
        _sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(_listenerSettings.queueUrl())
                .receiptHandle(sqsMessage.receiptHandle())
                .build());
    }
}
