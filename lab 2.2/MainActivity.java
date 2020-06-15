package com.example.lab22;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static int n = 12;
    private static int N = 64;
    private static double W = 1800;
    private double[] signal = new double[N];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(generateSignal(signal));
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(fastFourier());
        GraphView graph = findViewById(R.id.graph1);
        customizeGraph(graph, series1, -5, 6);
        graph = findViewById(R.id.graph2);
        customizeGraph(graph, series2, 0, 70);

        double fastFourierTotal = 0;
        double discreteFourierTotal = 0;
        for (int i = 0; i < 1000; i++) {
            long time = System.currentTimeMillis();
            fastFourier();
            fastFourierTotal += (System.currentTimeMillis() - time);

            time = System.currentTimeMillis();
            discreteFourier();
            discreteFourierTotal += (System.currentTimeMillis() - time);
        }

        Toast.makeText(getApplicationContext(), String.format(
                "Results for 1000 iteration: fft = %s ms; dft = %s ms",
                Math.round(fastFourierTotal),
                Math.round(discreteFourierTotal)
        ), Toast.LENGTH_LONG).show();
    }

    private DataPoint[] generateSignal(double[] res) {
        double phi;
        double A;
        double x;
        DataPoint[] data = new DataPoint[N];
        Random rnd = new Random();
        for (int i = 0; i < N; i++) {
            phi = rnd.nextDouble();
            A = rnd.nextDouble();
            x = 0.0;
            for (int j = 0; j < n; j++) {
                x += A * Math.sin(W / (j + 1) * i + phi);
            }
            res[i] = x;
            data[i] = new DataPoint(i, x);
        }
        return data;
    }

    private DataPoint[] fastFourier() {
        DataPoint[] res = new DataPoint[N];
        for (int p = 0; p < N; p++) {
            double temp = 4 * Math.PI * p / N;
            double real2;
            double imagine1;
            double imagine2;
            double real1 = real2 = imagine1 = imagine2 = 0;
            for (int k = 0; k < N / 2 - 1; k++) {
                double tmp = 4 * Math.PI * p * k / N;
                real1 += signal[2*k] * Math.cos(tmp);
                imagine1 += signal[2*k] * Math.sin(tmp);
                real2 += signal[2*k+1] * Math.cos(tmp);
                imagine2 += signal[2*k+1] * Math.sin(tmp);
            }
            if (p < N / 2) {
                res[p] = new DataPoint(p, Math.sqrt(Math.pow((real2
                        + real1 * Math.cos(temp)), 2) + Math.pow((imagine2
                        + imagine1 * Math.sin(temp)), 2)));
            } else {
                res[p] = new DataPoint(p, Math.sqrt(Math.pow((real2
                        - real1 * Math.cos(temp)), 2) + Math.pow((imagine2
                        - imagine1 * Math.sin(temp)), 2)));
            }
        }
        return res;
    }

    private DataPoint[] discreteFourier() {
        double[] real = new double[N];
        double[] imagine = new double[N];
        DataPoint[] res = new DataPoint[N];



        for (int p = 0; p < N; p++) {
            for (int k = 0; k < N; k++) {
                real[p] += signal[k] * Math.cos(2 * Math.PI * p * k / N);
                imagine[p] += signal[k] * Math.sin(2 * Math.PI * p * k / N);
            }
            res[p] = new DataPoint(p, Math.sqrt(Math.pow(real[p], 2) + Math.pow(imagine[p], 2)));
        }
        return res;
    }

    private void customizeGraph(GraphView graph, LineGraphSeries line, int miny, int maxy) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(maxy);
        graph.getViewport().setMinY(miny);
        graph.getViewport().setScrollable(true);
        graph.addSeries(line);
    }
}
