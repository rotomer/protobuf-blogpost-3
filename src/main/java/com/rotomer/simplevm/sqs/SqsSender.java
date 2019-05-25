package com.rotomer.simplevm.sqs;

import com.google.inject.Inject;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import static com.rotomer.simplevm.sqs.SqsClientFactory.createSqsClient;

public class SqsSender {

    private final SqsClient _sqsClient;

    @Inject
    public SqsSender(final SqsSettings sqsSettings) {
        _sqsClient = createSqsClient(sqsSettings);
    }

    @SuppressWarnings("UnusedReturnValue")
    public String sendMessage(final String queueUrl, final String messageBody) {
        final var sendMessageResponse = _sqsClient.sendMessage(
                SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(messageBody)
                        .build());

        return sendMessageResponse.messageId();
    }
}
