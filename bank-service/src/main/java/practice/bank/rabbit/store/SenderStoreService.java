package practice.bank.rabbit.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:rabbitBank.properties")
@RequiredArgsConstructor
@Service
@Log4j2
public class SenderStoreService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.from.bank.to.store}")
    private String queueFromBankToStore;


    public void send(SenderStorePayload senderStorePayload) {
        log.info("Send storePayload object to store-service. Payload: {}", senderStorePayload);
        rabbitTemplate.convertAndSend(queueFromBankToStore, senderStorePayload);
    }
}
