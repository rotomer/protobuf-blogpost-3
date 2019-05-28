package com.rotomer.anydemo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.VmCommandEnvelope;
import com.rotomer.simplevm.services.vm.model.Vm;

class VmProvisioningService {

    void provisionVm(final String json) throws InvalidProtocolBufferException {
        final var typeRegistry = JsonFormat.TypeRegistry.newBuilder()
                .add(ProvisionVmCommand.getDescriptor())
                .build();
        final var jsonParser = JsonFormat.parser()
                .usingTypeRegistry(typeRegistry);

        final var envelopeBuilder = VmCommandEnvelope.newBuilder();
        jsonParser.merge(json, envelopeBuilder);

        final var envelope = envelopeBuilder.build();
        final var anyInnerMessage = envelope.getInnerMessage();
        final var provisionVmCommand = anyInnerMessage.unpack(ProvisionVmCommand.class);

        doProvisioning(provisionVmCommand);
    }

    private void doProvisioning(final ProvisionVmCommand provisionVmCommand) {
        // Doing some mock logic here for the sake of brevity:

        final Vm vm = Vm.create(provisionVmCommand.getVmSpec(), provisionVmCommand.getRegion());
        System.out.println("Provisioned vm:\n" + vm);
    }
}