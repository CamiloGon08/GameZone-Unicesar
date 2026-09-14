package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;

public class Sale {

    private String id;
    private LocalDate date;
    private Seller seller;
    private Customer customer;
    private List<Product> products;
    private double total;

    public Sale(LocalDate date, String id, List<Product> products, Seller seller, Customer customer){
        this.date = date;
        this.id = id;
        this.products = products;
        this.seller = seller;
        this.customer = customer;
        this.total = calculateTotal();
    }

    private double calculateTotal(){
        double sum=0;
        for(Product product : products){
            sum += product.getPrice();
        }
        return sum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Customer getCustomer(){
        return customer;
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        this.total = calculateTotal();
    }

    public double getTotal() {
        return total;
    }


    @Override 
    public String toString(){
        return "Sale : "+ id + " Date : "+ date + 
        ",Customer : " + customer.getName()+ 
        ",Seller : "+ seller.getName()+
        ",Products : "+ products.size()+ 
        ", Total : $"+ String.format("%.2f",total);
    }

}
