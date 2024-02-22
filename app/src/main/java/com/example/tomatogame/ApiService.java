package com.example.tomatogame;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("uob/tomato/api.php")
    Call<QuestionResponse> getQuestion();
}
