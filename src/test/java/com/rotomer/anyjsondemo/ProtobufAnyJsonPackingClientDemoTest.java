package com.rotomer.anyjsondemo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.Region;
import com.rotomer.simplevm.messages.VmJsonEnvelope;
import com.rotomer.simplevm.messages.VmSpec;
import com.rotomer.simplevm.utils.AnyJsonPacker;
import org.junit.Test;

import java.util.UUID;

public class ProtobufAnyJsonPackingClientDemoTest {

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

        final var envelope = VmJsonEnvelope.newBuilder()
                .setInnerMessage(AnyJsonPacker.pack(provisionVmCommand))
                .build();

        final var json = JsonFormat.printer().print(envelope);

        vmProvisioningService.provisionVm(json);
    }
}
