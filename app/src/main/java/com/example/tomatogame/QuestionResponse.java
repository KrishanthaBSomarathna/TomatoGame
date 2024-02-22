package com.example.tomatogame;

import com.google.gson.annotations.SerializedName;

public class QuestionResponse {
    @SerializedName("question")
    private String questionImageUrl;
    @SerializedName("solution")
    private int solution;

    public QuestionResponse() {
    }

    public String getQuestionImageUrl() {
        return questionImageUrl;
    }

    public int getSolution() {
        return solution;
    }
}
