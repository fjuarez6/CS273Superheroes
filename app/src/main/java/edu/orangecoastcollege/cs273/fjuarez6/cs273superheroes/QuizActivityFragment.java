package edu.orangecoastcollege.cs273.fjuarez6.cs273superheroes;

import java.io.InputStream;
import java.util.ArrayList;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.media.CamcorderProfile.QUALITY_480P;
import static android.media.CamcorderProfile.get;
import static edu.orangecoastcollege.cs273.fjuarez6.cs273superheroes.QuizActivity.allSuperHeroes;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {

    private static final String TAG = "SuperHeroQuiz Activity";

    private static final int SUPERHEROES_IN_QUIZ = 10;

    private List<String> fileImageList;
    private List<String> quizImageList;
    private String choice;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private static final String SP = "Superpower";
    private static final String ONETHING = "OneThing";
    private SecureRandom random;
    private Handler handler;

    private TextView questionNumberTextView;
    private ImageView superHeroImageView;
    private TextView guessTextView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;

    public QuizActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_quiz, container, false);

        fileImageList = new ArrayList<>();
        quizImageList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();


        questionNumberTextView =
                (TextView) view.findViewById(R.id.questionNumberTextView);
        superHeroImageView = (ImageView) view.findViewById(R.id.superHeroImageView);
        guessTextView = (TextView) view.findViewById(R.id.guessTextView);
        guessLinearLayouts = new LinearLayout[2];
        guessLinearLayouts[0] =
                (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);


        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(
                getString(R.string.question, 1, SUPERHEROES_IN_QUIZ));
        return view;
    }

    public void updateQuizType(SharedPreferences sharedPreferences) {

        choice = sharedPreferences.getString(QuizActivity.CHOICES, null);

        if (choice.equals(SP))
        {
            guessTextView.setText(R.string.guess_superpower);
        }
        else if (choice.equals(ONETHING))
        {
            guessTextView.setText(R.string.guess_onething);
        }
        else
        {
            guessTextView.setText(R.string.guess_name);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resetQuiz() {

        correctAnswers = 0;
        totalGuesses = 0;
        quizImageList.clear();

        int heroCounter = 1;
        int numberOfHeroes = allSuperHeroes.size();

        while (heroCounter <= SUPERHEROES_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfHeroes);

            String fileName = allSuperHeroes.get(randomIndex).getImageName();

            if (!quizImageList.contains(fileName)) {
                quizImageList.add(fileName);
                ++heroCounter;
            }
        }

        loadNextHero();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadNextHero() {
        String nextImage = quizImageList.remove(0);
        correctAnswer = nextImage;
        answerTextView.setText("");
        guessRows = guessLinearLayouts.length;

        for (int i = 0; i < allSuperHeroes.size(); ++i)
        {
            fileImageList.add(allSuperHeroes.get(i).getImageName());
        }

        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), SUPERHEROES_IN_QUIZ));

       // String region = nextImage.substring(0, nextImage.indexOf('-'));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream =
                     assets.open(nextImage)) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            superHeroImageView.setImageDrawable(flag);

        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading" + nextImage, exception);
        }

        Collections.shuffle(fileImageList);

        int correct = fileImageList.indexOf(correctAnswer);
        fileImageList.add(fileImageList.remove(correct));

        for (int row = 0; row < guessRows; row++) {

            for (int column = 0;
                 column < guessLinearLayouts[row].getChildCount();
                 column++) {
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                String filename = fileImageList.get((row * 2) + column);
                newGuessButton.setText(getButtonText(filename));
            }
        }

        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        String buttonText = getButtonText(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(buttonText);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getButtonText(correctAnswer);
            ++totalGuesses;

            if (guess.equals(answer)) {
                ++correctAnswers;

                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                getContext().getTheme()));

                disableButtons();

                if (correctAnswers == SUPERHEROES_IN_QUIZ) {
                    DialogFragment quizResults =
                            new DialogFragment() {
                                @Override
                                public Dialog onCreateDialog(Bundle bundle) {
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(getActivity());
                                    builder.setMessage(
                                            getString(R.string.results,
                                                    totalGuesses,
                                                    (100 / (double) totalGuesses)));

                                    builder.setPositiveButton(R.string.reset_quiz,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    resetQuiz();

                                                }
                                            }
                                    );

                                    return builder.create();
                                }
                            };

                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else {
                    handler.postDelayed(
                            new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void run() {
                                    loadNextHero();
                                }
                            }, 2000);

                }
            }
            else {
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(
                        R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false);
            }

        }
    };

    private String getButtonText(String name) {
        int index = 0;
        for (int i = 0; i < allSuperHeroes.size(); ++i)
        {
            if (name == allSuperHeroes.get(i).getImageName())
            {
                index = i;
            }
        }

        String buttonText;
        if (choice.equals(SP))
        {
            buttonText = allSuperHeroes.get(index).getSuperpower();
        }
        else if (choice.equals(ONETHING))
        {
            buttonText = allSuperHeroes.get(index).getOneThing();
        }
        else
        {

            buttonText = allSuperHeroes.get(index).getName();
            return buttonText;
        }

        return buttonText;
    }

    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
        }
    }
}
