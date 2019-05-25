package com.rotomer.simplevm.services.vm.operations;

import com.google.inject.Inject;
import com.rotomer.simplevm.hypervisor.Hypervisor;
import com.rotomer.simplevm.messages.StopVmCommand;
import com.rotomer.simplevm.messages.VmStoppedEvent;
import com.rotomer.simplevm.services.Operation;

import static com.rotomer.simplevm.services.vm.model.VmState.Stopped;

public class StopVmOperation implements Operation<StopVmCommand, VmStoppedEvent> {

    private final Hypervisor _hypervisor;

    @Inject
    public StopVmOperation(final Hypervisor hypervisor) {
        _hypervisor = hypervisor;
    }

    @Override
    public VmStoppedEvent processCommand(StopVmCommand command) {
        // Doing some mock logic here for the sake of brevity:

        _hypervisor.changeVmState(command.getVmId(), Stopped);

        return VmStoppedEvent.newBuilder()
                .setCommand(command)
                .build();
    }
}
