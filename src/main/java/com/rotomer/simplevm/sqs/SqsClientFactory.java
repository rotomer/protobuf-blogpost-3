package com.rotomer.simplevm.sqs;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SqsClientFactory {

    public static SqsClient createSqsClient(final SqsSettings sqsSettings,
                                            final AwsCredentialsProvider awsCredentialsProvider) {
        return SqsClient.builder()
                .endpointOverride(sqsSettings.sqsAwsServiceEndpoint())
                .credentialsProvider(awsCredentialsProvider)
                .region(sqsSettings.awsRegion())
                .build();
    }
}
