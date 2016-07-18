package com.example.arib.mathmountain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ContinuousActivity extends Activity {

    private int level;
    ImageView progressBar;
    RelativeLayout layout;
    MediaPlayer song;
    Drawable barImage;
    Thread progressThread;
    boolean running;
    protected static ArrayList<String> times;
    ProgressTask progressTask;
    Runnable progressUpdate = new Runnable() {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = false;
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        if(!GameSelectionActivity.MUTED)
            song.start();
        else {
            song.stop();
        }
        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {
                    runOnUiThread(progressUpdate);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        setContentView(R.layout.activity_mainrel);
        level = 1;
        TextView levelView = (TextView) findViewById(R.id.levelView);
        levelView.setText("" + level);
        progressBar = (ImageView) findViewById(R.id.progressBar);
        progressBar.setImageDrawable(getDrawable(R.drawable.progresszero));
        setClickableButtons(false);
        CountdownDatabase handler = new CountdownDatabase(this);
        times = (ArrayList) handler.getAllTeams();
        layout = (RelativeLayout) findViewById(R.id.relativeLayout);
        layout.setBackground(getDrawable(R.drawable.mountainten));
        TextView timerView = (TextView) findViewById(R.id.fullscreen_content);
        timerView.setVisibility(View.GONE);

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
    }

    public void endGame() {
        ContinuousDatabase handler = new ContinuousDatabase(this);
        handler.addHighScore(level + "");
        setClickableButtons(false);
        times = (ArrayList) handler.getAllTeams();
        Button startButton = (Button) findViewById(R.id.start);
        startButton.setText("Restart");
        startButton.setVisibility(View.VISIBLE);
        running = false;
        startButton.setClickable(true);
    }

    public void viewScores(View view) {
        Intent intent = new Intent(this, HighScoresActivity.class);
        intent.putExtra("class", 3);
        startActivity(intent);
    }

    public void firstSelected(View view) {
        Button button = (Button) findViewById(R.id.first_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
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
        Button button = (Button) findViewById(R.id.second_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
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
        Button button = (Button) findViewById(R.id.third_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
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
        Button button = (Button) findViewById(R.id.fourth_choice);
        int answer = Integer.parseInt("" + button.getText());
        boolean correct = parseQuestion(answer);
        if(correct) {
            level++;
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

    private class ProgressTask extends AsyncTask<Integer, Void, Drawable> {
        private int level;
        public ProgressTask() {

        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            level = params[0];
            Drawable drawable = getProgressImage(level);
            return drawable;
        }

        private Drawable getProgressImage(int level) {
            switch (level) {
                case 1 : return getDrawable(R.drawable.progressone);
                case 2 : return getDrawable(R.drawable.progresstwo);
                case 3 : return getDrawable(R.drawable.progressthree);
                case 4 : return getDrawable(R.drawable.progressfour);
                case 5 : return getDrawable(R.drawable.progressfive);
                case 6 : return getDrawable(R.drawable.progresssix);
                case 7 : return getDrawable(R.drawable.progressseven);
                case 8 : return getDrawable(R.drawable.progresseight);
                case 9 : return getDrawable(R.drawable.progressnine);
                case 10: return getDrawable(R.drawable.progressten);
                default : return getDrawable(R.drawable.progresszero);
            }

        }

    }
}
