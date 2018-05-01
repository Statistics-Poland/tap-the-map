package com.kantoniak.discrete_fox.game_mechanics;

import android.content.Context;

import com.kantoniak.discrete_fox.country.Country;
import com.kantoniak.discrete_fox.country.CountryUtil;
import com.kantoniak.discrete_fox.ask.Question;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Gameplay {

    public static class Settings {
        public static final int COUNTRIES_PER_QUESTION = 5;
        public static final int QUESTIONS_PER_SERIES = 5;
    }

    public final int NUMBEROFQUESTIONS;
    int step;
    int currentQuestion;
    int mnumberOfCountries;
    int scoreTotal;
    int score;
    int maxScore;
    Integer[] decisions;
    ArrayList<Question> mquestions;

    public Gameplay(ArrayList<Question> questions, int numberOfCountries) {
        NUMBEROFQUESTIONS = questions.size();
        step = 0;
        score = 0;
        mnumberOfCountries = numberOfCountries;
        maxScore = mnumberOfCountries * NUMBEROFQUESTIONS;
        currentQuestion = 0;
        decisions = new Integer[mnumberOfCountries];
        for (int i = 0; i < mnumberOfCountries; i++) {
            decisions[i] = 0;
        }
        mquestions = questions;
    }

    public Question finishQuestion(Context context) {
        return nextQuestion(context);
    }

    public void updateScore(Map map) {
        // Gather decisions
        HashMap<Country, CountryInstance> mapCountries = map.getCountries();
        List<Country> countries = mquestions.get(currentQuestion).getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            Country country = countries.get(i);
            decisions[i] = mapCountries.get(country).getHeight();
        }
        // Calculate score
        score = calculateScore(map);
        scoreTotal += score;
    }

    private Question nextQuestion(Context context) {
        currentQuestion++;
        if (mquestions.size() == currentQuestion) {
            // TODO Present superb view of the final result!
            // TODO: We probably should delete this gameplay, and create a new one, otherwise, we should clean this one.
            currentQuestion--;
            return null;
        }
        score = 0;
        return mquestions.get(currentQuestion);
    }

    public int getResult() {
        return scoreTotal;
    }

    private Integer calculateScore(Map map) {
        int score = 0;
        HashMap<Country, CountryInstance> mapCountries = map.getCountries();
        Question question = getCurrentQuestion();
        List<Country> countries = question.getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            Country country = countries.get(i);
            int dec = decisions[i];
            Question q = mquestions.get(currentQuestion);
            try {
                int qq = q.getCorrectAnswer(country);
                if (Math.abs(dec - qq) == 0) {
                    score += 1;
                }
            } catch (Exception e) {
                int qq = q.getCorrectAnswer(country);
                e.printStackTrace();
            }
        }
        return score;
    }

    public Question getCurrentQuestion() {
        return mquestions.get(currentQuestion);
    }

    public int getMaxResult() {
        return maxScore;
    }

    public int getCurrentQuestionInt() {
        return currentQuestion;
    }

    public static Set<Country> getEnabledCountries() {
        final List<String> enabledEuCodes = CountryUtil.getEurostatCodes();
        return enabledEuCodes.stream().map(Country.Builder::fromEuCode).collect(Collectors.toSet());
    }
}
