package com.rotomer.simplevm.services.vm.di;

import com.google.inject.AbstractModule;
import com.rotomer.simplevm.services.Service;
import com.rotomer.simplevm.services.vm.VmService;
import com.rotomer.simplevm.sqs.ListenerSettings;
import com.rotomer.simplevm.sqs.SqsSettings;
import com.typesafe.config.Config;

public class VmServiceModule extends AbstractModule {

    private final SqsSettings _sqsSettings;
    private final ListenerSettings _listenerSettings;

    public VmServiceModule(final Config config) {
        super();

        _sqsSettings = SqsSettings.fromConfig(config.getConfig("simplevm.vm-service.sqs"));
        _listenerSettings = ListenerSettings.fromConfig(config.getConfig("simplevm.vm-service.listener"));
    }

    @Override
    protected void configure() {
        bind(Service.class).to(VmService.class);

        bind(SqsSettings.class).toInstance(_sqsSettings);
        bind(ListenerSettings.class).toInstance(_listenerSettings);
    }
}
