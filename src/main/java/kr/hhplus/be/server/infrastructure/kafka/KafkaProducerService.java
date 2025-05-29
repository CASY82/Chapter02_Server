package kr.hhplus.be.server.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Message sent to topic " + topic + ": " + message);
    }
    
    public void sendMessage(String topic, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, jsonMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("Message sent to topic " + topic + ": " + jsonMessage +
                                    ", Partition: " + result.getRecordMetadata().partition() +
                                    ", Offset: " + result.getRecordMetadata().offset());
                        } else {
                            System.err.println("Failed to send message to topic " + topic + ": " + ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize message: " + e.getMessage());
        }
    }
}