package com.tp.oathapi.ocra;

public class OcraRequest {
    private String email;
    private String hash;
    private String question;

    public OcraRequest(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getEmail() {
        return this.email;
    }
}
