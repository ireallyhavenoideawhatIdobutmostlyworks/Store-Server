package practice.bank.rabbit.mail;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:rabbitBank.properties")
@Service
public class SenderMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.from.bank.to.email}")
    private String queueFromBankToEmail;


    public void send(SenderMailPayload senderMailPayload) {
        rabbitTemplate.convertAndSend(queueFromBankToEmail, senderMailPayload);
    }
}
