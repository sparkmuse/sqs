package com.example.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class SqsController {

    static private final String QUEUE_NAME = "sqs-test";

    private final QueueMessagingTemplate messagingTemplate;
    private final AmazonSQS amazonSQS;

    @GetMapping(value = "/send")
    public String sendMessage() {

        Person person = new Person("John Doe", 100);
        messagingTemplate.convertAndSend(QUEUE_NAME, person, Map.of("hello", "hi"));

        return "Success send";
    }


    // Using the normal receive with only sdks, this is useful because it
    // gives us more flexibility with the sdk, setting delays, timeouts...
    @GetMapping(value = "/get-sdk")
    public String receiveDirectSDK() {

        String queueUrl = amazonSQS.getQueueUrl(QUEUE_NAME).getQueueUrl();
        ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl)
                .withMaxNumberOfMessages(1)
                .withMessageAttributeNames("hello");
        List<com.amazonaws.services.sqs.model.Message> messages = amazonSQS.receiveMessage(request).getMessages();

        com.amazonaws.services.sqs.model.Message receive = messages.get(0);

        System.out.println("I received person " + receive.getBody());
        System.out.println("I received headers " + receive.getMessageAttributes().toString());
        System.out.println("I received header hello=" + receive.getMessageAttributes().get("hello"));

        return "Success received";
    }


    // Using the normal receive with only Strings, this is useful because it
    // gives us the headers
    @GetMapping(value = "/get-object")
    public String receiveMessageObject() {

        Person person = messagingTemplate.receiveAndConvert(QUEUE_NAME, Person.class);

        System.out.println("I received person " + person.toString());

        return "Success received";
    }


    // Using the normal receive with only Strings, this is useful because it
    // gives us the headers
    @GetMapping(value = "/get-string")
    public String receiveMessage() {

        Message<?> receive = messagingTemplate.receive(QUEUE_NAME);

        System.out.println("I received person " + receive.getPayload().toString());
        System.out.println("I received headers " + receive.getHeaders().toString());
        System.out.println("I received header hello=" + receive.getHeaders().get("hello"));

        return "Success received";
    }

    // Using the annotations work 100%
    // @SqsListener(QUEUE_NAME)
    public void receiveMessage(Person person, @Headers Map<String, String> headers, @Header("hello") String helloHeader) {

        System.out.println("I received person " + person.toString());
        System.out.println("I received headers " + headers.toString());
        System.out.println("I received header hello=" + helloHeader);
    }
}
