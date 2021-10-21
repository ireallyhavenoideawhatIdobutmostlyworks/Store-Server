package practice.bank.rabbit.store;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:rabbitBank.properties")
@Service
@Log4j2
public class SenderStoreService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.from.bank.to.store}")
    private String queueFromBankToStore;


    public void send(SenderStorePayload senderStorePayload) {
        log.info("Send storePayload object to store-service. Payload: {}", senderStorePayload);
        rabbitTemplate.convertAndSend(queueFromBankToStore, senderStorePayload);
    }
}
