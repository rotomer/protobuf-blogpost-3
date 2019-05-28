package com.rotomer.simplevm.services.vm;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.rotomer.simplevm.messages.*;
import com.rotomer.simplevm.services.vm.di.VmServiceModule;
import com.rotomer.simplevm.sqs.SqsListener;
import com.rotomer.simplevm.sqs.SqsSender;
import com.rotomer.simplevm.sqs.SqsSettings;
import com.rotomer.simplevm.utils.AnyJsonPacker;
import com.rotomer.simplevm.utils.MessageTypeLookup;
import com.rotomer.simplevm.utils.AnyJsonUnpacker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.rotomer.simplevm.services.vm.EmbeddedSqsTestFixture.*;
import static com.rotomer.simplevm.utils.IdGenerator.nextId;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.Assert.assertEquals;

public class VmServiceFunctionalTest {

    private final static Config _config = ConfigFactory.parseString(
            "simplevm {\n" +
                    "vm-service {\n" +
                    "sqs {\n" +
                    "sqs-aws-service-endpoint = " + "\"" + SQS_AWS_SERVICE_ENDPOINT + "\"\n" +
                    "aws-region = " + AWS_REGION + "\n" +
                    "}\n" +
                    "listener {\n" +
                    "wait-time-seconds = 20\n" +
                    "queue-url = \"" + REQUEST_QUEUE_URL + "\"\n" +
                    "}\n" +
                    "}\n" +
                    "}");

    private static SqsSender _sqsSender;
    private static AnyJsonUnpacker _anyJsonUnpacker;
    private static SqsMessageReceiver _sqsMessageReceiver;
    private static EmbeddedSqsTestFixture _embeddedSqsTestFixture;
    private static SqsListener _unitUnderTest;

    @BeforeClass
    public static void setUp() {
        _embeddedSqsTestFixture = new EmbeddedSqsTestFixture();
        _embeddedSqsTestFixture.start();

        final var sqsSettings = SqsSettings.fromConfig(_config.getConfig("simplevm.vm-service.sqs"));
        _sqsSender = new SqsSender(sqsSettings);
        _sqsMessageReceiver = new SqsMessageReceiver(sqsSettings);

        final var messageTypeLookup = MessageTypeLookup.newBuilder()
                .addMessageTypeMapping(VmProvisionedEvent.getDescriptor(), VmProvisionedEvent::newBuilder)
                .addMessageTypeMapping(SpecEditedEvent.getDescriptor(), SpecEditedEvent::newBuilder)
                .addMessageTypeMapping(VmStoppedEvent.getDescriptor(), VmStoppedEvent::newBuilder)
                .build();
        _anyJsonUnpacker = new AnyJsonUnpacker(messageTypeLookup);

        _unitUnderTest = startUnitUnderTest();
    }

    @AfterClass
    public static void tearDown() {
        _embeddedSqsTestFixture.close();
    }

    private static SqsListener startUnitUnderTest() {
        final Injector injector = Guice.createInjector(new VmServiceModule(_config));
        _unitUnderTest = injector.getInstance(SqsListener.class);

        newSingleThreadExecutor().submit(_unitUnderTest::start);

        return _unitUnderTest;
    }

    @Test
    public void testProvisionVm() throws InvalidProtocolBufferException {
        // arrange:
        final var provisionVmCommand = ProvisionVmCommand.newBuilder()
                .setId(nextId())
                .setRegion(Region.US)
                .setVmSpec(VmSpec.newBuilder()
                        .setCpuCores(2)
                        .setGbRam(4))
                .build();
        final var anyMessage = AnyJsonPacker.pack(provisionVmCommand);
        final var envelope = VmJsonEnvelope.newBuilder()
                .setInnerMessage(anyMessage)
                .setVmId(provisionVmCommand.getId())
                .setResponseQueueUrl(RESPONSE_QUEUE_URL)
                .build();
        final var json = JsonFormat.printer().print(envelope);

        // act:
        _sqsSender.sendMessage(REQUEST_QUEUE_URL, json);
        final var jsonResponse = _sqsMessageReceiver.receiveSingleMessage(RESPONSE_QUEUE_URL);

        // assert:
        final var anyJsonBuilder = AnyJson.newBuilder();
        JsonFormat.parser().merge(jsonResponse, anyJsonBuilder);
        final var anyJsonResponseMessage = anyJsonBuilder.build();

        final var protobufAnyJsonUnpacker = new AnyJsonUnpacker(MessageTypeLookup.newBuilder()
                .addMessageTypeMapping(VmProvisionedEvent.getDescriptor(), VmProvisionedEvent::newBuilder)
                .build());
        final var actualResponse = (VmProvisionedEvent) protobufAnyJsonUnpacker.unpack(anyJsonResponseMessage);

        assertEquals(provisionVmCommand, actualResponse.getCommand());
    }

    @Test
    public void testEditSpec() throws InvalidProtocolBufferException {
        // arrange:
        final var editSpecCommand = EditSpecCommand.newBuilder()
                .setId(nextId())
                .setVmId(nextId())
                .setVmSpec(VmSpec.newBuilder()
                        .setCpuCores(2)
                        .setGbRam(4))
                .build();
        final var anyMessage = AnyJsonPacker.pack(editSpecCommand);
        final var envelope = VmJsonEnvelope.newBuilder()
                .setInnerMessage(anyMessage)
                .setVmId(editSpecCommand.getId())
                .setResponseQueueUrl(RESPONSE_QUEUE_URL)
                .build();
        final var json = JsonFormat.printer().print(envelope);

        // act:
        _sqsSender.sendMessage(REQUEST_QUEUE_URL, json);
        final var jsonResponse = _sqsMessageReceiver.receiveSingleMessage(RESPONSE_QUEUE_URL);

        // assert:
        final var anyJsonBuilder = AnyJson.newBuilder();
        JsonFormat.parser().merge(jsonResponse, anyJsonBuilder);
        final var anyJsonResponseMessage = anyJsonBuilder.build();

        final var actualResponse = (SpecEditedEvent) _anyJsonUnpacker.unpack(anyJsonResponseMessage);
        assertEquals(editSpecCommand, actualResponse.getCommand());
    }

    @Test
    public void testStopVm() throws InvalidProtocolBufferException {
        // arrange:
        final var stopVmCommand = StopVmCommand.newBuilder()
                .setId(nextId())
                .setVmId(nextId())
                .build();
        final var anyMessage = AnyJsonPacker.pack(stopVmCommand);
        final var envelope = VmJsonEnvelope.newBuilder()
                .setInnerMessage(anyMessage)
                .setVmId(stopVmCommand.getId())
                .setResponseQueueUrl(RESPONSE_QUEUE_URL)
                .build();
        final var json = JsonFormat.printer().print(envelope);

        // act:
        _sqsSender.sendMessage(REQUEST_QUEUE_URL, json);
        final var jsonResponse = _sqsMessageReceiver.receiveSingleMessage(RESPONSE_QUEUE_URL);

        // assert:
        final var anyJsonBuilder = AnyJson.newBuilder();
        JsonFormat.parser().merge(jsonResponse, anyJsonBuilder);
        final var anyJsonResponseMessage = anyJsonBuilder.build();

        final var actualResponse = (VmStoppedEvent) _anyJsonUnpacker.unpack(anyJsonResponseMessage);
        assertEquals(stopVmCommand, actualResponse.getCommand());
    }
}
