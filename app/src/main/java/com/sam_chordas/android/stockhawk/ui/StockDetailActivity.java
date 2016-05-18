package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

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
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID = 7;
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

    public final static String[] days = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun",};
    private static String mSymbol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mSymbol = getIntent().getStringExtra("symbol");

        mChart = (LineChartView)findViewById(R.id.chart);
        mChart.setOnValueTouchListener(new ValueTouchListener());
        getSupportActionBar().setTitle(mSymbol);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

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
            if (hasAxesNames) {
                axisY.setName("Axis Y");
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
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.withSymbol(mSymbol),
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.CREATED, QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setupData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(StockDetailActivity.this, value.getY()+"", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
}
