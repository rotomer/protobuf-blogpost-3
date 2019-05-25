package com.rotomer.simplevm.services.vm.di;

import com.google.inject.AbstractModule;
import com.rotomer.simplevm.services.ResponseSettings;
import com.rotomer.simplevm.services.Service;
import com.rotomer.simplevm.services.vm.VmService;
import com.rotomer.simplevm.sqs.ListenerSettings;
import com.rotomer.simplevm.sqs.SqsSettings;
import com.typesafe.config.Config;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public class VmServiceModule extends AbstractModule {

    private final SqsSettings _sqsSettings;
    private final ListenerSettings _listenerSettings;
    private final ResponseSettings _responseSettings;
    private final AwsCredentialsProvider _awsCredentialsProvider;

    public VmServiceModule(final Config config, final AwsCredentialsProvider awsCredentialsProvider) {
        super();

        _sqsSettings = SqsSettings.fromConfig(config.getConfig("simplevm.vm-service.sqs"));
        _listenerSettings = ListenerSettings.fromConfig(config.getConfig("simplevm.vm-service.listener"));
        _responseSettings = ResponseSettings.fromConfig(config.getConfig("simplevm.vm-service.response"));
        _awsCredentialsProvider = awsCredentialsProvider;
    }

    @Override
    protected void configure() {
        bind(Service.class).to(VmService.class);

        bind(SqsSettings.class).toInstance(_sqsSettings);
        bind(ListenerSettings.class).toInstance(_listenerSettings);
        bind(ResponseSettings.class).toInstance(_responseSettings);

        bind(AwsCredentialsProvider.class).toInstance(_awsCredentialsProvider);
    }
}
