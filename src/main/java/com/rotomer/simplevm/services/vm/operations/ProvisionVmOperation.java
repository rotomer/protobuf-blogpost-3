package com.rotomer.simplevm.services.vm.operations;

import com.google.inject.Inject;
import com.rotomer.simplevm.hypervisor.Hypervisor;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.VmProvisionedEvent;
import com.rotomer.simplevm.services.Operation;

public class ProvisionVmOperation implements Operation<ProvisionVmCommand, VmProvisionedEvent> {

    private final Hypervisor _hypervisor;

    @Inject
    public ProvisionVmOperation(final Hypervisor hypervisor) {
        _hypervisor = hypervisor;
    }

    @Override
    public VmProvisionedEvent processCommand(final ProvisionVmCommand command) {
        // Doing some mock logic here for the sake of brevity:

        final var vm = _hypervisor.provisionVm(command.getVmSpec(), command.getRegion());

        return VmProvisionedEvent.newBuilder()
                .setCommand(command)
                .setVmId(vm.id())
                .build();
    }
}