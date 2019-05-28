package com.rotomer.anydemo;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.Region;
import com.rotomer.simplevm.messages.VmCommandEnvelope;
import com.rotomer.simplevm.messages.VmSpec;
import org.junit.Test;

import java.util.UUID;

public class ProtobufJsonAnyClientDemo {

    @Test
    public void testProvisionVm() throws InvalidProtocolBufferException {

        final var vmProvisioningService = new VmProvisioningService();

        final var provisionVmCommand = ProvisionVmCommand.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRegion(Region.US)
                .setVmSpec(VmSpec.newBuilder()
                        .setCpuCores(2)
                        .setGbRam(4))
                .build();

        final var envelope = VmCommandEnvelope.newBuilder()
                .setInnerMessage(Any.pack(provisionVmCommand))
                .build();

        final var typeRegistry = JsonFormat.TypeRegistry.newBuilder()
                .add(ProvisionVmCommand.getDescriptor())
                .build();
        final var jsonPrinter = JsonFormat.printer()
                .usingTypeRegistry(typeRegistry);

        final var json = jsonPrinter.print(envelope);

        vmProvisioningService.provisionVm(json);
    }
}
