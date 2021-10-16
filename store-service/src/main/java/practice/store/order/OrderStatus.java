package practice.store.order;

public enum OrderStatus {

    ORDER_AWAITING,

    // for delivered scenario
    ORDER_ACCEPTED,
    ORDER_PREPARATION,
    ORDER_SENT,
    ORDER_RECEIVED,

    // for returned scenario
    ORDER_RETURNED,

    // for canceled scenario
    ORDER_CANCELED;
}
