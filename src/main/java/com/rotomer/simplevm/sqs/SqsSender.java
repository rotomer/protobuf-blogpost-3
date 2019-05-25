package com.rotomer.simplevm.sqs;

import com.google.inject.Inject;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static com.rotomer.simplevm.sqs.SqsClientFactory.createSqsClient;

public class SqsSender {

    private final SqsClient _sqsClient;

    @Inject
    public SqsSender(final SqsSettings sqsSettings,
                     final AwsCredentialsProvider awsCredentialsProvider) {
        _sqsClient = createSqsClient(sqsSettings, awsCredentialsProvider);
    }

    @SuppressWarnings("UnusedReturnValue")
    public String sendMessage(final String queueUrl, final String messageBody) {
        final SendMessageResponse sendMessageResponse = _sqsClient.sendMessage(
                SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(messageBody)
                        .build());

        return sendMessageResponse.messageId();
    }
}
