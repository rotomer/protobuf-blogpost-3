package com.rotomer.simplevm.services.vm;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.protobuf.Message;
import com.rotomer.simplevm.messages.EditSpecCommand;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.StopVmCommand;
import com.rotomer.simplevm.services.Operation;
import com.rotomer.simplevm.services.vm.operations.EditSpecOperation;
import com.rotomer.simplevm.services.vm.operations.ProvisionVmOperation;
import com.rotomer.simplevm.services.vm.operations.StopVmOperation;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

class VmOperationLookup {

    private final Provider<ProvisionVmOperation> _provisionVmOperationProvider;
    private final Provider<StopVmOperation> _stopVmOperationProvider;
    private final Provider<EditSpecOperation> _editSpecOperationProvider;

    @Inject
    VmOperationLookup(final Provider<ProvisionVmOperation> provisionVmOperationProvider,
                      final Provider<StopVmOperation> stopVmOperationProvider,
                      final Provider<EditSpecOperation> editSpecOperationProvider) {

        _provisionVmOperationProvider = provisionVmOperationProvider;
        _stopVmOperationProvider = stopVmOperationProvider;
        _editSpecOperationProvider = editSpecOperationProvider;
    }

    Operation getOperationByMessage(final Message message) {
        return Match(message).of(
                Case($(instanceOf(ProvisionVmCommand.class)), _provisionVmOperationProvider.get()),
                Case($(instanceOf(EditSpecCommand.class)), _editSpecOperationProvider.get()),
                Case($(instanceOf(StopVmCommand.class)), _stopVmOperationProvider.get()));
    }
}
