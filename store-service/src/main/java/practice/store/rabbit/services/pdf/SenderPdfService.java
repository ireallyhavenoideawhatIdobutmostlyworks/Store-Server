package practice.store.rabbit.services.pdf;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.store.order.OrderEntity;
import practice.store.rabbit.services.pdf.detaills.CustomerPdfDetails;
import practice.store.rabbit.services.pdf.detaills.OrderPdfDetails;
import practice.store.rabbit.services.pdf.detaills.ProductPdfDetails;
import practice.store.product.ProductEntity;
import practice.store.utils.converter.EntitiesConverter;

import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:rabbitStore.properties")
@Service
public class SenderPdfService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private EntitiesConverter converter;

    @Value("${queue.from.store.to.pdf}")
    private String queueFromStoreToPdf;


    public void send(OrderEntity order, List<ProductEntity> productEntityList) {
        CustomerPdfDetails customerPdfDetails = prepareCustomerDetails(order);
        OrderPdfDetails orderPdfDetails = prepareOrderDetails(order);
        List<ProductPdfDetails> productPdfDetailsList = preparePdfDetails(productEntityList);

        SenderPdfPayload payload = preparePublisherPayload(customerPdfDetails, orderPdfDetails, productPdfDetailsList);

        rabbitTemplate.convertAndSend(queueFromStoreToPdf, payload);
    }


    private SenderPdfPayload preparePublisherPayload(CustomerPdfDetails customerPdfDetails, OrderPdfDetails orderPdfDetails, List<ProductPdfDetails> productPdfDetailsList) {
        return SenderPdfPayload.builder()
                .customerPdfDetails(customerPdfDetails)
                .orderPdfDetails(orderPdfDetails)
                .productPdfDetailsList(productPdfDetailsList)
                .build();
    }

    private CustomerPdfDetails prepareCustomerDetails(OrderEntity order) {
        return CustomerPdfDetails
                .builder()
                .email(order.getCustomer().getEmail())
                .username(order.getCustomer().getUsername())
                .city(order.getCustomer().getCity())
                .street(order.getCustomer().getStreet())
                .postalCode(order.getCustomer().getPostalCode())
                .build();
    }

    private OrderPdfDetails prepareOrderDetails(OrderEntity order) {
        return OrderPdfDetails
                .builder()
                .orderUUID(order.getOrderUUID())
                .paymentUUID(order.getPaymentUUID())
                .orderPrice(order.getOrderFinalPrice())
                .accountNumber(order.getAccountNumber())
                .build();
    }

    private List<ProductPdfDetails> preparePdfDetails(List<ProductEntity> productEntityList) {
        return productEntityList
                .stream()
                .map(this::convertOrderProduct)
                .collect(Collectors.toList());
    }

    private ProductPdfDetails convertOrderProduct(ProductEntity product) {
        return ProductPdfDetails.builder()
                .name(product.getName())
                .productUUID(product.getProductUUID())
                .description(product.getDescription())
                .finalPrice(product.getFinalPrice())
                .build();
    }
}