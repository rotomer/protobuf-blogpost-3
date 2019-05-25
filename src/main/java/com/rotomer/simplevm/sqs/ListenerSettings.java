package com.rotomer.simplevm.sqs;

import com.typesafe.config.Config;
import org.immutables.value.Value;

@Value.Immutable
public abstract class ListenerSettings {
    public static ListenerSettings fromConfig(final Config config) {
        return ImmutableListenerSettings.builder()
                .waitTimeSeconds(config.getInt("wait-time-seconds"))
                .queueUrl(config.getString("queue-url"))
                .build();
    }

    public abstract int waitTimeSeconds();

    public abstract String queueUrl();
}
