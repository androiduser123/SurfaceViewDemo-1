package atiatulmaula.surfaceviewdemo_1;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements Runnable {
	// initialization for view
	private Button swapBtn;
	private SurfaceView surface;

	// Allows you to control the surface size and format, edit the pixels in the
	// surface, and monitor changes to the surface.
	private SurfaceHolder holder;

	private boolean locker = true;
	private Thread thread;
	private int radiusLeft, radiusRight;
	private boolean left = true;

	private static final int baseRadius = 10;
	private static final int maxRadius = 50;
	private static final int baseSpeed = 1;
	private int speed = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		swapBtn = (Button) findViewById(R.id.btn);
		surface = (SurfaceView) findViewById(R.id.srf);
		holder = surface.getHolder();

		thread = new Thread(this);
		thread.start();
		swapBtn.setOnClickListener(new OnClickListener() {

			/**
			 * swap condition for bounce ball (left or right ball)
			 */
			@Override
			public void onClick(View v) {
				left = !left;
			}
		});
	}

	@Override
	public void run() {
		while (locker) {
			// bounce ball on surface will be updating if surface have ready on
			// windows (surface)
			if (!holder.getSurface().isValid()) {
				continue;
			}

			// Start editing pixels in this surface.
			Canvas canvas = holder.lockCanvas();

			draw(canvas);

			// system will paint with this canvas to the surface.
			holder.unlockCanvasAndPost(canvas);
		}
	}

	private void draw(Canvas canvas) {
		// paint a background color
		canvas.drawColor(getResources().getColor(R.color.red));

		// paint a rectangular shape that fill the surface.
		int border = 10;
		RectF r = new RectF(border, border, canvas.getWidth() - border,
				canvas.getHeight() - border);
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.black));
		canvas.drawRect(r, paint);

		/*
		 * I want to paint the two red circles. one of circles will bounce, tile
		 * the button pressed and then other circle begin bouncing.
		 */
		calculateRadiuses();

		// paint left circle
		paint.setColor(getResources().getColor(R.color.red));
		canvas.drawCircle(canvas.getWidth() / 4, canvas.getHeight() / 2,
				radiusLeft, paint);

		// paint right circle
		paint.setColor(getResources().getColor(R.color.red));
		canvas.drawCircle(canvas.getWidth() / 4 * 3, canvas.getHeight() / 2,
				radiusRight, paint);
	}

	private void calculateRadiuses() {
		if (left) {
			updateSpeed(radiusLeft);
			radiusLeft += speed;
			radiusRight = baseRadius;
		} else {
			updateSpeed(radiusRight);
			radiusRight += speed;
			radiusLeft = baseRadius;
		}
	}

	private void updateSpeed(int radius) {
		if (radius >= maxRadius) {
			speed = -baseSpeed;
		} else if (radius <= baseRadius) {
			speed = baseSpeed;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		pause();
	}

	private void pause() {
		locker = false;
		while (true) {
			try {
				// wait until thread die, then exit while loop and release a
				// thread
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
		thread = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		resume();
	}

	private void resume() {
		// restart thread and open locker for run
		locker = true;
	}
}