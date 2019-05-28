package com.rotomer.simpledemo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.services.vm.model.Vm;

class VmProvisioningService {

    void provisionVm(final String json) throws InvalidProtocolBufferException {
        final var jsonParser = JsonFormat.parser();
        final var provisionVmCommandBuilder = ProvisionVmCommand.newBuilder();

        jsonParser.merge(json, provisionVmCommandBuilder);
        final var provisionVmCommand = provisionVmCommandBuilder.build();

        doProvisioning(provisionVmCommand);
    }

    private void doProvisioning(final ProvisionVmCommand provisionVmCommand) {
        // Doing some mock logic here for the sake of brevity:

        final Vm vm = Vm.create(provisionVmCommand.getVmSpec(), provisionVmCommand.getRegion());
        System.out.println("Provisioned vm:\n" + vm);
    }
}