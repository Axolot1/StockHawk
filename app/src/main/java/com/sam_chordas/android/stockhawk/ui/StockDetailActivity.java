package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.mvp.StockDetailPresenter;
import com.sam_chordas.android.stockhawk.mvp.StockDetailView;
import com.sam_chordas.android.stockhawk.retrofit.bean.ChartDataResult;
import com.sam_chordas.android.stockhawk.retrofit.bean.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
 * Created by axolotl on 16/5/16.
 */
public class StockDetailActivity extends
        MvpLceViewStateActivity<SwipeRefreshLayout, ChartDataResult, StockDetailView, StockDetailPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, StockDetailView {

    private LineChartView mChart;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    private static String mSymbol;
    private ChartDataResult mCharDataResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        setRetainInstance(true);

        mSymbol = getIntent().getStringExtra("symbol");

        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);

        mChart = (LineChartView)findViewById(R.id.chart);
        mChart.setOnValueTouchListener(new ValueTouchListener());
        getSupportActionBar().setTitle(mSymbol);


    }

    private void setupData(Cursor cursor) {
        List<Line> lines = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        float maxPrice = 10;
        float minPrice = -1;
        int index = 1;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if(cursor.getString(cursor.getColumnIndex(QuoteColumns.CREATED)) == null){
                continue;
            }
            float price = Float.valueOf(cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            String date = cursor.getString(cursor.getColumnIndex(QuoteColumns.CREATED)).split("T")[1];
            String time = date.substring(0, date.length()-4);
            maxPrice = Math.max(maxPrice, price);
            minPrice = Math.min(minPrice, price);
            axisValues.add(new AxisValue(index).setLabel(time));
            values.add(new PointValue(index, price));
            index++;
        }


        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_ORANGE);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        LineChartData lineChartData = new LineChartData(lines);
        if (hasAxes) {
            Axis axisY = new Axis().setHasLines(true);
            lineChartData.setAxisYLeft(axisY);
            lineChartData.setAxisXBottom(new Axis(axisValues));
        } else {
            lineChartData.setAxisXBottom(null);
            lineChartData.setAxisYLeft(null);
        }

        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        mChart.setLineChartData(lineChartData);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        mChart.setViewportCalculationEnabled(false);

        final Viewport v = new Viewport(mChart.getMaximumViewport());
        v.bottom = minPrice;
        v.top = maxPrice + 10;
        v.left = minPrice;
        v.right = index-1;
        mChart.setMaximumViewport(v);
        mChart.setCurrentViewport(v);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public StockDetailPresenter createPresenter() {
        return new StockDetailPresenter();
    }

    @Override
    public LceViewState<ChartDataResult, StockDetailView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public ChartDataResult getData() {
        return mCharDataResult == null ? null : mCharDataResult;
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        if (pullToRefresh) {
            return getString(R.string.errorMsg_1);
        } else {
            return getString(R.string.errorMsg_2);
        }
    }

    @Override
    public void setData(ChartDataResult data) {
        mCharDataResult = data;
        List<Line> lines = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        List<Series> seriesData = data.getSeries();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

        for(int i = 0; i < seriesData.size(); i++){
            Series series = seriesData.get(i);
            Date date = new Date(series.getTimestamp() * 1000L);
            axisValues.add(new AxisValue(i).setLabel(dateFormat.format(date)));
            values.add(new PointValue(i, series.getClose()));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_ORANGE);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        LineChartData lineChartData = new LineChartData(lines);
        if (hasAxes) {
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisY.setName("   ");
            }
            lineChartData.setAxisYLeft(axisY);
            lineChartData.setAxisXBottom(new Axis(axisValues));
        } else {
            lineChartData.setAxisXBottom(null);
            lineChartData.setAxisYLeft(null);
        }

        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        mChart.setLineChartData(lineChartData);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        mChart.setViewportCalculationEnabled(false);

        final Viewport v = new Viewport(mChart.getMaximumViewport());
        v.bottom = data.getRanges().getClose().getMin().floatValue();
        v.top = data.getRanges().getClose().getMax().floatValue();
        v.left = 0;
        v.right = seriesData.size();
        mChart.setMaximumViewport(v);
        mChart.setCurrentViewport(v);

    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        mChart.setVisibility(View.VISIBLE);
        contentView.setEnabled(false);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadTodayStockDetail(pullToRefresh, mSymbol);
    }

    @Override
    public void onRefresh() {
        presenter.loadTodayStockDetail(true, mSymbol);
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(StockDetailActivity.this, getString(R.string.price)+value.getY(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
