package cn.edu.bistu.se.cs.picturegame;


public interface ImageLayoutListener {
    void nextLevel();
    void stepChange(int currentStep);
    void timeChange(int currentTime);
    void gameOver();
}
