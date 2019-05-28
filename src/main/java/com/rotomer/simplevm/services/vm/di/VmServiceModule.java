package com.rotomer.simplevm.services.vm.di;

import com.google.inject.AbstractModule;
import com.rotomer.simplevm.messages.*;
import com.rotomer.simplevm.services.Service;
import com.rotomer.simplevm.services.vm.VmService;
import com.rotomer.simplevm.sqs.ListenerSettings;
import com.rotomer.simplevm.sqs.SqsSettings;
import com.rotomer.simplevm.utils.MessageTypeLookup;
import com.rotomer.simplevm.utils.ProtobufAnyJsonUnpacker;
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
        final var messageTypeLookup = MessageTypeLookup.newBuilder()
                .addMessageTypeMapping(ProvisionVmCommand.getDescriptor(), ProvisionVmCommand::newBuilder)
                .addMessageTypeMapping(VmProvisionedEvent.getDescriptor(), VmProvisionedEvent::newBuilder)
                .addMessageTypeMapping(EditSpecCommand.getDescriptor(), EditSpecCommand::newBuilder)
                .addMessageTypeMapping(SpecEditedEvent.getDescriptor(), SpecEditedEvent::newBuilder)
                .addMessageTypeMapping(StopVmCommand.getDescriptor(), StopVmCommand::newBuilder)
                .addMessageTypeMapping(VmStoppedEvent.getDescriptor(), VmStoppedEvent::newBuilder)
                .build();

        bind(ProtobufAnyJsonUnpacker.class).toInstance(new ProtobufAnyJsonUnpacker(messageTypeLookup));
        bind(Service.class).to(VmService.class);

        bind(SqsSettings.class).toInstance(_sqsSettings);
        bind(ListenerSettings.class).toInstance(_listenerSettings);
    }
}
