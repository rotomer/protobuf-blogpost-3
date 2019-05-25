package com.rotomer.simplevm.services.vm;

import com.rotomer.simplevm.sqs.ImmutableSqsSettings;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import java.net.URI;

import static com.rotomer.simplevm.sqs.SqsClientFactory.createSqsClient;
import static software.amazon.awssdk.regions.Region.US_EAST_1;

public class EmbeddedSqsTestFixture implements AutoCloseable {

    final static Region AWS_REGION = US_EAST_1;
    private final static int EMBEDDED_SQS_SERVER_PORT = 9324;
    final static URI SQS_AWS_SERVICE_ENDPOINT = URI.create("http://localhost:" + EMBEDDED_SQS_SERVER_PORT);
    private final static String REQUEST_QUEUE_NAME = "VmRequestQueue";
    static final String REQUEST_QUEUE_URL = getEmbeddedQueueUrl(REQUEST_QUEUE_NAME);
    private final static String RESPONSE_QUEUE_NAME = "VmResponseQueue";
    static final String RESPONSE_QUEUE_URL = getEmbeddedQueueUrl(RESPONSE_QUEUE_NAME);

    private final SqsClient _sqsClient;

    private SQSRestServer _embeddedSqsServer;

    EmbeddedSqsTestFixture() {
        final var sqsSettings = ImmutableSqsSettings.builder()
                .awsRegion(AWS_REGION)
                .sqsAwsServiceEndpoint(SQS_AWS_SERVICE_ENDPOINT)
                .build();
        _sqsClient = createSqsClient(sqsSettings);
    }

    private static String getEmbeddedQueueUrl(final String queueName) {
        return SQS_AWS_SERVICE_ENDPOINT + "/queue/" + queueName;
    }

    void start() {
        _embeddedSqsServer = createAndStartEmbeddedSqsServer();
        createQueuesForTest();
    }

    @Override
    public void close() {
        _embeddedSqsServer.stopAndWait();
    }

    private void createQueuesForTest() {
        _sqsClient.createQueue(CreateQueueRequest.builder()
                .queueName(REQUEST_QUEUE_NAME)
                .build());
        _sqsClient.createQueue(CreateQueueRequest.builder()
                .queueName(RESPONSE_QUEUE_NAME)
                .build());
    }

    private SQSRestServer createAndStartEmbeddedSqsServer() {
        final var embeddedSqsServer = SQSRestServerBuilder
                .withPort(EMBEDDED_SQS_SERVER_PORT)
                .withInterface("localhost")
                .start();

        embeddedSqsServer.waitUntilStarted();

        return embeddedSqsServer;
    }
}
