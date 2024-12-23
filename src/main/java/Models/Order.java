package Models;

import java.util.List;

public class Order {
    private String id;
    private String serialNumber;
    private String userId;
    private String consignee;
    private String consigneeAddress;
    private String phone;
    private double amount;
    private String state;
    private List<OrderDetail> orderDetails;

    // Getters and setters
    // ... similar to above
}