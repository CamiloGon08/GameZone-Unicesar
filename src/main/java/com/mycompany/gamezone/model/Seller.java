package com.mycompany.gamezone.model;

public class Seller extends Person{
    private long employeeCode;
    private String shift;

    public Seller() {
    }

    public Seller(long employeeCode, String shift, String name, long iD, long contactNumber) {
        super(name, iD, contactNumber);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    public long getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(long employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
    
    @Override
    public String textFormat(){
        return getiD() + "|" + getName() + "|" + getContactNumber() + "|" + employeeCode + "|" + shift;
    }
}
