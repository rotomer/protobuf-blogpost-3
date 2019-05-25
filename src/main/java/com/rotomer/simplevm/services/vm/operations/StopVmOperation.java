package com.rotomer.simplevm.services.vm.operations;

import com.google.inject.Inject;
import com.rotomer.simplevm.hypervisor.Hypervisor;
import com.rotomer.simplevm.messages.StopVmCommand;
import com.rotomer.simplevm.messages.VmStoppedEvent;
import com.rotomer.simplevm.services.AbstractOperation;
import com.rotomer.simplevm.services.ResponseSettings;
import com.rotomer.simplevm.sqs.SqsSender;

import static com.rotomer.simplevm.services.vm.model.VmState.Stopped;

public class StopVmOperation extends AbstractOperation<StopVmCommand, VmStoppedEvent> {

    private final Hypervisor _hypervisor;

    @Inject
    public StopVmOperation(final Hypervisor hypervisor,
                           final SqsSender sqsSender,
                           final ResponseSettings responseSettings) {
        super(sqsSender, responseSettings);
        _hypervisor = hypervisor;
    }

    @Override
    protected VmStoppedEvent doProcessing(final StopVmCommand command) {
        // Doing some mock logic here for the sake of brevity:

        _hypervisor.changeVmState(command.getVmId(), Stopped);

        return VmStoppedEvent.newBuilder()
                .setCommand(command)
                .build();
    }
}
