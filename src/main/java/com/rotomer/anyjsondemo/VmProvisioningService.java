package com.rotomer.anyjsondemo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.VmJsonEnvelope;
import com.rotomer.simplevm.services.vm.model.Vm;
import com.rotomer.simplevm.utils.AnyJsonUnpacker;
import com.rotomer.simplevm.utils.MessageTypeLookup;

class VmProvisioningService {

    void provisionVm(final String json) throws InvalidProtocolBufferException {
        final var jsonParser = JsonFormat.parser();
        final var envelopeBuilder = VmJsonEnvelope.newBuilder();
        jsonParser.merge(json, envelopeBuilder);
        final var vmCommandEnvelope = envelopeBuilder.build();

        final var messageTypeLookup = MessageTypeLookup.newBuilder()
                .addMessageTypeMapping(ProvisionVmCommand.getDescriptor(), ProvisionVmCommand::newBuilder)
                .build();
        final var anyJsonUnpacker = new AnyJsonUnpacker(messageTypeLookup);
        final var provisionVmCommand = (ProvisionVmCommand) anyJsonUnpacker.unpack(vmCommandEnvelope.getInnerMessage());

        doProvisioning(provisionVmCommand);
    }

    private void doProvisioning(final ProvisionVmCommand provisionVmCommand) {
        // Doing some mock logic here for the sake of brevity:

        final Vm vm = Vm.create(provisionVmCommand.getVmSpec(), provisionVmCommand.getRegion());
        System.out.println("Provisioned vm:\n" + vm);
    }
}
