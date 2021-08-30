package practice.store.utils.initdata;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerRepository;
import practice.store.order.OrderEntity;
import practice.store.order.OrderRepository;
import practice.store.order.details.OrderProductRepository;
import practice.store.product.ProductRepository;

@RequiredArgsConstructor
@Transactional
@Component
public class SaveDataToDatabase implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    private final CreateDataStartApp createDataStartApp;


    @Override
    public void run(ApplicationArguments args) {
        createDataStartApp.createCustomers();
        createDataStartApp.createProducts();
        createDataStartApp.createOrders();
        createDataStartApp.createOrderProductDetails();

        addCustomers();
        addProducts();
        addOrders();

        addCustomerToOrders();
        addOrderProductDetails();
        mappingOrderWithOrderProduct();
    }

    private void addCustomers() {
        customerRepository.save(createDataStartApp.getCustomerFirst());
        customerRepository.save(createDataStartApp.getCustomerSecond());
        customerRepository.save(createDataStartApp.getCustomerThird());
    }

    private void addProducts() {
        productRepository.save(createDataStartApp.getProductFirst());
        productRepository.save(createDataStartApp.getProductSecond());
        productRepository.save(createDataStartApp.getProductThird());
        productRepository.save(createDataStartApp.getProductFourth());
        productRepository.save(createDataStartApp.getProductFifth());
        productRepository.save(createDataStartApp.getProductSixth());
    }

    private void addOrders() {
        orderRepository.save(createDataStartApp.getOrderFirst());
        orderRepository.save(createDataStartApp.getOrderSecond());
    }

    private void addCustomerToOrders() {
        CustomerEntity customer = createDataStartApp.getCustomerFirst();

        OrderEntity orderFirst = orderRepository.getById(1L);
        orderFirst.setCustomer(customer);
        orderRepository.save(orderFirst);

        OrderEntity orderSecond = orderRepository.getById(2L);
        orderSecond.setCustomer(customer);
        orderRepository.save(orderSecond);
    }

    private void addOrderProductDetails() {
        orderProductRepository.save(createDataStartApp.getOrderProductFirst());
        orderProductRepository.save(createDataStartApp.getOrderProductSecond());
        orderProductRepository.save(createDataStartApp.getOrderProductThird());
    }

    private void mappingOrderWithOrderProduct() {
        OrderEntity orderFirst = orderRepository.getById(1L);
        orderFirst.setOrderProduct(createDataStartApp.getOrderProductEntities());
        orderRepository.save(orderFirst);

        OrderEntity orderSecond = orderRepository.getById(2L);
        orderSecond.setOrderProduct(createDataStartApp.getOrderProductEntities());
        orderRepository.save(orderSecond);
    }
}
