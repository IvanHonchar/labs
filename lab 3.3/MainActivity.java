package com.example.lab33;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RadioGroup radioGroup1;
    RadioButton radioButton1;
    EditText aInput, bInput, cInput, dInput, yInput;
    int aValue, bValue, cValue, dValue, yValue;
    double mutation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioGroup1 = findViewById(R.id.radioGroup1);
    }

    public void checkButton(View v) {
        int radioId = radioGroup1.getCheckedRadioButtonId();
        radioButton1 = findViewById(radioId);
        Toast.makeText(this, "Selected mutation = " + radioButton1.getText(),
                Toast.LENGTH_SHORT).show();
    }

    public void startExecution(View v) {
        int radioId1 = radioGroup1.getCheckedRadioButtonId();
        radioButton1 = findViewById(radioId1);
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
        mutation = Double.parseDouble(radioButton1.getText().toString());
        hideKeyboard();
        showToast(countGen());
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

    private String countGen() {
        Random rand = new Random();
        ArrayList<ArrayList<Integer>> popList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ArrayList<Integer> tmp = new ArrayList<>(4);
            for (int j = 0; j < 4; j++) {
                tmp.add(rand.nextInt(yValue / 2));
            }
            popList.add(tmp);
        }
        do {
            double sum = 0;
            int[] delta = new int[5];
            for (int i = 0; i < 5; i++) {
                delta[i] = Math.abs(yValue - popList.get(i).get(0) * aValue - popList.get(i).get(1) * bValue - popList.get(i).get(2) * cValue - popList.get(i).get(3) * dValue);
                if (delta[i] == 0) {
                    return "x1 = " + popList.get(i).get(0) + "\nx2 = " + popList.get(i).get(1) + "\nx3 = " + popList.get(i).get(2) + "\nx4 = " + popList.get(i).get(3) +
                            "\n\nEquation: " + aValue + " * " + popList.get(i).get(0) + " + " + bValue + " * " + popList.get(i).get(1) + " + " + cValue + " * " + popList.get(i).get(2) + " + " + dValue + " * " + popList.get(i).get(3) + " = " + yValue;
                }
                sum += 1.0 / delta[i];
            }
            double[] percentParent = new double[5];
            for (int i = 0; i < 5; i++) {
                percentParent[i] = 1.0 / (delta[i] * sum) * 100;
            }
            double[] child = new double[6];
            child[0] = 0;
            for (int i = 0; i < 4; i++) {
                child[i + 1] = child[i] + percentParent[i];
            }
            child[4] = 100;
            ArrayList<ArrayList<Integer>> parentList = new ArrayList<>();
            int parent;
            for (int i = 0; i < 10; i++) {
                parent = rand.nextInt(100);
                for (int j = 0; j < child.length - 1; j++) {
                    if (child[j] <= parent && child[j + 1] > parent) {
                        parentList.add(popList.get(j));
                    }
                }
            }
            popList.clear();
            for (int i = 0; i < parentList.size(); i = i + 2) {
                ArrayList<Integer> nextGen = new ArrayList<>(4);
                parent = (int)(Math.random() * 3 + 1);
                for (int j = 0; j < parent; j++) {
                    nextGen.add(parentList.get(i).get(j));
                }
                for (int j = parent; j < 4; j++) {
                    nextGen.add(parentList.get(i + 1).get(j));
                }
                popList.add(nextGen);
            }
            parentList.clear();
            if (Math.random() < mutation) {
                int change = rand.nextInt(5);
                for (int i = 0; i < change; i++) {
                    popList.get(rand.nextInt(5)).set(rand.nextInt(4),
                            rand.nextInt(yValue / 2));
                }
            }
        } while (true);
    }
}
