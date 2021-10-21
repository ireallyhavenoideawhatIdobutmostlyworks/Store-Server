package practice.store.rabbit.services.bank;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ConsumerBankService {


    @RabbitListener(queues = "${queue.from.bank.to.store}")
    public void receivedMessage(ConsumerBankPayload consumerBankPayload) {
        log.info("Consume bankPayload object from bank-service. Payload: {}", consumerBankPayload);
        // ToDo add logic edit order entity if ConsumerBankPayload.isPaymentSuccess = true
    }
}
