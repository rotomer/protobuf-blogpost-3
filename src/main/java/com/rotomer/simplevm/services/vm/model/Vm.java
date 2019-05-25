package com.rotomer.simplevm.services.vm.model;

import com.rotomer.simplevm.messages.Region;
import com.rotomer.simplevm.messages.VmSpec;
import org.immutables.value.Value;

import static com.rotomer.simplevm.services.vm.model.VmState.Running;
import static com.rotomer.simplevm.utils.IdGenerator.nextId;

@Value.Immutable
public abstract class Vm {
    public static Vm create(final VmSpec vmSpec, final Region region) {
        return ImmutableVm.builder()
                .id(nextId())
                .vmSpec(vmSpec)
                .region(region)
                .state(Running)
                .build();
    }

    public abstract String id();

    public abstract VmSpec vmSpec();

    public abstract Region region();

    public abstract VmState state();
}
