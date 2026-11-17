package com.example.headunitapplication.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.headunitapplication.models.DynoCurve;

/** Draws a dyno curve with a marker that follows the engine's current RPM. */
public class DynoGraphView extends View {

    private static final int RPM_TICK_SPACING = 1000;
    private static final int POWER_GRID_SPACING = 100;
    private static final double POWER_HEADROOM = 1.1;
    private static final long MARKER_ANIMATION_MS = 200;
    private static final float NO_RPM = -1f;

    private static final int GRID_ALPHA = 0x33;
    private static final float LABEL_TEXT_SP = 15;
    private static final float READOUT_TEXT_SP = 23;

    private static final String SEPARATOR = " · ";
    private static final String NO_VALUE = "—";
    private static final String HP_UNIT = "HP";
    private static final String TORQUE_UNIT = "LB-FT";
    private static final String RPM_TITLE = "RPM";

    private final Paint hpPaint;
    private final Paint torquePaint;
    private final Paint axisPaint;
    private final Paint gridPaint;
    private final Paint markerPaint;
    private final Paint dotPaint;
    private final Paint labelPaint;
    private final Paint readoutPaint;

    private final Path hpPath = new Path();
    private final Path torquePath = new Path();
    private final Path axisPath = new Path();
    private final Path gridPath = new Path();
    private final RectF plot = new RectF();
    private final StringBuilder readout = new StringBuilder();

    private final ValueAnimator markerAnimator;

    private DynoCurve curve;
    private double powerAxisMax;
    private float markerRpm = NO_RPM;

    private float readoutBaseline;
    private float xLabelBaseline;
    private float bottomRowBaseline;
    private float hpLegendX;
    private float torqueLegendX;
    private String[] xLabels = new String[0];
    private float[] xLabelPositions = new float[0];
    private String[] yLabels = new String[0];
    private float[] yLabelBaselines = new float[0];

    public DynoGraphView(Context context) {
        this(context, null);
    }

