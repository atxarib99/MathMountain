package com.example.arib.mathmountain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ContinuousActivity extends BasicActivity {

    boolean running;
    protected static ArrayList<String> times;
    protected void postCreate() {
        layout.setBackground(getDrawable(R.drawable.mountainten));
        progressUpdate = new Runnable() {
            @Override
            public void run() {
                progressTask = new ProgressTask();
                Random random = new Random();
                int num = random.nextInt(11);
                try {
                    barImage = progressTask.execute(num).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                progressBar.setImageDrawable(barImage);
            }
        };
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {
                    runOnUiThread(progressUpdate);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        right = MediaPlayer.create(this, R.raw.right);
        right.setLooping(false);
        wrong = MediaPlayer.create(this, R.raw.wrong);
        wrong.setLooping(false);
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        if(!GameSelectionActivity.MUTED)
            song.start();
        else {
            song.stop();
        }
        CountdownDatabase handler = new CountdownDatabase(this);
        times = (ArrayList) handler.getAllTeams();
        TextView timerView = (TextView) findViewById(R.id.fullscreen_content);
        timerView.setVisibility(View.GONE);
    }

    public void startGame(View view) {
        setClickableButtons(true);
        insertQuestion();
        insertQuestion();
        insertQuestion();
        running = true;
        Button bigButton = (Button) findViewById(R.id.bigStart);
        bigButton.setVisibility(View.GONE);
        Button button = (Button) findViewById(R.id.start);
        button.setText("Restart");
        level = 1;
        if(!song.isPlaying() && !GameSelectionActivity.MUTED)
            song.start();
        progressThread.start();
    }

    public void endGame() {
        ContinuousDatabase handler = new ContinuousDatabase(this);
        handler.addHighScore(level + "");
        setClickableButtons(false);
        times = (ArrayList) handler.getAllTeams();
        Button bigButton = (Button) findViewById(R.id.bigStart);
        bigButton.setText("RESTART");
        bigButton.setVisibility(View.VISIBLE);
        Button startButton = (Button) findViewById(R.id.start);
        startButton.setText("Restart");
        startButton.setVisibility(View.VISIBLE);
        running = false;
        startButton.setClickable(true);
        progressThread.interrupt();
    }

    public void viewScores(View view) {
        Intent intent = new Intent(this, HighScoresActivity.class);
        intent.putExtra("class", 3);
        startActivity(intent);
    }

    public void firstSelected(View view) {
        final Button button = (Button) findViewById(R.id.first_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.GREEN);
                        }
                    });
                    try {
                        Thread.sleep(650);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             button.setBackgroundColor(Color.parseColor("#b5535cca"));
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
            right.start();
        } else {
            endGame();
        }
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        TextView questionBox = (TextView) findViewById(R.id.questionText);
        questionBox.setText(questionBox2.getText());
        questionBox2.setText(questionBox3.getText());
        questionBox3.setText("");
        insertQuestion();
    }

    public void secondSelected(View view) {
        final Button button = (Button) findViewById(R.id.second_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.GREEN);
                        }
                    });
                    try {
                        Thread.sleep(650);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             button.setBackgroundColor(Color.parseColor("#b5535cca"));
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
            right.start();
        } else {
            endGame();
        }
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        TextView questionBox = (TextView) findViewById(R.id.questionText);
        questionBox.setText(questionBox2.getText());
        questionBox2.setText(questionBox3.getText());
        questionBox3.setText("");
        insertQuestion();
    }

    public void thirdSelected(View view) {
        final Button button = (Button) findViewById(R.id.third_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.GREEN);
                        }
                    });
                    try {
                        Thread.sleep(650);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             button.setBackgroundColor(Color.parseColor("#b5535cca"));
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
            right.start();
        } else {
            endGame();
        }
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        TextView questionBox = (TextView) findViewById(R.id.questionText);
        questionBox.setText(questionBox2.getText());
        questionBox2.setText(questionBox3.getText());
        questionBox3.setText("");
        insertQuestion();
    }


    public void fourthSelected(View view) {
        final Button button = (Button) findViewById(R.id.fourth_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.GREEN);
                        }
                    });
                    try {
                        Thread.sleep(650);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             button.setBackgroundColor(Color.parseColor("#b5535cca"));
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
            right.start();
        } else {
            endGame();
        }
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        TextView questionBox = (TextView) findViewById(R.id.questionText);
        questionBox.setText(questionBox2.getText());
        questionBox2.setText(questionBox3.getText());
        questionBox3.setText("");
        insertQuestion();
    }

}
