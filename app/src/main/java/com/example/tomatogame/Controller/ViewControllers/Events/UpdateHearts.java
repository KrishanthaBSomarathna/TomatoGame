package com.example.tomatogame.Controller.ViewControllers.Events;

import android.widget.TextView;

public class UpdateHearts {




    public void update(int remainingAttempts, TextView heartTextView) {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < remainingAttempts; i++) {
            hearts.append("❤️ ");
        }
        heartTextView.setText(hearts.toString());
    }
}