    public DynoGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        hpPaint = strokePaint(dp(4));
        torquePaint = strokePaint(dp(2));
        torquePaint.setPathEffect(new DashPathEffect(new float[]{dp(8), dp(6)}, 0));
        axisPaint = strokePaint(dp(2));
        gridPaint = strokePaint(dp(1));
        gridPaint.setAlpha(GRID_ALPHA);
        markerPaint = strokePaint(dp(2));

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);

        labelPaint = textPaint(sp(LABEL_TEXT_SP));
        readoutPaint = textPaint(sp(READOUT_TEXT_SP));
        readoutPaint.setTextAlign(Paint.Align.RIGHT);

        markerAnimator = ValueAnimator.ofFloat(0f, 1f);
        markerAnimator.setDuration(MARKER_ANIMATION_MS);
        markerAnimator.setInterpolator(new LinearInterpolator());
        markerAnimator.addUpdateListener(animation -> {
            markerRpm = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    public void setCurve(DynoCurve curve) {
        this.curve = curve;
        markerAnimator.cancel();
        markerRpm = NO_RPM;
        rebuildGeometry();
        invalidate();
    }

    /** Glides the marker to the given RPM, or hides it when the RPM is negative (no telemetry). */
    public void setRpm(int rpm) {
        if (curve == null) {
            return;
        }

        markerAnimator.cancel();
        if (rpm < 0) {
            markerRpm = NO_RPM;
            invalidate();
            return;
        }

        float target = Math.max(curve.minRpm(), Math.min(curve.maxRpm(), rpm));
        if (markerRpm == NO_RPM) {
            markerRpm = target;
            invalidate();
            return;
        }

        markerAnimator.setFloatValues(markerRpm, target);
        markerAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rebuildGeometry();
    }

    @Override
    protected void onDetachedFromWindow() {
        markerAnimator.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (curve == null || plot.isEmpty()) {
            return;
        }

        canvas.drawPath(gridPath, gridPaint);
        canvas.drawPath(axisPath, axisPaint);

        labelPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < xLabels.length; i++) {
            canvas.drawText(xLabels[i], xLabelPositions[i], xLabelBaseline, labelPaint);
        }
        canvas.drawText(RPM_TITLE, plot.centerX(), bottomRowBaseline, labelPaint);

        labelPaint.setTextAlign(Paint.Align.RIGHT);
        float yLabelRight = plot.left - dp(8);
        for (int i = 0; i < yLabels.length; i++) {
            canvas.drawText(yLabels[i], yLabelRight, yLabelBaselines[i], labelPaint);
        }

        labelPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(HP_UNIT, hpLegendX, bottomRowBaseline, labelPaint);
        canvas.drawText(TORQUE_UNIT, torqueLegendX, bottomRowBaseline, labelPaint);

        canvas.drawPath(torquePath, torquePaint);
        canvas.drawPath(hpPath, hpPaint);

        readout.setLength(0);
        if (markerRpm == NO_RPM) {
            readout.append(NO_VALUE).append(' ').append(HP_UNIT)
                    .append(SEPARATOR)
                    .append(NO_VALUE).append(' ').append(TORQUE_UNIT);
        } else {
            double hp = curve.hpAt(markerRpm);
            double torque = curve.torqueAt(markerRpm);
            float x = xFor(markerRpm);
            canvas.drawLine(x, plot.top, x, plot.bottom, markerPaint);
            canvas.drawCircle(x, yFor(hp), dp(5), dotPaint);
            canvas.drawCircle(x, yFor(torque), dp(5), dotPaint);

            readout.append(Math.round(hp)).append(' ').append(HP_UNIT)
                    .append(SEPARATOR)
                    .append(Math.round(torque)).append(' ').append(TORQUE_UNIT);
        }
        canvas.drawText(readout, 0, readout.length(), plot.right, readoutBaseline, readoutPaint);
    }

    private void rebuildGeometry() {
        hpPath.reset();
        torquePath.reset();
        axisPath.reset();
        gridPath.reset();
        plot.setEmpty();
        if (curve == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        float labelHeight = labelPaint.descent() - labelPaint.ascent();
        float readoutHeight = readoutPaint.descent() - readoutPaint.ascent();
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float right = getWidth() - getPaddingRight();
        float bottom = getHeight() - getPaddingBottom();

        readoutBaseline = top - readoutPaint.ascent();
        bottomRowBaseline = bottom - labelPaint.descent();
        plot.set(
                left + labelPaint.measureText("0000") + dp(8),
                top + readoutHeight + dp(16),
                right - labelPaint.measureText("0k") / 2,
                bottom - 2 * labelHeight - dp(16));
        xLabelBaseline = plot.bottom + dp(8) - labelPaint.ascent();
        if (plot.width() <= 0 || plot.height() <= 0) {
            plot.setEmpty();
            return;
        }

        int peak = Math.max(curve.peakHp(), curve.peakTorque());
        powerAxisMax = Math.ceil(peak * POWER_HEADROOM / POWER_GRID_SPACING) * POWER_GRID_SPACING;

        axisPath.moveTo(plot.left, plot.top);
        axisPath.lineTo(plot.left, plot.bottom);
        axisPath.lineTo(plot.right, plot.bottom);

        int firstTick = (int) Math.ceil(curve.minRpm() / (double) RPM_TICK_SPACING) * RPM_TICK_SPACING;
        int tickCount = Math.max(0, (curve.maxRpm() - firstTick) / RPM_TICK_SPACING + 1);
        xLabels = new String[tickCount];
        xLabelPositions = new float[tickCount];
        for (int i = 0; i < tickCount; i++) {
            int tickRpm = firstTick + i * RPM_TICK_SPACING;
            float x = xFor(tickRpm);
            gridPath.moveTo(x, plot.top);
            gridPath.lineTo(x, plot.bottom);
            xLabels[i] = (tickRpm / 1000) + "k";
            xLabelPositions[i] = x;
        }

        int gridCount = (int) (powerAxisMax / POWER_GRID_SPACING);
        float labelCentreOffset = -(labelPaint.ascent() + labelPaint.descent()) / 2;
        yLabels = new String[gridCount];
        yLabelBaselines = new float[gridCount];
        for (int i = 0; i < gridCount; i++) {
            int value = (i + 1) * POWER_GRID_SPACING;
            float y = yFor(value);
            gridPath.moveTo(plot.left, y);
            gridPath.lineTo(plot.right, y);
            yLabels[i] = String.valueOf(value);
            yLabelBaselines[i] = y + labelCentreOffset;
        }

        for (int i = 0; i < curve.pointCount(); i++) {
            float x = xFor(curve.rpmPoint(i));
            float hpY = yFor(curve.hpPoint(i));
            float torqueY = yFor(curve.torquePoint(i));
            if (i == 0) {
                hpPath.moveTo(x, hpY);
                torquePath.moveTo(x, torqueY);
            } else {
                hpPath.lineTo(x, hpY);
                torquePath.lineTo(x, torqueY);
            }
        }

        // The legend's line samples share the curves' paths so they pick up the same stroke.
        float sampleLength = dp(24);
        float gap = dp(8);
        float legendY = bottomRowBaseline + (labelPaint.ascent() + labelPaint.descent()) / 2;
        torqueLegendX = plot.right - labelPaint.measureText(TORQUE_UNIT);
        float torqueSampleEnd = torqueLegendX - gap;
        torquePath.moveTo(torqueSampleEnd - sampleLength, legendY);
        torquePath.lineTo(torqueSampleEnd, legendY);
        hpLegendX = torqueSampleEnd - sampleLength - 2 * gap - labelPaint.measureText(HP_UNIT);
        float hpSampleEnd = hpLegendX - gap;
        hpPath.moveTo(hpSampleEnd - sampleLength, legendY);
        hpPath.lineTo(hpSampleEnd, legendY);
    }

    private float xFor(double rpm) {
        return plot.left + (float) curve.rpmFraction(rpm) * plot.width();
    }

    private float yFor(double power) {
        return plot.bottom - (float) (power / powerAxisMax) * plot.height();
    }

    private Paint strokePaint(float width) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private Paint textPaint(float size) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(size);
        return paint;
    }

    private float dp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private float sp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }
}
