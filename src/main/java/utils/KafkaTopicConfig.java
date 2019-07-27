package utils;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "topics")
class KafkaTopicConfig {
    @Value("${hostname}")
    private String hostname;

    @Value("${player1.topic}")
    private String topicName1;

    @Value("${player2.topic}")
    private String topicName2;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, hostname);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {

        return new NewTopic(topicName1, 1, (short) 1);
    }

    @Bean
    public NewTopic topic2() {

        return new NewTopic(topicName2, 1, (short) 1);
    }


}
