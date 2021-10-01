package practice.store.order.rabbit.pdfService;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.store.order.OrderEntity;
import practice.store.order.rabbit.pdfService.detaills.CustomerPdfDetails;
import practice.store.order.rabbit.pdfService.detaills.OrderPdfDetails;
import practice.store.order.rabbit.pdfService.detaills.ProductPdfDetails;
import practice.store.product.ProductEntity;
import practice.store.utils.converter.EntitiesConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:rabbit.properties")
@Service
public class PublisherPdfService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private EntitiesConverter converter;

    @Value("${queue.to.pdf}")
    private String queueToPdf;


    public void send(OrderEntity order, List<ProductEntity> productEntityList) throws JsonProcessingException {
        CustomerPdfDetails customerPdfDetails = prepareCustomerDetails(order);
        OrderPdfDetails orderPdfDetails = prepareOrderDetails(order);
        List<ProductPdfDetails> productPdfDetailsList = preparePdfDetails(productEntityList);

        PublisherPdfPayload payload = preparePublisherPayload(customerPdfDetails, orderPdfDetails, productPdfDetailsList);

        ObjectMapper objectMapper = createObjectMapper();
        String payloadAsString = objectMapper.writeValueAsString(payload);

        rabbitTemplate.convertAndSend(queueToPdf, payloadAsString);
    }


    private PublisherPdfPayload preparePublisherPayload(CustomerPdfDetails customerPdfDetails, OrderPdfDetails orderPdfDetails, List<ProductPdfDetails> productPdfDetailsList) {
        return PublisherPdfPayload.builder()
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

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }
}