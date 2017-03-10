package com.wordpress.lonelytripblog.circlesminesweeper;

import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class choose_level extends AppCompatActivity {
    private ImageButton[] level = new ImageButton[6];
    private ImageView back_img;
    private ImageButton continue_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_level_view);
        level[0] = (ImageButton) findViewById(R.id.first);
        level[1] = (ImageButton) findViewById(R.id.second);
        level[2] = (ImageButton) findViewById(R.id.third);
        level[3] = (ImageButton) findViewById(R.id.fourth);
        level[4] = (ImageButton) findViewById(R.id.fifth);
        level[5] = (ImageButton) findViewById(R.id.sixth);
        continue_btn = (ImageButton) findViewById(R.id.continue_btn);
        back_img = (ImageView) findViewById(R.id.Background_image);
        if (getResources().getConfiguration().orientation ==  2) {
            back_img.setImageResource(R.drawable.background_level_land);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("levels", MODE_PRIVATE);
        int opened_levels = sharedPreferences.getInt("opened_levels", 1);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int size_of_side = (metrics.widthPixels > metrics.heightPixels) ?
                metrics.heightPixels/5: metrics.widthPixels/4;
        Bitmap lock = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.lock),
                size_of_side/2, size_of_side/2, false);
        if (sharedPreferences.getInt("game_saved", 0) == 1) {
            Bitmap play_bmp = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(getResources(),R.drawable.play),
                    size_of_side, size_of_side, false);
            continue_btn.setImageBitmap(play_bmp);
            continue_btn.setVisibility(View.VISIBLE);
        } else {
            continue_btn.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < 6; i++) {
            if (i >= opened_levels) {
                level[i].setEnabled(false);
            } else {
                level[i].setEnabled(true);
            }
        }

        for (int i = 0; i<6; i++) {
            Bitmap button = Bitmap.createBitmap(
                                    size_of_side, size_of_side, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(button);
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            int color = Color.rgb(255,255,255);
            int color2 = Color.rgb(0,0,0);
            switch (i) {
                case 0:     // Blue
                    color = Color.rgb(81,118,151);
                    color2 = Color.rgb(38,55,70);
                    //paint.setColor(Color.rgb(81,118,151));
                    break;
                case 1:     // Purple
                    color = Color.rgb(200,60,190);
                    color2 = Color.rgb(77,21,72);
                    //paint.setColor(Color.rgb(200,60,190));
                    break;
                case 2:     // Red
                    color = Color.rgb(255,55,55);
                    color2 = Color.rgb(89,0,0);
                    //paint.setColor(Color.rgb(255,55,55));
                    break;
                case 3:     // Brown
                    color = Color.rgb(255, 128, 0);
                    color2 = Color.rgb(108,54,0);
                    //paint.setColor(Color.rgb(255, 128, 0));
                    break;
                case 4:     // Yellow
                    color = Color.rgb(250,243,12);
                    color2 = Color.rgb(89,87,2);
                    //paint.setColor(Color.rgb(250,243,12));
                    break;
                case 5:     // Green
                    color = Color.rgb(0,174,44);
                    color2 = Color.rgb(0,45,12);
                    //paint.setColor(Color.rgb(0,174,44));
                    break;
            }
            RadialGradient rd = new RadialGradient(size_of_side/2, size_of_side/2,
                        size_of_side/2, color, color2, Shader.TileMode.CLAMP);
            paint.setDither(true);
            paint.setShader(rd);
            canvas.drawCircle(size_of_side/2, size_of_side/2, size_of_side/2, paint);
            paint.setTextSize(size_of_side/2);
            paint.setDither(false);
            paint.setShader(null);
            paint.setColor(Color.BLACK);
            if (i >= opened_levels) {
                canvas.drawBitmap(lock, size_of_side / 4, size_of_side / 4, paint);
            } else {
                canvas.drawText(Integer.toString(i + 1), size_of_side / 2,
                        size_of_side / 2 + size_of_side / 8, paint);
            }
            level[i].setImageBitmap(button);
            //level[i].setBackground(null);
        }
    }
    public void btn_1(View view) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", 1);
        startActivity(intent);
    }
    public void btn_2(View view) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", 2);
        startActivity(intent);
    }
    public void btn_3(View view) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", 3);
        startActivity(intent);
    }
    public void btn_4(View view) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", 4);
        startActivity(intent);
    }
    public void btn_5(View view) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", 5);
        startActivity(intent);
    }
    public void btn_6(View view) {
        DialogFragment choose_dialog = new custom_level_dialog();
        //choose_dialog.setCancelable(false);
        choose_dialog.show(getSupportFragmentManager(), "choose");
    }
    public void set_custom(int field_size, int number_of_mines) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", 6);
        intent.putExtra("field_size", field_size);
        intent.putExtra("number_of_mines", number_of_mines);
        startActivity(intent);
    }
    public void continue_game(View view) {
        Intent intent = new Intent(this, game.class);
        intent.putExtra("level", -1);
        startActivity(intent);
    }
    public void back_to_menu(View view) {
        onBackPressed();
    }


}
