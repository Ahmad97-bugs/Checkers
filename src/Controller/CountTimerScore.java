package Controller;

import View.HamkaBoard;
import View.HamkaOptionPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CountTimerScore {

    private static final int ONE_SECOND = 1000;
    private final HamkaOptionPanel hamkaOptionPanel;
    private int count;
    private HamkaBoard h;
    private boolean isTimerActive = false;
    private final Timer tmr = new Timer(ONE_SECOND, this::actionPerformed);
    private final JLabel cntL;
    private final JLabel cnsL;
    private final int pTime;

    /**
     * Responsible for in game timers and score count also being used in Green and Orange coloring
     */

    public CountTimerScore(HamkaOptionPanel hamkaOptionPanel, JLabel tL, JLabel sL, int pT) {
        this.hamkaOptionPanel = hamkaOptionPanel;
        pTime = pT;
        cnsL = sL;
        cntL = tL;
        setTimerText(cntL, TimeFormat(count));
        if (pTime == 1 || pTime == 2) setTimerColor(cntL, Color.GREEN.darker());
    }

    // @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (pTime == 1 && hamkaOptionPanel.getWindow().getBoard().getGame().isP1Turn()) {
                if (isTimerActive) {
                    count++;
                    setTimerText(cntL, TimeFormat(count));
                    if (count > 60 && (pTime == 1 || pTime == 2)) setTimerColor(cntL, Color.RED.darker());
                    if (count > 30) {
                        hamkaOptionPanel.getWindow().getBoard().getGame().isGreen = true;
                        hamkaOptionPanel.getWindow().getBoard().update(false);
                    }
                    // Condition for 45s // zebra group
                    if(count >= 45 ){
                        hamkaOptionPanel.getWindow().getBoard().getGame().isPurple = true;
                        hamkaOptionPanel.getWindow().getBoard().update(false);
                    }
                    if (count > 90) {
                        //orange
                        hamkaOptionPanel.getWindow().getBoard().getGame().isGreen = false;
                        hamkaOptionPanel.getWindow().getBoard().getGame().isOrange = true;
                        hamkaOptionPanel.getWindow().getBoard().update(false);
                    }
                }
            }
            if (pTime == 1 && !hamkaOptionPanel.getWindow().getBoard().getGame().isP1Turn()) {
                if (count != 0)
                    hamkaOptionPanel.getWindow().getBoard().getGame().getBlack1Player().setpScore(hamkaOptionPanel.getWindow().getBoard().getGame().getBlack1Player().getpScore() + (60 - count));
                count = 0;

                cnsL.setText(String.valueOf(hamkaOptionPanel.getWindow().getBoard().getGame().getBlack1Player().getpScore()));
                setTimerText(cntL, TimeFormat(count));
                setTimerColor(cntL, Color.GREEN.darker());
            }
            if (pTime == 2 && !hamkaOptionPanel.getWindow().getBoard().getGame().isP1Turn()) {
                if (isTimerActive) {
                    count++;
                    setTimerText(cntL, TimeFormat(count));
                    if (count > 60 && (pTime == 1 || pTime == 2)) setTimerColor(cntL, Color.RED.darker());
                    if (count > 30) {
                        //green
                        hamkaOptionPanel.getWindow().getBoard().getGame().isGreen = true;
                        hamkaOptionPanel.getWindow().getBoard().update(false);
                    }
                    // Condition for 45s // zebra group
                    if(count >= 45 ){
                        hamkaOptionPanel.getWindow().getBoard().getGame().isPurple = true;
                        hamkaOptionPanel.getWindow().getBoard().update(false);
                    }
                    if (count > 90) {
                        //orange
                        hamkaOptionPanel.getWindow().getBoard().getGame().isGreen = false;
                        hamkaOptionPanel.getWindow().getBoard().getGame().isOrange = true;
                        hamkaOptionPanel.getWindow().getBoard().update(false);
                    }
                }
            }
            if (pTime == 2 && hamkaOptionPanel.getWindow().getBoard().getGame().isP1Turn()) {
                if (count != 0)
                    hamkaOptionPanel.getWindow().getBoard().getGame().getWhite2Player().setpScore(hamkaOptionPanel.getWindow().getBoard().getGame().getWhite2Player().getpScore() + (60 - count));
                count = 0;

                cnsL.setText(String.valueOf(hamkaOptionPanel.getWindow().getBoard().getGame().getWhite2Player().getpScore()));
                setTimerText(cntL, TimeFormat(count));
                setTimerColor(cntL, Color.GREEN.darker());
            }

            if (pTime == 3) {
                if (isTimerActive) {
                    count++;
                    setTimerText(cntL, TimeFormat(count));
                }
            }
        } catch (NullPointerException nEx) {

        }
    }

    public void start() {
        count = 0;
        isTimerActive = true;
        tmr.start();
    }

    public void resume() {
        isTimerActive = true;
        tmr.restart();
    }

    public void stop() {
        tmr.stop();
    }

    public void pause() {
        isTimerActive = false;
    }

    public void reset() {
        hamkaOptionPanel.getWindow().getBoard().getGame().getBlack1Player().setpScore(0);
        hamkaOptionPanel.getWindow().getBoard().getGame().getWhite2Player().setpScore(0);
        count = 0;
        isTimerActive = true;
        setTimerText(cntL, TimeFormat(count));
        if (pTime == 1 || pTime == 2) setTimerColor(cntL, Color.GREEN.darker());
        tmr.restart();

    }

    private String TimeFormat(int count) {

        int hours = count / 3600;
        int minutes = (count - hours * 3600) / 60;
        int seconds = count - minutes * 60;

        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    private void setTimerText(JLabel tL, String sTime) {
        tL.setText(sTime);
    }


    private void setTimerColor(JLabel tL, Color sColor) {
        tL.setForeground(sColor);
    }
}
