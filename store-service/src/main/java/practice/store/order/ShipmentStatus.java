package practice.store.order;

public enum ShipmentStatus {

    // for delivered scenario
    SHIPMENT_AWAITING_FOR_ACCEPT,
    SHIPMENT_ACCEPTED,
    SHIPMENT_PREPARATION,
    SHIPMENT_AWAITING_FOR_SENDING,
    SHIPMENT_DELIVERY_IN_PROGRESS,
    SHIPMENT_RECEIVED,

    // for awaiting scenario
    SHIPMENT_NOT_RECEIVED,
    SHIPMENT_AWAITING_IN_PARCEL,

    // for not delivered scenario
    SHIPMENT_RETURNED_TO_SENDER,
    SHIPMENT_IN_STORAGE,

    // for cancelled scenario
    SHIPMENT_CANCELLED;
}
