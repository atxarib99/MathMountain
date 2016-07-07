package com.example.arib.mathmountain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

//TODO: MAKE LEVEL UP MORE EXTREME, CREATE STATIC VARIABLES FOR ALL DRAWABLE RUNTIME, COUNTDOWN, CONTINUOUS
public class MainActivity extends Activity {

    private int level;
    Chronometer chronometer;
    SeekBar seekBar;
    RelativeLayout layout;
    Drawable d;
    ImageTask imageTask;
    protected static ArrayList<String> times;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainrel);
        level = 1;
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(10);
        seekBar.setProgress(level - 1);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setClickableButtons(false);
        DatabaseHandler handler = new DatabaseHandler(this);
        times = (ArrayList) handler.getAllTeams();
        layout = (RelativeLayout) findViewById(R.id.relativeLayout);
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

    private void setClickableButtons(boolean b) {
        TextView questionBox = (TextView) findViewById(R.id.questionText);
        TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        Button first = (Button) findViewById(R.id.first_choice);
        Button second = (Button) findViewById(R.id.second_choice);
        Button third = (Button) findViewById(R.id.third_choice);
        Button fourth = (Button) findViewById(R.id.fourth_choice);
        if(b) {
            questionBox.setVisibility(View.VISIBLE);
            questionBox2.setVisibility(View.VISIBLE);
            questionBox3.setVisibility(View.VISIBLE);
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            third.setVisibility(View.VISIBLE);
            fourth.setVisibility(View.VISIBLE);
        } else {
            questionBox.setVisibility(View.GONE);
            questionBox2.setVisibility(View.GONE);
            questionBox3.setVisibility(View.GONE);
            first.setVisibility(View.GONE);
            second.setVisibility(View.GONE);
            third.setVisibility(View.GONE);
            fourth.setVisibility(View.GONE);
        }
        first.setClickable(b);
        second.setClickable(b);
        third.setClickable(b);
        fourth.setClickable(b);
    }

    private String generateQuestion(char c) {
        String question;
        switch (c) {
            case 'a' : question = generateAddQuestion(); break;
            case 's' : question = generateSubQuestion(); break;
            case 'm' : question = generateMulQuestion(); break;
            case 'd' : question = generateDivQuestion(); break;
            default : question = "YOU BROKE THE GAME"; break;
        }
        return question;
    }

    private String generateAddQuestion() {
        String question;
        Random generator = new Random();
        int num1 = generator.nextInt(9 + level) + 1;
        int num2 = generator.nextInt(9 + level) + 1;
        question = num1 + " + " + num2;
        return question;
    }

    private String generateSubQuestion() {
        String question;
        Random generator = new Random();
        int num1 = generator.nextInt(9 + level) + 1;
        int num2 = generator.nextInt(9 + level) + 1;
        while(num1 == num2) {
            num1 = generator.nextInt(9 + level) + 1;
        }
        if(num1 > num2) {
            question = num1 + " - " + num2;
        } else {
            question = num2 + " - " + num1;
        }
        return question;
    }

    private String generateMulQuestion() {
        String question;
        Random generator = new Random();
        int num1 = generator.nextInt(6 + (int)Math.floor(level/2)) + 1;
        int num2 = generator.nextInt(6 + (int)Math.floor(level/2)) + 1;
        question = num1 + " * " + num2;
        return question;
    }

    private String generateDivQuestion() {
        String question;
        Random generator = new Random();
        int num1 = generator.nextInt(6 + (int)Math.floor(level/2)) + 1;
        int num2 = generator.nextInt(6 + (int)Math.floor(level/2)) + 1;
        while(num1 % num2 != 0) {
            num1 = generator.nextInt(6 + (int)Math.floor(level/2)) + 1;
            num2 = generator.nextInt(6 + (int)Math.floor(level/2)) + 1;
        }
        question = num1 + " / " + num2;
        return question;
    }

    //This method calls other methods to generate answers based on the numbers and operation
    private int[] generateAnswers(char c, int num1, int num2) {
        int[] answers;
        switch(c) {
            case 'a' : answers = generateAddAnswers(num1, num2); break;
            case 's' : answers = generateSubAnswers(num1, num2); break;
            case 'm' : answers = generateMulAnswers(num1, num2); break;
            case 'd' : answers = generateDivAnswers(num1, num2); break;
            default : answers = new int[]{0, 0, 0, 0}; break;
        }
        return answers;
    }
    //calculates the right answer plus 3 other slightly incorrect answers for addition
    private int[] generateAddAnswers(int num1, int num2) {
        int rightAnswer = num1 + num2;                              //the right answer
        int trick1 = num1 + num2 + 1;                               //slightly incorrect 1
        int trick2 = num1 + num2 - 1;                               //slightly incorrect 2
        int otherAnswer;
        if(num1 != num2) {
            otherAnswer = num1 + num1;                              //different operation
        } else {
            otherAnswer = Math.abs(num1 - num2);
        }
        int[] answers = new int[]{trick1, trick2, otherAnswer};     //puts the incorrect to an array
        Random generator = new Random();                            //creates random generator
        int place = generator.nextInt(4);                           //generates where the right answer will be
        int[] returning = new int[4];                               //creates the list of answers
        returning[place] = rightAnswer;                             //places the right answer in
        int count = 0;                                              //variable for looping through incorrect answers
        for(int i =0; i < 4; i++) {                                 //for loop for answers
            if(returning[i] == 0) {                                 //if the spot is empty
                returning[i] = answers[count];                      //insert an answer
                count++;                                            //move further in the incorrect answers
            }
        }
        return returning;                                           //return the list of answers
    }

    //calculates the right answer plus 3 other slightly incorrect answers for multiplication
    private int[] generateMulAnswers(int num1, int num2) {  //same algorithm see addition
        int rightAnswer = num1 * num2;
        int trick1 = (num1 + 1) * num2;
        int trick2 = (num1 - 1) * num2;
        int otherAnswers = num1 + num2;
        int[] answers = new int[]{trick1, trick2, otherAnswers};
        Random generator = new Random();
        int place = generator.nextInt(4);
        int[] returning = new int[4];
        returning[place] = rightAnswer;
        int count = 0;
        for(int i =0; i < 4; i++) {
            if(returning[i] == 0) {
                returning[i] = answers[count];
                count++;
            }
        }
        return returning;
    }

    //calculates the right answer plus 3 other slightly incorrect answers for subtraction
    private int[] generateSubAnswers(int num1, int num2) {  //same algorithm see addition
        int rightAnswer = num1 - num2;
        int trick1 = Math.abs(num1 - num2 + 1);
        int trick2 = (int) Math.floor(num1 / num2);
        int otherAnswers = num1 + num2;
        int[] answers = new int[]{trick1, trick2, otherAnswers};
        Random generator = new Random();
        int place = generator.nextInt(4);
        int[] returning = new int[4];
        returning[place] = rightAnswer;
        int count = 0;
        for(int i =0; i < 4; i++) {
            if(returning[i] == 0) {
                returning[i] = answers[count];
                count++;
            }
        }
        return returning;
    }

    //calculates the right answer plus 3 other slightly incorrect answers for division
    private int[] generateDivAnswers(int num1, int num2) {  //same algorithm see addition
        int rightAnswer = num1 / num2;
        int trick1 = (int)Math.floor(num2 / num1);
        int trick2 = (int)Math.floor((num1 + num2) / num2);
        int otherAnswers = num1 * num2;
        int[] answers = new int[]{trick1, trick2, otherAnswers};
        Random generator = new Random();
        int place = generator.nextInt(4);
        int[] returning = new int[4];
        returning[place] = rightAnswer;
        int count = 0;
        for(int i =0; i < 4; i++) {
            if(returning[i] == 0) {
                returning[i] = answers[count];
                count++;
            }
        }
        return returning;
    }

    private void insertQuestion() {
        Random generator = new Random();
        int type = generator.nextInt(5) + 1;
        char c;
        switch (type) {
            case 1 : c = 'a'; break;
            case 2 : c = 's'; break;
            case 3 : c = 'm'; break;
            case 4 : c = 'd'; break;
            default : c = 'a'; break;
        }
        String question = generateQuestion(c);
        TextView questionBox2 = (TextView) findViewById(R.id.questionText2);
        TextView questionBox3 = (TextView) findViewById(R.id.questionText3);
        TextView questionBox = (TextView) findViewById(R.id.questionText);
        if(questionBox3.getText().equals("")) {
            questionBox3.setText(question);
        } else if(questionBox2.getText().equals("")) {
            questionBox2.setText(question);

        } else {
            questionBox.setText(question);
        }
        if(!questionBox.getText().equals("")) {
            question = questionBox.getText() + "";
            String[] questionSplit = question.split(" ");
            int num1 = Integer.parseInt(questionSplit[0]);
            int num2 = Integer.parseInt(questionSplit[2]);String func = questionSplit[1];
            switch (func) {
                case "+" : c = 'a'; break;
                case "-" : c = 's'; break;
                case "*" : c = 'm'; break;
                case "/" : c = 'd'; break;
                default : c = 'a'; break;
            }
            int[] answers = generateAnswers(c, num1, num2);
            Button answer1 = (Button) findViewById(R.id.first_choice);
            Button answer2 = (Button) findViewById(R.id.second_choice);
            Button answer3 = (Button) findViewById(R.id.third_choice);
            Button answer4 = (Button) findViewById(R.id.fourth_choice);
            answer1.setText("" + answers[0]);
            answer2.setText("" + answers[1]);
            answer3.setText("" + answers[2]);
            answer4.setText("" + answers[3]);
        }
    }

    public boolean parseQuestion(int answer) {
        String question = "" + ((TextView) findViewById(R.id.questionText)).getText();
        String[] questionSplit = question.split(" ");
        int correctAnswer;
        int num1 = Integer.parseInt(questionSplit[0]);
        int num2 = Integer.parseInt(questionSplit[2]);
        String func = questionSplit[1];
        switch (func) {
            case "+" : correctAnswer = num1 + num2; break;
            case "-" : correctAnswer = num1 - num2; break;
            case "*" : correctAnswer = num1 * num2; break;
            case "/" : correctAnswer = num1 / num2; break;
            default : correctAnswer = num1 + num2; break;
        }
        return answer == correctAnswer;
    }

    public void startGame(View view) {
        setClickableButtons(true);
        layout.setBackground(getDrawable(R.drawable.mountainone));
        insertQuestion();
        insertQuestion();
        insertQuestion();
        Button button = (Button) findViewById(R.id.start);
        button.setText("Restart");
        level = 1;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    public void viewScores(View view) {
        Intent intent = new Intent(this, HighScoresActivity.class);
        intent.putExtra("class", 1);
        startActivity(intent);
    }

    public void firstSelected(View view) {
        Button button = (Button) findViewById(R.id.first_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            seekBar.setProgress(level - 1);
        } else {
            if(level > 1) {
                level--;
                seekBar.setProgress(level - 1);
            }
        }
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
            DatabaseHandler handler = new DatabaseHandler(this);
            handler.addHighScore(chronometer.getText() + "");
            Button startButton = (Button) findViewById(R.id.start);
            startButton.setText("Restart");
            startButton.setVisibility(View.VISIBLE);
            setClickableButtons(false);
            seekBar.setProgress(level);
            startButton.setClickable(true);
            chronometer.stop();

        }
    }

    public void secondSelected(View view) {
        Button button = (Button) findViewById(R.id.second_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            seekBar.setProgress(level - 1);
        } else {
            if(level > 1) {
                level--;
                seekBar.setProgress(level - 1);
            }
        }
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
            DatabaseHandler handler = new DatabaseHandler(this);
            setClickableButtons(false);
            handler.addHighScore(chronometer.getText() + "");
            times = (ArrayList) handler.getAllTeams();
            Button startButton = (Button) findViewById(R.id.start);
            startButton.setText("Restart");
            startButton.setVisibility(View.VISIBLE);
            startButton.setClickable(true);
            seekBar.setProgress(level);
            chronometer.stop();
            Log.d("getBase", chronometer.getBase() + "");

        }
    }

    public void thirdSelected(View view) {
        Button button = (Button) findViewById(R.id.third_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            seekBar.setProgress(level - 1);
        } else {
            if(level > 1) {
                level--;
                seekBar.setProgress(level - 1);
            }
        }
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
            DatabaseHandler handler = new DatabaseHandler(this);
            handler.addHighScore(chronometer.getText() + "");
            times = (ArrayList) handler.getAllTeams();
            Button startButton = (Button) findViewById(R.id.start);
            startButton.setText("Restart");
            startButton.setVisibility(View.VISIBLE);
            setClickableButtons(false);
            seekBar.setProgress(level);
            startButton.setClickable(true);
            chronometer.stop();
            Log.d("getBase", chronometer.getBase() + "");

        }
    }

    public void fourthSelected(View view) {
        Button button = (Button) findViewById(R.id.fourth_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
            seekBar.setProgress(level - 1);
        } else {
            if(level > 1) {
                level--;
                seekBar.setProgress(level - 1);
            }
        }
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
            DatabaseHandler handler = new DatabaseHandler(this);
            handler.addHighScore(chronometer.getText() + "");
            setClickableButtons(false);
            times = (ArrayList) handler.getAllTeams();
            Button startButton = (Button) findViewById(R.id.start);
            startButton.setText("Restart");
            startButton.setVisibility(View.VISIBLE);
            seekBar.setProgress(level);
            startButton.setClickable(true);
            chronometer.stop();
            Log.d("getBase", chronometer.getBase() + "");

        }
    }

    private class ImageTask extends AsyncTask<Integer, Void, Drawable> {
        private int level;
        public ImageTask() {

        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            level = params[0];
            Drawable drawable = getBackgroundImage(level);
            return drawable;
        }

        private Drawable getBackgroundImage(int level) {
            switch (level) {
                case 1 : return getDrawable(R.drawable.mountainone);
                case 2 : return getDrawable(R.drawable.mountaintwo);
                case 3 : return getDrawable(R.drawable.mountainthree);
                case 4 : return getDrawable(R.drawable.mountainfour);
                case 5 : return getDrawable(R.drawable.mountainfive);
                case 6 : return getDrawable(R.drawable.mountainsix);
                case 7 : return getDrawable(R.drawable.mountainseven);
                case 8 : return getDrawable(R.drawable.mountaineight);
                case 9 : return getDrawable(R.drawable.mountainnine);
                case 10: return getDrawable(R.drawable.mountainten);
                default : return getDrawable(R.drawable.mountainten);
            }

        }

    }
}

