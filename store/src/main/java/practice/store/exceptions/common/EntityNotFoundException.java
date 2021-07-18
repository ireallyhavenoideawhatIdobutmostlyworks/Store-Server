package practice.store.exceptions.common;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s not exist with ID:%d.", entityName.replace("Service", ""), id));
    }
}
