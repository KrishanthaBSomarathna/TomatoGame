package com.example.tomatogame.Controller.ViewControllers.Events;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

public class ButtonAnimation {
    public static ObjectAnimator zoomInPlayX;
    public static ObjectAnimator zoomOutPlayX;
    public static ObjectAnimator zoomInPlayY;
    public static ObjectAnimator zoomOutPlayY;

    public static void animateButton(View view) {
        Animation animation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        view.startAnimation(animation);
    }
    public static void startLoopAnimations(ImageButton button) {
        // Create zoom in and zoom out animators for the restartButton scaleX
        zoomInPlayX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 1.2f);
        zoomInPlayX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInPlayX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInPlayX.setDuration(1000);

        zoomOutPlayX = ObjectAnimator.ofFloat(button, "scaleX", 1.2f, 1.0f);
        zoomOutPlayX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutPlayX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutPlayX.setDuration(1000);

        // Create zoom in and zoom out animators for the restartButton scaleY
        zoomInPlayY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 1.2f);
        zoomInPlayY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInPlayY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInPlayY.setDuration(1500);

        zoomOutPlayY = ObjectAnimator.ofFloat(button, "scaleY", 1.2f, 1.0f);
        zoomOutPlayY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutPlayY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutPlayY.setDuration(1500);

        // Start the animations for restartButton
        zoomInPlayX.start();
        zoomOutPlayX.start();
        zoomInPlayY.start();
        zoomOutPlayY.start();
    }

}
