package com.example.arib.mathmountain;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

//TODO: MAKE LEVEL UP MORE EXTREME, CREATE STATIC VARIABLES FOR ALL DRAWABLE RUNTIME, COUNTDOWN, CONTINUOUS
public class MainActivity extends BasicActivity {

    Chronometer chronometer;
    protected static ArrayList<String> times;

    protected void postCreate() {
        progressUpdate = new Runnable() {
            @Override
            public void run() {
                progressTask = new ProgressTask();
                try {
                    barImage = progressTask.execute(level).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                progressBar.setImageDrawable(barImage);
            }
        };
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        if(!GameSelectionActivity.MUTED)
            song.start();
        else
            song.stop();
        DatabaseHandler handler = new DatabaseHandler(this);
        times = (ArrayList) handler.getAllTeams();
        imageTask = new ImageTask();
        try {
            d = imageTask.execute(level).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.setBackground(d);
        }
        chronometer = (Chronometer) findViewById(R.id.fullscreen_content);
    }

    @Deprecated
    private void playAnimation() {
        final TextView questionBox = (TextView) findViewById(R.id.questionText);
        final TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        final TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        float box2pos = questionBox2.getY();
        float box2size = questionBox2.getTextSize();
        int refAlpha = Color.alpha(questionBox.getCurrentTextColor());
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        final ViewGroup.LayoutParams layoutParams = questionBox2.getLayoutParams();
        th = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while(questionBox2.getGravity() != Gravity.CENTER && questionBox2.getCurrentTextColor() != questionBox.getCurrentTextColor()) {
                    int prevColor = questionBox2.getCurrentTextColor();
                    int prevAlpha = Color.alpha(prevColor);
                    count++;
                    if(count > 50){
                        count = 50;
                    }
                    params.setMargins(width/2, count + height, 0, 0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            questionBox.setLayoutParams(params);
                        }
                    });
                    int newAlpha = prevAlpha + 35;
                    if(newAlpha > 255)
                        newAlpha = 255;
                    final int newColor = Color.argb(newAlpha, 255, 255, 255);
                    Log.d("test", ""+prevAlpha);
                    if(prevAlpha < 255) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                questionBox2.setTextColor(newColor);
                            }
                        });
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();

    }


    public void startGame(View view) {
        setClickableButtons(true);
        layout.setBackground(getDrawable(R.drawable.mountainone));
        progressBar.setImageDrawable(getDrawable(R.drawable.progressone));
        goodImage.setVisibility(View.VISIBLE);
        insertQuestion();
        insertQuestion();
        insertQuestion();
        ImageView endImage = (ImageView) findViewById(R.id.endImage);
        endImage.setVisibility(View.GONE);
        int color = ((TextView) findViewById(R.id.questionText)).getCurrentTextColor();
        color = Color.alpha(color);
        Log.d("color", "" + color);
        color = ((TextView) findViewById(R.id.questionText2)).getCurrentTextColor();
        color = Color.alpha(color);
        Log.d("color", ""+ color);
        Button bigButton = (Button) findViewById(R.id.bigStart);
        bigButton.setVisibility(View.GONE);
        Button button = (Button) findViewById(R.id.start);
        button.setText("Restart");
        if(!song.isPlaying() && !GameSelectionActivity.MUTED)
            song.start();
        level = 1;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    public void viewScores(View view) {
        Intent intent = new Intent(this, HighScoresActivity.class);
        intent.putExtra("class", 1);
        startActivity(intent);
    }

    protected void endGame() {
        DatabaseHandler handler = new DatabaseHandler(this);
        handler.addHighScore(chronometer.getText() + "");
        times = (ArrayList) handler.getAllTeams();
        Button startButton = (Button) findViewById(R.id.start);
        startButton.setText("Restart");
        startButton.setVisibility(View.VISIBLE);
        setClickableButtons(false);
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(progressUpdate);
            }
        });
        startButton.setClickable(true);
        chronometer.stop();
        song.stop();
        ImageView endImage = (ImageView) findViewById(R.id.endImage);
        endImage.setVisibility(View.VISIBLE);
        goodImage.setVisibility(View.GONE);
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
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(progressUpdate);
            }
        });
        progressThread.start();
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        imageTask = new ImageTask();
        try {
            d = imageTask.execute(level).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setBackground(d);
                button.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        if(level < 10) {
            TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
            TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
            TextView questionBox = (TextView) findViewById(R.id.questionText);
            questionBox.setText(questionBox2.getText());
            questionBox2.setText(questionBox3.getText());
            questionBox3.setText("");
            insertQuestion();
        }
        else {
            endGame();
        }
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
            goodThread = new Thread(imageUpdate);
            goodThread.start();
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
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(progressUpdate);
            }
        });
        progressThread.start();
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        imageTask = new ImageTask();
        try {
            d = imageTask.execute(level).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setBackground(d);
            }
        });

        if(level < 10) {
            TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
            TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
            TextView questionBox = (TextView) findViewById(R.id.questionText);
            questionBox.setText(questionBox2.getText());
            questionBox2.setText(questionBox3.getText());
            questionBox3.setText("");
            insertQuestion();
        }
        else {
            endGame();

        }
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
            goodThread = new Thread(imageUpdate);
            goodThread.start();
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
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(progressUpdate);
            }
        });
        progressThread.start();
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        imageTask = new ImageTask();
        try {
            d = imageTask.execute(level).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setBackground(d);
            }
        });
        if(level < 10) {
            TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
            TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
            TextView questionBox = (TextView) findViewById(R.id.questionText);
            questionBox.setText(questionBox2.getText());
            questionBox2.setText(questionBox3.getText());
            questionBox3.setText("");
            insertQuestion();
        }
        else {
            endGame();

        }
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
            goodThread = new Thread(imageUpdate);
            goodThread.start();

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
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(progressUpdate);
            }
        });
        progressThread.start();
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        imageTask = new ImageTask();
        try {
            d = imageTask.execute(level).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setBackground(d);
            }
        });
        if(level < 10) {
            TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
            TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
            TextView questionBox = (TextView) findViewById(R.id.questionText);
            questionBox.setText(questionBox2.getText());
            questionBox2.setText(questionBox3.getText());
            questionBox3.setText("");
            insertQuestion();
        }
        else {
            endGame();

        }
    }

}

