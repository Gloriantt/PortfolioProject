package com.example.PortfolioProject.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String customerEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid phone number")
    private String customerPhone;

    @NotBlank(message = "Shipping address is required")
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    private String shippingAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{5,10}$", message = "Please provide a valid postal code")
    private String postalCode;

    private String country = "USA";

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    // Дополнительные поля для платежной информации
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    // Поля для дополнительной информации
    private String orderNotes;
    private boolean saveInfo;
    private boolean agreeToTerms;

    // Конструкторы
    public CheckoutDto() {}

    // Геттеры и сеттеры
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public boolean isSaveInfo() {
        return saveInfo;
    }

    public void setSaveInfo(boolean saveInfo) {
        this.saveInfo = saveInfo;
    }

    public boolean isAgreeToTerms() {
        return agreeToTerms;
    }

    public void setAgreeToTerms(boolean agreeToTerms) {
        this.agreeToTerms = agreeToTerms;
    }

    // Метод для получения полного адреса
    public String getFullAddress() {
        return shippingAddress + ", " + city + ", " + postalCode + ", " + country;
    }
}