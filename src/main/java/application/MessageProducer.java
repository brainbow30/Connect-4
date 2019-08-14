package application;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


@Component
class MessageProducer {
    private final Gson gson;
    @Value("${player2.topic}")
    private String topicName2;
    @Value("${player1.topic}")
    private String topicName1;


    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public MessageProducer(Gson gson, KafkaTemplate<String, String> kafkaTemplate) {
        this.gson = gson;
        this.kafkaTemplate = kafkaTemplate;
    }

    private void sendMessage(String topicName, Integer partition, String key, Object value) {

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            byte[] data = bos.toByteArray();
            String toJson = gson.toJson(data);

            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, partition, key, toJson);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    //System.out.println("Sent message=[" + key + ":" + toJson + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("Unable to send message=[" + key + ":" + toJson + "] due to : " + ex.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage1(Integer partition, String key, Board value) {
        sendMessage(topicName1, partition, key, value);
    }

    public void sendMessage2(Integer partition, String key, Board value) {
        sendMessage(topicName2, partition, key, value);

    }

}
