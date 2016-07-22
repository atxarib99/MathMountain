package com.example.arib.mathmountain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

//TODO: NEW DATABASE
public class CountdownActivity extends BasicActivity {

    CountDownTimer timer;
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
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        if(!GameSelectionActivity.MUTED)
            song.start();
        else
            song.stop();
        CountdownDatabase handler = new CountdownDatabase(this);
        times = (ArrayList) handler.getAllTeams();
        final TextView timerView = (TextView) findViewById(R.id.fullscreen_content);
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerView.setText("" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                endGame();
            }
        };
    }

    public void startGame(View view) {
        setClickableButtons(true);
        insertQuestion();
        insertQuestion();
        insertQuestion();
        Button bigButton = (Button) findViewById(R.id.bigStart);
        bigButton.setVisibility(View.GONE);
        Button button = (Button) findViewById(R.id.start);
        button.setText("Restart");
        level = 1;
        running = true;
        timer.start();
        if(!song.isPlaying() && !GameSelectionActivity.MUTED)
            song.start();
        progressThread.start();
    }

    protected void endGame() {
        CountdownDatabase handler = new CountdownDatabase(CountdownActivity.this);
        handler.addHighScore(level + "");
        setClickableButtons(false);
        times = (ArrayList) handler.getAllTeams();
        Button startButton = (Button) findViewById(R.id.start);
        startButton.setText("Restart");
        startButton.setVisibility(View.VISIBLE);
        running = false;
        startButton.setClickable(true);
        progressThread.interrupt();
    }

    public void viewScores(View view) {
        Intent intent = new Intent(this, HighScoresActivity.class);
        intent.putExtra("class", 2);
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
        } else {
            if(level > 1) {
                level--;
            }
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.RED);
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
        } else {
            if(level > 1) {
                level--;
            }
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.RED);
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
        } else {
            if(level > 1) {
                level--;
            }
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.RED);
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
            displayGood();
        } else {
            if(level > 1) {
                level--;
            }
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundColor(Color.RED);
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
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                }
            });
            flashThread.start();
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
