package com.rotomer.simplevm.services.vm.operations;

import com.google.inject.Inject;
import com.rotomer.simplevm.hypervisor.Hypervisor;
import com.rotomer.simplevm.messages.EditSpecCommand;
import com.rotomer.simplevm.messages.SpecEditedEvent;
import com.rotomer.simplevm.services.Operation;

public class EditSpecOperation implements Operation<EditSpecCommand, SpecEditedEvent> {

    private final Hypervisor _hypervisor;

    @Inject
    public EditSpecOperation(final Hypervisor hypervisor) {
        _hypervisor = hypervisor;
    }

    @Override
    public SpecEditedEvent processCommand(EditSpecCommand command) {
        // Doing some mock logic here for the sake of brevity:

        _hypervisor.setVmSpec(command.getVmId(), command.getVmSpec());

        return SpecEditedEvent.newBuilder()
                .setCommand(command)
                .build();
    }
}