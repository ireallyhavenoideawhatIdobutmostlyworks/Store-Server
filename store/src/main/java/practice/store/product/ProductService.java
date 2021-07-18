package practice.store.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.exceptions.common.EntityNotFoundException;
import practice.store.utils.converter.EntitiesConverter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final EntitiesConverter entitiesConverter;


    public ProductPayload getById(long id) {
        return entitiesConverter.convertProduct(productRepository.getById(id));
    }

    public List<ProductPayload> getList() {
        return productRepository.
                findAll()
                .stream()
                .map(entitiesConverter::convertProduct)
                .collect(Collectors.toList());
    }
}
