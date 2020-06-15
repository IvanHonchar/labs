package com.example.lab33;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Random;
import java.util.Map;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Iterator;
import static java.util.Map.Entry.*;

public class MainActivity extends AppCompatActivity {

    EditText aInput, bInput, cInput, dInput, yInput;
    int aValue, bValue, cValue, dValue, yValue;
    double mutation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startExecution(View v) {
        aInput = findViewById(R.id.editText1);
        bInput = findViewById(R.id.editText2);
        cInput = findViewById(R.id.editText3);
        dInput = findViewById(R.id.editText4);
        yInput = findViewById(R.id.editText5);
        aValue = Integer.parseInt(aInput.getText().toString());
        bValue = Integer.parseInt(bInput.getText().toString());
        cValue = Integer.parseInt(cInput.getText().toString());
        dValue = Integer.parseInt(dInput.getText().toString());
        yValue = Integer.parseInt(yInput.getText().toString());
        hideKeyboard();

        int[] coeffs = { aValue, bValue, cValue, dValue };
        Equation equation = new Equation(coeffs, yValue);
        Population population = new Population(1000, 4, 10);

        int[] result = population.generate(equation);
        showToast("Answer is: " + Arrays.toString(result));

    }

    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class ShuffleArray {
    public static void shuffle(int[] array) {
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            int j = rand.nextInt(array.length);
            int temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
}

class Equation {
    public int result;
    public int[] coeffs;

    public Equation(int[] coeffs, int result) {
        this.coeffs = coeffs;
        this.result = result;
    }

    public int execute(int[] values) {
        int value = 0;
        for (int i = 0; i < this.coeffs.length; i++) {
            value += coeffs[i] * values[i];
        }
        return Math.abs(this.result - value);
    }
}
class Population {
    public int populationSize;
    public int chromosomeSize;
    public int[][] chromosomes;
    static Random random = new Random();

    public Population(
            int populationSize,
            int chromosomeSize,
            int geneRange
            ) {
        this.populationSize = populationSize;
        this.chromosomeSize = chromosomeSize;
        this.chromosomes = new int[populationSize][chromosomeSize];

        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < chromosomeSize; j++) {

                this.chromosomes[i][j] = random.nextInt(geneRange);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int[] generate(Equation equation) {
        Map<Integer, Integer> deltas = new TreeMap();
        int[][] tmp = new int[this.chromosomeSize][this.populationSize];

        Iterator it1;
        Iterator it2;
        Iterator it3;

        while (true) {
            for (int i = 0; i < this.populationSize; i++) {
                int delta = equation.execute(this.chromosomes[i]);
                deltas.put(i, delta);
            }

            it1 = deltas.entrySet().stream().sorted(comparingByValue()).iterator();
            it2 = deltas.entrySet().stream().sorted(comparingByValue()).iterator();
            it3 = deltas.entrySet().stream().sorted(comparingByValue()).iterator();

            Map.Entry best = (Map.Entry)it3.next();
            if ((int)best.getValue() == 0) {
                return this.chromosomes[(int)best.getKey()];
            }

            for (int i = 0; i < this.populationSize; i++) {
                Iterator it = i < this.populationSize - 2 ? it1 : it2;
                Map.Entry entry = (Map.Entry)it.next();
                int index = (int)entry.getKey();
                int delta = (int)entry.getValue();
                int step = (int)(1) + 1;
                int modify = random.nextInt(step) - ((int)(step / 2));
                for (int k = 0; k < this.chromosomeSize; k++) {
                    tmp[k][i] = this.chromosomes[index][k] + modify;
                }
            }

            for (int i = 0; i < this.chromosomeSize; i++) {
                ShuffleArray.shuffle(tmp[i]);
                for (int j = 0; j < this.populationSize; j++) {
                    this.chromosomes[j][i] = tmp[i][j];
                }
            }
        }
    }
}