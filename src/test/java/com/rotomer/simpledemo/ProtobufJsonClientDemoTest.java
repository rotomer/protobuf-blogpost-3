package com.rotomer.simpledemo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.ProvisionVmCommand;
import com.rotomer.simplevm.messages.Region;
import com.rotomer.simplevm.messages.VmSpec;
import org.junit.Test;

import java.util.UUID;

public class ProtobufJsonClientDemoTest {

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

        final var jsonPrinter = JsonFormat.printer();
        final var json = jsonPrinter.print(provisionVmCommand);

        vmProvisioningService.provisionVm(json);
    }
}

