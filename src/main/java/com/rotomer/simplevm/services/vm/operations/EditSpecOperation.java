package com.rotomer.simplevm.services.vm.operations;

import com.google.inject.Inject;
import com.rotomer.simplevm.hypervisor.Hypervisor;
import com.rotomer.simplevm.messages.EditSpecCommand;
import com.rotomer.simplevm.messages.SpecEditedEvent;
import com.rotomer.simplevm.services.AbstractOperation;
import com.rotomer.simplevm.services.ResponseSettings;
import com.rotomer.simplevm.sqs.SqsSender;

public class EditSpecOperation extends AbstractOperation<EditSpecCommand, SpecEditedEvent> {

    private final Hypervisor _hypervisor;

    @Inject
    public EditSpecOperation(final Hypervisor hypervisor,
                             final SqsSender sqsSender,
                             final ResponseSettings responseSettings) {
        super(sqsSender, responseSettings);
        _hypervisor = hypervisor;
    }

    @Override
    protected SpecEditedEvent doProcessing(final EditSpecCommand command) {
        // Doing some mock logic here for the sake of brevity:

        _hypervisor.setVmSpec(command.getVmId(), command.getVmSpec());

        return SpecEditedEvent.newBuilder()
                .setCommand(command)
                .build();
    }
}