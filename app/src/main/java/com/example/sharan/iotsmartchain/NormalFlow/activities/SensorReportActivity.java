package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Created by Sharan on 28-03-2018.
 */
public class SensorReportActivity extends BaseActivity {

    @BindView(R.id.viewLiveData)
    TextView mSensorViewLabel;

    @BindView(R.id.SensorChartView)
    LineChartView mLineChartView;

    @BindView(R.id.previewsLabel)
    TextView mPreviewsLabel;

    @BindView(R.id.PreviewsChartView)
    LineChartView mPreviewsChartView;

    private String mSensorType, mSensorResult;
    private LineChartData data;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 12;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;

    List<PointValue> pointValues = new ArrayList<>();
    int maxNumberOfPoints = 50;
    Handler mHandler = new Handler();

    List<PointValue> mPrePointValues = new ArrayList<>();
    Handler mPreHandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_result);

        injectViews();

        Bundle bundle  = getIntent().getExtras();
        if (bundle != null) {
            mSensorType = bundle.getString("SENSOR");
            mSensorResult = bundle.getString("RESULT");
        }

        //Set values current sensor data and chart
        setCurrentSensorData();

        //set previews / yesterday sensor data and chart
        setPreviewsSensorData();
    }

    private void setPreviewsSensorData() {
        mPreviewsLabel.setText("One day Preview "+mSensorType + mSensorResult);

        mPreviewsChartView.setInteractive(true);
        mPreviewsChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        mPreviewsChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        List<Line> lines = new ArrayList<>();
        Line line = new Line();
        line.setHasLines(true);
        line.setHasPoints(false);
        line.setFilled(true);
        line.setColor(Color.parseColor("#bcbcbc"));
        lines.add(line);
        LineChartData data = new LineChartData(lines);

        Axis axisX = new Axis().setName("Time in Sec");
        Axis axisY = new Axis().setHasLines(true).setName("Temperature in "+"\u2103");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mPreviewsChartView.setLineChartData(data);

        drawPreviewGraph();
    }

    private void setCurrentSensorData() {
        mSensorViewLabel.setText("Current "+mSensorType + mSensorResult);

        mLineChartView.setInteractive(true);
        mLineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        mLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        List<Line> lines = new ArrayList<>();
        Line line = new Line();
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setColor(Color.GRAY);
        lines.add(line);
        LineChartData data = new LineChartData(lines);

        Axis axisX = new Axis().setName("Time in Sec");
        Axis axisY = new Axis().setHasLines(true).setName("Temperature in "+"\u2103");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mLineChartView.animate();
        mLineChartView.setLineChartData(data);

        drawGraph();
    }

    private void drawGraph() {
        mHandler.postDelayed(mRunnable, 25);
    }

    Runnable mRunnable = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            float yValue = (float) (Math.random() * 100);
            LineChartData data = mLineChartView.getLineChartData();
            pointValues.add(new PointValue(i++, yValue));
            data.getLines().get(0).setValues(new ArrayList<>(pointValues));
            mLineChartView.setLineChartData(data);
            setViewport();
            if (i < 500) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };


    private void drawPreviewGraph(){
        mPreHandler.postDelayed(mPreviewRunnable, 25);
    }

    Runnable mPreviewRunnable = new Runnable(){
        private int i = 0;
        @Override
        public void run() {
            float yValue = (float) (Math.random() * 100);
            LineChartData data = mPreviewsChartView.getLineChartData();
            mPrePointValues.add(new PointValue(i++, yValue));
            data.getLines().get(0).setValues(new ArrayList<>(mPrePointValues));
            mPreviewsChartView.setLineChartData(data);
            setViewport();
            if (i < 500) {
                mPreHandler.postDelayed(this, 25);
            }
        }
    };

    private void setViewport() {
        int size = pointValues.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(mLineChartView.getMaximumViewport());
            viewport.left = size - 50;
            mLineChartView.setCurrentViewport(viewport);
        }
    }

    private void generateData() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
            }
        }
    }

    private void generateValues() {
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j+0.5f, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);

            //line.setHasGradientToTransparent(hasGradientToTransparent);

            if (pointsHaveDifferentColor){
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Time in Sec");
                axisY.setName("Temperature in "+"\u2103");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mLineChartView.setLineChartData(data);
    }

    private LineChartData getCurrentLineChart() {
        final int numValues = 4;
        LineChartData data = new LineChartData();
        List<PointValue> values = new ArrayList<PointValue>(numValues);
        values.add(new PointValue(0, 20));
        values.add(new PointValue(10, 40));
        values.add(new PointValue(20, 30));
        values.add(new PointValue(30, 40));
        Line line = new Line(values)
                .setColor(Color.GRAY)
                .setCubic(true)
                .setHasPoints(false)
                .setFilled(true)
                .setStrokeWidth(0);
        List<Line> lines = new ArrayList<Line>(1);
        lines.add(line);
        data.setLines(lines);
        return data;
    }

    private Axis setChartAxisXBottom() {

        List axisLabelsX = new ArrayList<>();

        AxisValue av0 = new AxisValue(0).setLabel("0");
        AxisValue av1 = new AxisValue(10).setLabel("25");
        AxisValue av2 = new AxisValue(20).setLabel("75");
        AxisValue av3 = new AxisValue(40).setLabel("100");

        axisLabelsX.add(av0);
        axisLabelsX.add(av1);
        axisLabelsX.add(av3);
        axisLabelsX.add(av2);

        Axis axisX = new Axis(axisLabelsX);
        axisX.setHasLines(true);
        axisX.setName("Time in sec");
        axisX.setHasSeparationLine(false);
        axisX.setAutoGenerated(true);

        return axisX;
    }

    private Axis setChartAxisLift() {

        List axisLabelsY = new ArrayList<>();

        AxisValue axisValue1 = new AxisValue(0).setLabel("0");
        AxisValue axisValue3 = new AxisValue(20).setLabel("20");
        AxisValue axisValue5 = new AxisValue(40).setLabel("40");
        AxisValue axisValue7 = new AxisValue(60).setLabel("60");

        axisLabelsY.add(axisValue1);
        axisLabelsY.add(axisValue3);
        axisLabelsY.add(axisValue5);
        axisLabelsY.add(axisValue7);

        Axis axisY = new Axis(axisLabelsY);
        axisY.setHasLines(true);
        axisY.setName("Temperature in " + " \u2103");
        axisY.setHasSeparationLine(true);
        axisY.setAutoGenerated(true);

        return axisY;
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            // generateData();
            Toast.makeText(SensorReportActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }
    }

}
