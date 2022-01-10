package practice.storage.utils.initdata;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.storage.product.ProductRepository;
import practice.store.customer.CustomerEntity;
import practice.store.order.OrderEntity;

import java.text.ParseException;

@RequiredArgsConstructor
@Transactional
@Component
public class SaveDataToDatabase implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final CreateDataStartApp createDataStartApp;


    @Override
    public void run(ApplicationArguments args) throws ParseException {
        createDataStartApp.createCustomers();
        createDataStartApp.createProducts();
        createDataStartApp.createOrders();
        createDataStartApp.createOrderProductDetails();

        addProducts();
    }

    private void addProducts() {
        productRepository.save(createDataStartApp.getProductFirst());
        productRepository.save(createDataStartApp.getProductSecond());
        productRepository.save(createDataStartApp.getProductThird());
        productRepository.save(createDataStartApp.getProductFourth());
        productRepository.save(createDataStartApp.getProductFifth());
        productRepository.save(createDataStartApp.getProductSixth());
    }
}
