package com.tp.oathapi.ocra;

public class OcraRequest {
    private Long userId;
    private String otp;
    private String transactionData;
    private String hash;
    private String question;
    private String key;

    public OcraRequest(Long userId, String otp, String transactionData, String hash) {
        this.userId = userId;
        this.otp = otp;
        this.transactionData = transactionData;
        this.hash = hash;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTransactionData() {
        return transactionData;
    }

    public String getHash() {
        return hash;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTransactionData(String transactionData) {
        this.transactionData = transactionData;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
