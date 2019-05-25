package com.rotomer.simplevm.sqs;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Value.Immutable
public abstract class SqsSettings {

    public static SqsSettings fromConfig(final Config config) {
        return ImmutableSqsSettings.builder()
                .sqsAwsServiceEndpoint(URI.create(config.getString("sqs-aws-service-endpoint")))
                .awsRegion(Region.of(config.getString("aws-region")))
                .build();
    }

    public abstract URI sqsAwsServiceEndpoint();

    public abstract Region awsRegion();

}