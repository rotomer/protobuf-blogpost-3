package com.rotomer.simplevm.services.vm;

import com.rotomer.simplevm.sqs.SqsSettings;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.NoSuchElementException;

import static com.rotomer.simplevm.sqs.SqsClientFactory.createSqsClient;

class SqsMessageReceiver {

    private static final int NUMBER_OF_RECEIVE_MESSAGE_ATTEMPTS = 10;

    private final SqsClient _sqsClient;

    SqsMessageReceiver(final SqsSettings sqsSettings) {
        _sqsClient = createSqsClient(sqsSettings, EnvironmentVariableCredentialsProvider.create());
    }

    String receiveSingleMessage(@SuppressWarnings("SameParameterValue") final String queueUrl) {
        // When receiving messages from an SQS queue, you need to repeatedly call sqs:ReceiveMessage.
        // Note that multiple receive message attempts are needed because of the eventual consistency guarantee of SQS.
        // i.e. SQS does NOT guarantee that a message can be received right after it was sent to the queue. It's
        // guarantee is that EVENTUALLY it can be received.
        // See the following link for reference - https://stackoverflow.com/a/31030493/1952591

        for (int i = 0; i < NUMBER_OF_RECEIVE_MESSAGE_ATTEMPTS; i++) {
            final ReceiveMessageResponse receiveMessageResponse = receiveMessageHelper(queueUrl);

            if (receiveMessageResponse.messages() != null && !receiveMessageResponse.messages().isEmpty()) {
                final Message message = receiveMessageResponse.messages().get(0);
                deleteMessage(queueUrl, message.receiptHandle());

                return message.body();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        throw new NoSuchElementException("ReceiveSqsMessage yielded zero messages");
    }

    private ReceiveMessageResponse receiveMessageHelper(final String queueUrl) {
        return _sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(1)
                        .build());
    }

    private void deleteMessage(final String queueUrl, final String receiptHandle) {
        _sqsClient.deleteMessage(
                DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(receiptHandle)
                        .build());
    }
}
