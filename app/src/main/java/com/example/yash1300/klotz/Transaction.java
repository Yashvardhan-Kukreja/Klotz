package com.example.yash1300.klotz;

/**
 * Created by Yash 1300 on 14-10-2017.
 */

public class Transaction {
    String transactionName, transactionDate, transactionAmount;
    int choice;

    public Transaction(String transactionName, String transactionDate, String transactionAmount, int choice) {
        this.transactionName = transactionName;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.choice = choice;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public int getChoice() {
        return choice;
    }
}
