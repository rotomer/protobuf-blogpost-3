package com.rotomer.simplevm.sqs;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SqsClientFactory {

    public static SqsClient createSqsClient(final SqsSettings sqsSettings) {
        return SqsClient.builder()
                .endpointOverride(sqsSettings.sqsAwsServiceEndpoint())
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(sqsSettings.awsRegion())
                .build();
    }
}
