package com.ecommerce.dto;
import java.math.BigDecimal;

public class PaymentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String method;       // UPI, CARD
    private String txnReference; // Mock Transaction ID

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getTxnReference() { return txnReference; }
    public void setTxnReference(String txnReference) { this.txnReference = txnReference; }
}