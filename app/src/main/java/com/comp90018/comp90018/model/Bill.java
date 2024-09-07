package com.comp90018.comp90018.model;

import java.util.List;

public class Bill {
    private BillType type;
    private String notes;
    private double amount;
    private User payer;
    private List<User> participants;
    private double shareAmount;
}
