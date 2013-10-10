package com.paymill.android.payment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.widget.EditText;

import com.paymill.android.samples.vouchermill.R;

/**
 * A custom EditText that looks Holo-Like on Android 2.X.
 *
 */
public class CustomEditText extends EditText {

	Paint paint;
	int width;
	int height;
	int activeColor;
	int inactiveColor;

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();

		// get border color from attributes(if none use orange)
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomEditText, 0, 0);

		activeColor = array.getColor(R.styleable.CustomEditText_borderColor,
				getResources().getColor(R.color.editTextBorderColor));
		inactiveColor = array.getColor(
				R.styleable.CustomEditText_inactiveBorderColor, getResources()
						.getColor(R.color.editTextInactiveBorderColor));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		width = w;
		height = h;

		// Orange color for focused and selected state
		Bitmap b = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas c = new Canvas(b);
		paint.setColor(activeColor);
		paint.setStrokeWidth(2.0f);

		// horizontal line
		c.drawLine(4, height - 2, width - 4, height - 2, paint);
		// left vertical line
		c.drawLine(4, height - 9, 4, height - 2, paint);
		// right vertical line
		c.drawLine(width - 4, height - 9, width - 4, height - 2, paint);

		// Grey Color for all other states
		Bitmap b2 = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas c2 = new Canvas(b2);
		paint.setColor(inactiveColor);
		paint.setStrokeWidth(1.0f);

		// horizontal line
		c2.drawLine(4, height - 2, width - 4, height - 2, paint);
		// left vertical line
		c2.drawLine(4, height - 9, 4, height - 2, paint);
		// right vertical line
		c2.drawLine(width - 4, height - 9, width - 4, height - 2, paint);

		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_focused },
				new BitmapDrawable(getResources(), b));
		drawable.addState(StateSet.WILD_CARD, new BitmapDrawable(
				getResources(), b2));
		setBackgroundDrawable(drawable);
		super.onSizeChanged(w, h, oldw, oldh);
	}

}
