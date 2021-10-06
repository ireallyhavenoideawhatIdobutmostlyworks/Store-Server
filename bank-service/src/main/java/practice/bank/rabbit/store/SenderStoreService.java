package practice.bank.rabbit.store;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:rabbitBank.properties")
@Service
public class SenderStoreService {

    @Autowired
    private RabbitTemplate template;

    @Value("${queue.from.bank.to.store}")
    private String queueFromBankToStore;


    public void send(SenderStorePayload senderStorePayload) {
        template.convertAndSend(queueFromBankToStore, senderStorePayload);
    }
}
