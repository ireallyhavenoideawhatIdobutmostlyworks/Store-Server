package practice.store.rabbit.services.bank;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerBankService {


    @RabbitListener(queues = "${queue.from.bank.to.store}")
    public void receivedMessage(ConsumerBankPayload consumerBankPayload) {
        // ToDo add logic edit order entity if ConsumerBankPayload.isPaymentSuccess = true
    }
}
