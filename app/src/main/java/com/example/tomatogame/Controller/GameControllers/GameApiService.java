package com.example.tomatogame.Controller.GameControllers;

import com.example.tomatogame.Model.QuestionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
/**
 * Game that interfaces to an external Server to retrieve a game.
 * A game consists of an image and an integer that denotes the solution of this game.
 *
 * @author Krishantha
 *
 */
public interface GameApiService {
    @GET("uob/tomato/api.php")
    Call<QuestionResponse> getQuestion();
}
