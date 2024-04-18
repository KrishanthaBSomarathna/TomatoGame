package com.example.tomatogame.Model;

import com.google.gson.annotations.SerializedName;
/**
 * Represents a response from the server containing a game question.
 * A question consists of an image URL and an integer solution.
 * This class is used to deserialize JSON responses from the server.
 */
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
