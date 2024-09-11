package com.comp90018.comp90018.model;

import java.util.List;

public class Bill {
    private BillType type;
    private String notes;
    private double amount;
    private User payer;
    private List<User> participants;
    private double shareAmount;

    public Bill(BillType type, String notes, double amount, User payer, List<User> participants, double shareAmount) {
        this.type = type;
        this.notes = notes;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.shareAmount = shareAmount;
    }

    public Bill() {
    }

    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(double shareAmount) {
        this.shareAmount = shareAmount;
    }
}
