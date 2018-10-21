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
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener,
        CustomLevelDialogFragment.CustomLevelDialogCallback {
    private ImageView game_image;
    private TextView out_score;
    private TextView out_mine;
    private TextView title_score;
    private TextView title_mine;
    private Paint my_paint;
    private ImageButton checking_btn;
    private ImageButton next_repeat_btn;
    private ImageView smile_out;
    private int radius;
    private int prime_x, prime_y, prime_r;
    private ArrayList<Circle> circles;
    private Circle taken_circle = null;
    private int size_row, size_col;
    private int[][] position;
    private int prime_row, prime_col;
    private int margin_width, margin_height;
    private int amount_of_mines = 2;
    private boolean game_over_flag = false;
    private boolean state_of_marking = false;
    private int amount_of_right_guesses = 0;
    private Bitmap bang;
    private Bitmap bomb_pict;
    private int level;
    private int score_of_mines;
    private int score = 0;
    private int amount_of_circles = 0;
    private int orientation;
    private int amount_of_alive;
    private boolean last_game_won;
    private boolean mines_set = false;
    private boolean combo = false;
    private boolean game_win_in_checking = false;
    private boolean game_over_in_checking = false;


    public void next_repeat(View view) {
        if (!last_game_won) {
        //if (next_repeat_btn.getText().equals(getString(R.string.try_again))) {
            while (!set_circles());
        } else {
            set_new_level();
        }
        checking_btn.setVisibility(View.VISIBLE);
        out_mine.setVisibility(View.VISIBLE);
        title_mine.setVisibility(View.VISIBLE);
        next_repeat_btn.setVisibility(View.INVISIBLE);
        smile_out.setVisibility(View.INVISIBLE);
    }

    public void checking_on_off(View view) {
     if (!state_of_marking) {
         state_of_marking = true;
         checking_btn.setImageResource(R.drawable.bomb_check_pressed);
     } else {
         state_of_marking = false;
         checking_btn.setImageResource(R.drawable.bomb_check);
     }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (game_over_flag) {
            checking_btn.setVisibility(View.INVISIBLE);
            out_mine.setVisibility(View.INVISIBLE);
            title_mine.setVisibility(View.INVISIBLE);
            next_repeat_btn.setVisibility(View.VISIBLE);
            next_repeat_btn.setImageResource(R.drawable.play);
            if (last_game_won) {
                smile_out.setImageResource(R.drawable.smile_win);
            } else {
                smile_out.setImageResource(R.drawable.smile_lost);
            }
            next_repeat_btn.setVisibility(View.VISIBLE);
            smile_out.setVisibility(View.VISIBLE);
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        game_image = (ImageView) findViewById(R.id.game_image);
        out_mine = (TextView) findViewById(R.id.mine_out);
        out_score = (TextView) findViewById(R.id.score_out);
        checking_btn = (ImageButton)findViewById(R.id.checking_btn);
        smile_out = (ImageView) findViewById(R.id.smile_out);
        next_repeat_btn = (ImageButton) findViewById(R.id.next_repeat_btn);
        title_mine = (TextView) findViewById(R.id.text_about_mines);
        title_score = (TextView) findViewById(R.id.text_about_score);
        game_image.setOnTouchListener(this);
        my_paint = new Paint();
        my_paint.setTextAlign(Paint.Align.CENTER);
        circles = new ArrayList<>();
        if (savedInstanceState != null) {
            final ArrayList<Circle> old_circles = savedInstanceState.getParcelableArrayList("circles");
            final int[][] old_position = (int[][]) savedInstanceState.getSerializable("position");
            amount_of_right_guesses = savedInstanceState.getInt("amount_of_right_guesses");
            amount_of_mines = savedInstanceState.getInt("amount_of_mines");
            game_over_flag = savedInstanceState.getBoolean("game_over_flag");
            state_of_marking = savedInstanceState.getBoolean("state_of_marking");
            score = savedInstanceState.getInt("score");
            score_of_mines = savedInstanceState.getInt("score_of_mines");
            amount_of_circles = savedInstanceState.getInt("amount_of_circles");
            level = savedInstanceState.getInt("level");
            amount_of_alive = savedInstanceState.getInt("amount_of_alive");
            mines_set = savedInstanceState.getBoolean("mines_set");
            last_game_won = savedInstanceState.getBoolean("last_game_won");
            if (state_of_marking) {
                checking_btn.setImageResource(R.drawable.bomb_check_pressed);
            }
            if (getResources().getConfiguration().orientation != savedInstanceState.getInt("orientation")) {
                game_image.post(new Runnable() {
                    @Override
                    public void run() {
                        recreate_circles(old_circles, old_position);
                    }
                });
            } else {
                circles = old_circles;
                position = old_position;
            }
        } else {
            Intent intent = getIntent();
            level = intent.getIntExtra("level", 1);
            if (level == -1) {
                load_game();
                if (state_of_marking) {
                    checking_btn.setImageResource(R.drawable.bomb_check_pressed);
                }
                if (getResources().getConfiguration().orientation != orientation) {
                    game_image.post(new Runnable() {
                        @Override
                        public void run() {
                            recreate_circles(circles, position);
                        }
                    });
                } else {
                    bang = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bang),
                            2*radius, 2*radius, false);
                    bomb_pict = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bomb),
                            2*radius, 2*radius, false);
                    game_image.post(new Runnable() {
                        @Override
                        public void run() {
                            redraw_game();
                        }
                    });
                }
            } else {
                // possible combination is 12, 24, 60
                if (level == 6) {
                    int field_size = intent.getIntExtra("field_size", 1);
                    int number_of_mines = intent.getIntExtra("number_of_mines", 5);
                    amount_of_mines = number_of_mines;
                    switch (field_size) {
                        case 1:
                            amount_of_circles = 12;
                            break;
                        case 2:
                            amount_of_circles = 24;
                            break;
                        case 3:
                            amount_of_circles = 60;
                            break;
                    }
                }
                set_level();
                game_image.post(new Runnable() {
                    @Override
                    public void run() {
                        while (!set_circles()) ;
                    }
                });
            }
        }

        orientation = getResources().getConfiguration().orientation;
        out_mine.setText( String.format(Locale.US, "%d", score_of_mines) );
        out_score.setText( String.format(Locale.US, "%d", score) );
    }
    public void recreate_circles(ArrayList<Circle> old_circles, int[][] old_position) {
        int circles_in_short_side, circles_in_long_side;
        int current_x, current_y;
        int current_row, current_col;
        int one_side;
        int number_in_old;
        Circle tmp_circle;
        boolean to_land;
        if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            to_land = true;
        } else {
            to_land = false;
        }


        int amount_of_circles = old_circles.size();
        if (amount_of_circles == 12) {
            circles_in_short_side = 3;
            circles_in_long_side  = 4;
        }
        else {
            if (amount_of_circles == 24) {
                circles_in_short_side = 4;
                circles_in_long_side = 6;
            }
            else {
                circles_in_short_side = 6;
                circles_in_long_side  = 10;
            }
        }
        if (game_image.getWidth() > game_image.getHeight()) {
            size_col = circles_in_long_side;
            size_row = circles_in_short_side;
            one_side = game_image.getHeight()/ circles_in_short_side;
            if ((circles_in_long_side * one_side) > (game_image.getWidth()) ) {
                one_side = game_image.getWidth() / circles_in_long_side;
            }
            margin_width = (game_image.getWidth() - one_side*circles_in_long_side)/2;
            margin_height = (game_image.getHeight() - one_side*circles_in_short_side)/2;
        } else {
            size_row = circles_in_long_side;
            size_col = circles_in_short_side;
            one_side = game_image.getWidth() / circles_in_short_side;
            if ((circles_in_long_side * one_side) > (game_image.getHeight()) ) {
                one_side = game_image.getHeight() / circles_in_long_side;
            }
            margin_width = (game_image.getWidth() - one_side*circles_in_short_side)/2;
            margin_height = (game_image.getHeight() - one_side*circles_in_long_side)/2;
        }
        int size_of_old_col = old_position[0].length;
        int size_of_old_row = old_position.length;
        position = new int[size_row][size_col];
        circles = new ArrayList<>();
        radius = one_side / 2;
        current_x = radius + margin_width;
        current_y = radius + margin_height;
        current_col = 0;
        current_row = 0;
        for (int i=0; i<amount_of_circles; i++ ) {
            if (i !=0) {
                current_y += radius*2;
                current_row ++;
            }
            if (current_row >= size_row) {
                current_y = radius + margin_height;
                current_x += radius*2;
                current_col ++;
                current_row = 0;
            }
            if (to_land) {
                number_in_old = old_position[current_col][size_of_old_col - current_row - 1];
            } else {
                number_in_old = old_position[size_of_old_row - current_col - 1][current_row];
            }
            tmp_circle = old_circles.get(number_in_old - 1);
            circles.add( new Circle(current_x, current_y, radius, tmp_circle.color, tmp_circle.with_mine)   );
            circles.get(circles.size()-1).alive = tmp_circle.alive;
            circles.get(circles.size()-1).animation = tmp_circle.animation;
            circles.get(circles.size()-1).marked = tmp_circle.marked;
            circles.get(circles.size()-1).mines_near = tmp_circle.mines_near;
            position[current_row][current_col] = circles.size();
        }
        bang = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bang),
                2*radius, 2*radius, false);
        bomb_pict = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bomb),
                2*radius, 2*radius, false);
        redraw_game();
    }
    public void game_over() {
        checking_btn.setVisibility(View.INVISIBLE);
        out_mine.setVisibility(View.INVISIBLE);
        title_mine.setVisibility(View.INVISIBLE);
        next_repeat_btn.setImageResource(R.drawable.play);
        last_game_won = false;
        smile_out.setImageResource(R.drawable.smile_lost);
        next_repeat_btn.setVisibility(View.VISIBLE);
        smile_out.setVisibility(View.VISIBLE);
        for (Circle circle : circles) {
            if (circle.alive) circle.alive = false;
        }
        redraw_game();
    }
    public void game_win() {
        SharedPreferences sharedPreferences = getSharedPreferences("levels", MODE_PRIVATE);
        int opened_levels = sharedPreferences.getInt("opened_levels", 1);
        if (opened_levels < (level+1)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("opened_levels", level+1);
            editor.apply();
        }
        int best_score;
        SimpleDateFormat output_time = new SimpleDateFormat("dd.MM.yy, H:mm", Locale.US);
        switch (level) {
            case 1:
                best_score = sharedPreferences.getInt("best_first", 0);
                if (score > best_score) {
                    Toast.makeText(getApplicationContext(),
                            "New record is set! Best score is "+score,
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("best_first", score);
                    editor.putString("best_first_time",
                            output_time.format(new Date()));
                    editor.apply();
                }
                break;
            case 2:
                best_score = sharedPreferences.getInt("best_second", 0);
                if (score > best_score) {
                    Toast.makeText(getApplicationContext(),
                            "New record is set! Best score is "+score,
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("best_second", score);
                    editor.putString("best_second_time",
                            output_time.format(new Date()));
                    editor.apply();
                }
                break;
            case 3:
                best_score = sharedPreferences.getInt("best_third", 0);
                if (score > best_score) {
                    Toast.makeText(getApplicationContext(),
                            "New record is set! Best score is "+score,
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("best_third", score);
                    editor.putString("best_third_time",
                            output_time.format(new Date()));
                    editor.apply();
                }
                break;
            case 4:
                best_score = sharedPreferences.getInt("best_fourth", 0);
                if (score > best_score) {
                    Toast.makeText(getApplicationContext(),
                            "New record is set! Best score is "+score,
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("best_fourth", score);
                    editor.putString("best_fourth_time",
                            output_time.format(new Date()));
                    editor.apply();
                }
                break;
            case 5:
                best_score = sharedPreferences.getInt("best_fifth", 0);
                if (score > best_score) {
                    Toast.makeText(getApplicationContext(),
                            "New record is set! Best score is "+score,
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("best_fifth", score);
                    editor.putString("best_fifth_time",
                            output_time.format(new Date()));
                    editor.apply();
                }
                break;
        }
        checking_btn.setVisibility(View.INVISIBLE);
        out_mine.setVisibility(View.INVISIBLE);
        title_mine.setVisibility(View.INVISIBLE);
        next_repeat_btn.setImageResource(R.drawable.play);
        last_game_won = true;
        smile_out.setImageResource(R.drawable.smile_win);
        next_repeat_btn.setVisibility(View.VISIBLE);
        smile_out.setVisibility(View.VISIBLE);
        for (Circle circle : circles) {
            if (circle.alive) circle.alive = false;
        }
        redraw_game();
    }
    public void set_level() {
        switch (level) {
            case 1:
                amount_of_circles = 12;
                amount_of_mines = 0;
                break;
            case 2:
                amount_of_circles = 12;
                amount_of_mines = 1;
                break;
            case 3:
                amount_of_circles = 24;
                amount_of_mines = 3;
                break;
            case 4:
                amount_of_circles = 60;
                amount_of_mines = 5;
                break;
            case 5:
                amount_of_circles = 60;
                amount_of_mines = 7;
                break;
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Random r = new Random();
        if (game_over_flag) return true;
        if (MotionEvent.ACTION_DOWN == event.getAction())
        {
            for (Circle circle:circles) {
                if ( ((int)event.getX() < (circle.x + radius)) &&
                        ((int)event.getX() > (circle.x - radius)) &&
                        ((int)event.getY() < (circle.y + radius)) &&
                        ((int)event.getY() > (circle.y - radius))) {
                    if (state_of_marking && circle.alive) {
                        if (!circle.marked) {
                            circle.marked = true;
                            score_of_mines--;
                            out_mine.setText( String.format(Locale.US, "%d", score_of_mines) );
                            if (circle.with_mine) {
                                amount_of_right_guesses ++;
                            } else {
                                amount_of_right_guesses --;
                            }
                            if ((!game_over_flag) && (checked_for_win())) {
                                game_over_flag = true;
                                game_win();
                            }
                        } else {
                            circle.marked = false;
                            score_of_mines++;
                            out_mine.setText( String.format(Locale.US, "%d", score_of_mines) );
                            if (circle.with_mine) {
                                amount_of_right_guesses --;
                            } else {
                                amount_of_right_guesses ++;
                            }
                            if ((!game_over_flag) && (checked_for_win())) {
                                game_over_flag = true;
                                game_win();
                            }
                            int t_row = (circle.y - margin_height - radius) / (2 * radius);
                            int t_col = (circle.x - margin_width - radius) / (2 * radius);
                            if (check_out(t_row, t_col)) {
                                if (!mines_set) {
                                    set_mines();
                                    mines_set = true;
                                }
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        redraw_game();
                                    }
                                }, 1000);
                            }
                        }
                        redraw_game();
                        state_of_marking = false;
                        checking_btn.setImageResource(R.drawable.bomb_check);
                        return true;
                    }
                    if (circle.marked) {
                        return true;
                    }
                    taken_circle = circle;
                    if (taken_circle.with_mine) {
                        game_over_flag = true;
                        game_over();
                        return true;
                    }
                    if (taken_circle.alive) {
                        prime_x = taken_circle.x;
                        prime_y = taken_circle.y;
                        prime_r = taken_circle.radius;
                        prime_row = (prime_y - margin_height - radius) / (2 * radius);
                        prime_col = (prime_x - margin_width - radius) / (2 * radius);
                        taken_circle.radius -= (taken_circle.radius/8);
                        taken_circle.x = (int) event.getX();
                        taken_circle.y = (int) event.getY();
                        redraw_game();
                    }
                    break;
                }
            }
        }

        if (MotionEvent.ACTION_UP == event.getAction())
        {
            if ((taken_circle != null) && (taken_circle.alive) ) {
                taken_circle.x = prime_x;
                taken_circle.y = prime_y;
                taken_circle.radius = prime_r;
                taken_circle = null;
                redraw_game();
            }
        }

        if (MotionEvent.ACTION_MOVE == event.getAction())
        {
            if ( (taken_circle != null) && (taken_circle.alive) ) {
                if (( (int) event.getX() > (margin_width + size_col * 2 * radius) ) ||
                    ( (int) event.getY() > (margin_height + size_row * 2 * radius) ) ||
                    ( (int) event.getY() < margin_height ) ||
                    ( (int) event.getX() < margin_width ) ) {
                    taken_circle.x = prime_x;
                    taken_circle.y = prime_y;
                    taken_circle.radius = prime_r;
                    taken_circle = null;
                    redraw_game();
                    return true;
                }
                taken_circle.x = (int) event.getX();
                taken_circle.y = (int) event.getY();
                redraw_game();
                if (((int) event.getX() > (prime_x + radius)) ||
                        ((int) event.getX() < (prime_x - radius)) ||
                        ((int) event.getY() > (prime_y + radius)) ||
                        ((int) event.getY() < (prime_y - radius))) {
                    for (Circle circle : circles) {
                        if (((int) event.getX() < (circle.x + radius)) &&
                                ((int) event.getX() > (circle.x - radius)) &&
                                ((int) event.getY() < (circle.y + radius)) &&
                                ((int) event.getY() > (circle.y - radius)) &&
                                (!circle.equals(taken_circle))) {
                            if (circle.marked) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                taken_circle = null;
                                redraw_game();
                                return true;
                            }
                            if (circle.with_mine) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                taken_circle = null;
                                game_over_flag = true;
                                game_over();
                                return true;
                            }
                            int new_index_for_taken = circles.indexOf(circle);
                            int old_index_for_taken = circles.indexOf(taken_circle);
                            if ( (circles.get(new_index_for_taken).with_mine) ||
                                    (circles.get(old_index_for_taken).with_mine)  ) {
                                game_over_flag = true;
                                game_over();
                            }
                            circles.set(new_index_for_taken, taken_circle); // Swapping circles
                            circles.set(old_index_for_taken, circle);
                            int tmp_mines = circle.mines_near;
                            circle.mines_near = taken_circle.mines_near;
                            taken_circle.mines_near = tmp_mines;
                            int tmp = circle.x;
                            circle.x = prime_x;
                            prime_x = tmp;
                            tmp = circle.y;
                            circle.y = prime_y;
                            prime_y = tmp;
                            boolean something_deleted = false;
                            if (check_out(prime_row, prime_col) ) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                something_deleted = true;
                                combo = true;
                            }
                            prime_row = (prime_y - margin_height - radius) / (2 * radius);
                            prime_col = (prime_x - margin_width - radius) / (2 * radius);
                            if (check_out(prime_row, prime_col) ) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                something_deleted = true;
                            } else {
                                combo = false;
                            }
                            if ((something_deleted) && (!mines_set)) {
                                set_mines();
                                mines_set = true;
                            }
                            redraw_game();
                            if (game_over_in_checking) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                game_over_in_checking = false;
                                game_over();
                            }
                            if (game_win_in_checking) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                game_win_in_checking = false;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        game_win();
                                    }
                                }, 900);
                            }
                            if (something_deleted) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        redraw_game();
                                    }
                                }, 1000);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
    public boolean checked_for_win() {

        for (int i = 0; i < circles.size(); i++) {
            if (amount_of_right_guesses != amount_of_mines) {
                return false;
            }
            if ((circles.get(i).alive) && (!circles.get(i).with_mine)) {
                for (int j = i+1; j < circles.size(); j++) {
                    if ((circles.get(j).color == circles.get(i).color) &&
                            (circles.get(j).alive) && (!circles.get(j).with_mine)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean check_out(int row, int col) {
        boolean has_to_be_deleted = false;
        int pos_for_remove;
        if ( (!circles.get(position[row][col]-1).alive ) ||(circles.get(position[row][col]-1).marked ) ) return false;
        if ((row != 0) && (circles.get(position[row-1][col]-1).alive) )  { // Up
            if (!circles.get(position[row - 1][col] - 1).marked) {
                if (circles.get(position[row - 1][col] - 1).color ==
                        circles.get(position[row][col] - 1).color) {
                    if (circles.get(position[row - 1][col] - 1).with_mine) {
                        game_over_flag = true;
                        game_over_in_checking = true;
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row - 1][col];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                    amount_of_alive--;
                    if (amount_of_alive <=
                            ((position.length > position[0].length ?
                                    position[0].length : position.length) + amount_of_mines)) {
                        if ((!game_over_flag) && (checked_for_win())) {
                            game_over_flag = true;
                            game_win_in_checking = true;
                        }
                    }
                }
            }
        }
        if ((row < (size_row - 1) ) && (circles.get(position[row+1][col] - 1).alive) )  { // Down
            if (!circles.get(position[row + 1][col] - 1).marked) {
                if (circles.get(position[row+1][col]-1).color ==
                        circles.get(position[row][col]-1).color) {
                    if (circles.get(position[row + 1][col] - 1).with_mine) {
                        game_over_flag = true;
                        game_over_in_checking = true;
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row + 1][col];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                    amount_of_alive--;
                    if (amount_of_alive <=
                            ((position.length > position[0].length ?
                                    position[0].length : position.length) + amount_of_mines)) {
                        if ((!game_over_flag) && (checked_for_win())) {
                            game_over_flag = true;
                            game_win_in_checking = true;
                        }
                    }
                }
            }
        }
        if ((col != 0) && (circles.get(position[row][col - 1] - 1).alive) )  { // left
            if (!circles.get(position[row][col - 1] - 1).marked) {
                if (circles.get(position[row][col-1]-1).color ==
                    circles.get(position[row][col]-1).color) {
                    if (circles.get(position[row][col - 1] - 1).with_mine) {
                        game_over_flag = true;
                        game_over_in_checking = true;
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row][col - 1];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                    amount_of_alive--;
                    if (amount_of_alive <=
                            ((position.length > position[0].length ?
                                    position[0].length : position.length) + amount_of_mines)) {
                        if ((!game_over_flag) && (checked_for_win())) {
                            game_over_flag = true;
                            game_win_in_checking = true;
                        }
                    }
                }
            }
        }
        if ((col < (size_col - 1) ) && (circles.get(position[row][col + 1] - 1).alive) )  { // right
            if (!circles.get(position[row][col + 1] - 1).marked) {
                if (circles.get(position[row][col + 1] - 1).color ==
                        circles.get(position[row][col] - 1).color) {
                    if (circles.get(position[row][col + 1] - 1).with_mine) {
                        game_over_flag = true;
                        game_over_in_checking = true;
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row][col + 1];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                    amount_of_alive--;
                    if (amount_of_alive <=
                            ((position.length > position[0].length ?
                                    position[0].length : position.length) + amount_of_mines)) {
                        if ((!game_over_flag) && (checked_for_win())) {
                            game_over_flag = true;
                            game_win_in_checking = true;
                        }
                    }
                }
            }
        }
        if (has_to_be_deleted) {
            circles.get(position[row][col]-1).alive = false;
            circles.get(position[row][col] - 1).animation = true;
            amount_of_alive--;
            if (amount_of_alive <=
                    ((position.length > position[0].length ?
                            position[0].length : position.length) + amount_of_mines)) {
                if ((!game_over_flag) && (checked_for_win())) {
                    game_over_flag = true;
                    game_win_in_checking = true;
                }
            }
            return true;
        }
        return false;
    }
    public boolean set_circles() {
        int circles_in_short_side, circles_in_long_side;
        int current_x, current_y, current_color = 0;
        int current_row, current_col;
        int iterations = 0;
        boolean checked = false;
        int [] colors;
        int[] amount_of_circles_for_color;
        int one_side;
        List<Integer> arrays_of_colors = new ArrayList<>(Arrays.asList(Color.rgb(81,118,151),
                Color.rgb(200,60,190),Color.rgb(255,55,55), Color.rgb(255, 128, 0),
                Color.rgb(250,243,12), Color.rgb(0,174,44) ));
        Random r = new Random();
        circles = new ArrayList<>();
        if (amount_of_circles == 12) {
            circles_in_short_side = 3;
            circles_in_long_side  = 4;
            colors = new int[3];
            amount_of_circles_for_color = new int[3];
            Arrays.fill(amount_of_circles_for_color, 4);
        }
        else {
            if (amount_of_circles == 24) {
                circles_in_short_side = 4;
                circles_in_long_side = 6;
                colors = new int[4];
                amount_of_circles_for_color = new int[4];
                Arrays.fill(amount_of_circles_for_color, 6);
            }
            else {
                circles_in_short_side = 6;
                circles_in_long_side  = 10;
                colors = new int[6];
                amount_of_circles_for_color = new int[6];
                Arrays.fill(amount_of_circles_for_color, 10);
            }
        }
        if (game_image.getWidth() > game_image.getHeight()) {
            size_col = circles_in_long_side;
            size_row = circles_in_short_side;
            one_side = game_image.getHeight()/ circles_in_short_side;
            if ((circles_in_long_side * one_side) > (game_image.getWidth()) ) {
                one_side = game_image.getWidth() / circles_in_long_side;
            }
            margin_width = (game_image.getWidth() - one_side*circles_in_long_side)/2;
            margin_height = (game_image.getHeight() - one_side*circles_in_short_side)/2;
        } else {
            size_row = circles_in_long_side;
            size_col = circles_in_short_side;
            one_side = game_image.getWidth() / circles_in_short_side;
            if ((circles_in_long_side * one_side) > (game_image.getHeight()) ) {
                one_side = game_image.getHeight() / circles_in_long_side;
            }
            margin_width = (game_image.getWidth() - one_side*circles_in_short_side)/2;
            margin_height = (game_image.getHeight() - one_side*circles_in_long_side)/2;
        }

        position = new int[size_row][size_col];
        radius = one_side / 2;
        current_x = radius + margin_width;
        current_y = radius + margin_height;
        for (int i = 0; i < colors.length; i++) {
            // Remove one color from the list of all possible colors and set cur color to that
            colors[i] = arrays_of_colors.remove(r.nextInt(arrays_of_colors.size()));
        }
        checked = false;
        current_col = 0;
        current_row = 0;
        for (int i=0; i<amount_of_circles; i++ ) {
            iterations = 0;
            if (i !=0) {
                current_y += radius*2;
                current_row ++;
            }
            if (current_row >= size_row) {
            //if ( (current_y + radius) >  game_image.getHeight()) {
                current_y = radius + margin_height;
                current_x += radius*2;
                current_col ++;
                current_row = 0;
            }
            do {
                iterations++;
                if (iterations == 5 ) return false;
                int color_ind;
                //if (current_col == 0) {
                if (i < (amount_of_circles/3) ) {
                    color_ind = r.nextInt(colors.length);
                    current_color = colors[color_ind];
                } else {
                    int tmp = 0, ind_for_color_ind = 0;
                    for (int ind = 0; ind < amount_of_circles_for_color.length; ind++) {
                        if (amount_of_circles_for_color[ind] > tmp) {
                            if ( ( (current_row == 0) ||                        //Check up
                                    (circles.get(position[current_row-1][current_col]-1).color !=
                                            colors[ind]) ) &&
                                    ( (current_col == 0) ||                        //Check down
                                            (circles.get(position[current_row][current_col-1]-1).color !=
                                                    colors[ind]) ) ) {
                                tmp = amount_of_circles_for_color[ind];
                                ind_for_color_ind = ind;
                            }
                        }
                    }
                    color_ind = ind_for_color_ind;
                    current_color = colors[color_ind];
                }
                if ( ( (current_row == 0) ||                        //Check up
                        (circles.get(position[current_row-1][current_col]-1).color !=
                                current_color) ) &&
                        ( (current_col == 0) ||                        //Check down
                        (circles.get(position[current_row][current_col-1]-1).color !=
                                current_color) ) ) {
                    amount_of_circles_for_color[color_ind]--;
                    checked = true;
                }
            } while(!checked);
            checked = false;
            circles.add( new Circle(current_x, current_y, radius, current_color, false)   );
            position[current_row][current_col] = circles.size();
        }
        game_over_flag = false;
        mines_set = false;
        amount_of_right_guesses = 0;
        bang = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bang),
                2*radius, 2*radius, false);
        bomb_pict = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bomb),
                2*radius, 2*radius, false);
        score_of_mines = amount_of_mines;
        amount_of_alive = amount_of_circles;
        out_mine.setText( String.format(Locale.US, "%d", score_of_mines) );
        score = 0;
        out_score.setText("0");
        redraw_game();
        return true;
    }
    public void set_mines() {
        Random r = new Random();
        for (int i = 0; i < amount_of_mines; i++) {
            Circle random_circle;
            do {
                random_circle = circles.get( r.nextInt(amount_of_circles) );
            } while ((random_circle.with_mine) || (!random_circle.alive));
            random_circle.with_mine = true;
            int row_with_mine = (random_circle.y - margin_height - radius) / (2*radius);
            int col_with_mine = (random_circle.x - margin_width - radius) / (2*radius);
            // Here comes checking all juxtapositions with the circle containing mine
            if (row_with_mine != 0) {
                circles.get(position[row_with_mine-1][col_with_mine]-1).mines_near++;
                if (col_with_mine < ( size_col - 1) ) {
                    circles.get(position[row_with_mine - 1][col_with_mine + 1] - 1).mines_near++;
                }
            }
            if (col_with_mine != 0) {
                circles.get(position[row_with_mine][col_with_mine - 1]-1).mines_near++;
                if (row_with_mine != 0) {
                    circles.get(position[row_with_mine - 1][col_with_mine - 1]-1).mines_near++;
                }
            }
            if (row_with_mine < (size_row - 1)) {
                circles.get(position[row_with_mine + 1][col_with_mine]-1).mines_near++;
                if (col_with_mine != 0) {
                    circles.get(position[row_with_mine + 1][col_with_mine - 1] - 1).mines_near++;
                }
            }
            if (col_with_mine < (size_col - 1)) {
                circles.get(position[row_with_mine][col_with_mine + 1]-1).mines_near++;
                if (row_with_mine < (size_row - 1)) {
                    circles.get(position[row_with_mine + 1][col_with_mine + 1]-1).mines_near++;
                }
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (game_over_flag) {
            SharedPreferences sharedPreferences = getSharedPreferences("levels", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("game_saved", 0);
            editor.apply();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("levels", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("game_saved", 1);
        editor.apply();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (game_over_flag) {
            return;
        }

        try {
            ByteBuffer tmp = ByteBuffer.allocate(4);
            byte[] result;
            FileOutputStream game_saved = openFileOutput("game_saving", MODE_PRIVATE);
            tmp.putInt(amount_of_circles);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            for (Circle circle : circles) {
                tmp.putInt(circle.color);
                result = tmp.array();
                tmp.flip();
                game_saved.write(result);
                tmp.putInt(circle.mines_near);
                result = tmp.array();
                tmp.flip();
                game_saved.write(result);
                tmp.putInt(circle.radius);
                result = tmp.array();
                tmp.flip();
                game_saved.write(result);
                tmp.putInt(circle.x);
                result = tmp.array();
                tmp.flip();
                game_saved.write(result);
                tmp.putInt(circle.y);
                result = tmp.array();
                tmp.flip();
                game_saved.write(result);
                game_saved.write((byte)(circle.alive? 1: 0 ));
                game_saved.write((byte)(circle.marked? 1: 0 ));
                game_saved.write((byte)(circle.with_mine? 1: 0 ));
                game_saved.write((byte)(circle.animation? 1: 0 ));
            }
            tmp.putInt(position.length);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(position[0].length);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            for (int i = 0; i < position.length; i++) {
                for (int j = 0; j < position[0].length; j++) {
                    tmp.putInt(position[i][j]);
                    result = tmp.array();
                    tmp.flip();
                    game_saved.write(result);
                }
            }
            tmp.putInt(orientation);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(amount_of_right_guesses);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(amount_of_mines);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(score);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(score_of_mines);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(level);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(amount_of_alive);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            game_saved.write((byte)(game_over_flag? 1: 0 ));
            game_saved.write((byte)(state_of_marking? 1: 0 ));
            game_saved.write((byte)(mines_set? 1: 0 ));
            game_saved.write((byte)(mines_set? 1: 0 ));
            tmp.putInt(margin_width);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            tmp.putInt(margin_height);
            result = tmp.array();
            tmp.flip();
            game_saved.write(result);
            game_saved.close();
        }
        catch (Exception e) {

        }
    }
    public void load_game() {
        try {
            byte[] in = new byte[4];
            int result2;
            ByteBuffer result;
            FileInputStream game_saved = openFileInput("game_saving");
            //for (int i = 0; i<in.length; i++)
            game_saved.read(in);
            amount_of_circles = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            circles = new ArrayList<>();
            for (int i = 0; i < amount_of_circles; i++) {
                circles.add(new Circle(0,0,0,0,false));
                Circle circle = circles.get(circles.size()-1);
                game_saved.read(in);
                circle.color = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
                game_saved.read(in);
                circle.mines_near = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
                game_saved.read(in);
                circle.radius = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
                game_saved.read(in);
                circle.x = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
                game_saved.read(in);
                circle.y = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
                game_saved.read(in);
                circle.alive = (in[0] == 1);
                circle.marked = (in[1] == 1);
                circle.with_mine =(in[2] == 1);
                circle.animation = (in[3] == 1);
            }
            radius = circles.get(0).radius;

            game_saved.read(in);
            int first_index = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            int second_index = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            position = new int[first_index][second_index];
            size_row = first_index;
            size_col = second_index;
            for (int i = 0; i < position.length; i++) {
                for (int j = 0; j < position[0].length; j++) {
                    game_saved.read(in);
                    position[i][j] = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                            ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
                }
            }
            game_saved.read(in);
            orientation = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            amount_of_right_guesses = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                            ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            amount_of_mines = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            score = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            score_of_mines = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            level = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            amount_of_alive = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            game_over_flag = (in[0] == 1);
            state_of_marking = (in[1] == 1);
            mines_set =(in[2] == 1);

            game_saved.read(in);
            margin_width = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            game_saved.read(in);
            margin_height = ((in[0]<< 24)&0xFF000000)|((in[1] << 16)&0x00FF0000)|
                    ((in[2] << 8)&0x0000FF00)|(in[3]&0x000000FF);
            my_paint = new Paint();
            my_paint.setTextAlign(Paint.Align.CENTER);
            //game_save_stream.close();
            game_saved.close();
            //redraw_game();


            /*game_saved.write(position.length);
            game_saved.write(position[0].length);
            for (int i = 0; i < position.length; i++)
                for (int j = 0; j < position[0].length; j++)
                    game_saved.write(position[i][j]);*/
            /*
            savedInstanceState.putParcelableArrayList("circles", circles);
            savedInstanceState.putSerializable("position", position);
            savedInstanceState.putInt("orientation", orientation);
            savedInstanceState.putInt("amount_of_right_guesses", amount_of_right_guesses);
            savedInstanceState.putInt("amount_of_mines", amount_of_mines);
            savedInstanceState.putBoolean("game_over_flag", game_over_flag);
            savedInstanceState.putBoolean("state_of_marking", state_of_marking);
            savedInstanceState.putInt("score", score);
            savedInstanceState.putInt("score_of_mines", score_of_mines);
            savedInstanceState.putInt("amount_of_circles", amount_of_circles);
            savedInstanceState.putInt("level", level);
            savedInstanceState.putInt("amount_of_alive", amount_of_alive);
            savedInstanceState.putBoolean("mines_set", mines_set);
            */
        }
        catch (Exception e) {

        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("circles", circles);
        savedInstanceState.putSerializable("position", position);
        savedInstanceState.putInt("orientation", orientation);
        savedInstanceState.putInt("amount_of_right_guesses", amount_of_right_guesses);
        savedInstanceState.putInt("amount_of_mines", amount_of_mines);
        savedInstanceState.putBoolean("game_over_flag", game_over_flag);
        savedInstanceState.putBoolean("state_of_marking", state_of_marking);
        savedInstanceState.putInt("score", score);
        savedInstanceState.putInt("score_of_mines", score_of_mines);
        savedInstanceState.putInt("amount_of_circles", amount_of_circles);
        savedInstanceState.putInt("level", level);
        savedInstanceState.putInt("amount_of_alive", amount_of_alive);
        savedInstanceState.putBoolean("mines_set", mines_set);
        savedInstanceState.putBoolean("last_game_won", last_game_won);
    }
    public void redraw_game() {
        //HashMap<Integer, Integer> colors = new HashMap<>();
        SparseIntArray colors_map = new SparseIntArray();
        colors_map.put(Color.rgb(81,118,151),Color.rgb(38,55,70));  // Blue
        colors_map.put(Color.rgb(200,60,190), Color.rgb(77,21,72)); // Purple
        colors_map.put(Color.rgb(255,55,55), Color.rgb(89,0,0));    // Red
        colors_map.put(Color.rgb(255, 128, 0), Color.rgb(108,54,0));// Brown
        colors_map.put(Color.rgb(250,243,12), Color.rgb(89,87,2));  // Yellow
        colors_map.put(Color.rgb(0,174,44), Color.rgb(0,45,12));    // Green
        /*if (game_over_flag) {
            //game_image.setImageBitmap(game_almost_over_img);
            return;
        }*/
        int min_x = 1000000, min_y = 1000000, max_x = - 1, max_y = - 1, count_animated = 0;
        Bitmap out = Bitmap.createBitmap(game_image.getWidth(), game_image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        for (Circle circle:circles) {
            if (circle.alive) {
                //my_paint.setColor(circle.color);
                RadialGradient rd = new RadialGradient(circle.x, circle.y,
                        circle.radius, circle.color, colors_map.get(circle.color),
                                                                Shader.TileMode.CLAMP);
                my_paint.setDither(true);
                my_paint.setShader(rd);
                canvas.drawCircle(circle.x, circle.y, circle.radius, my_paint);
                my_paint.setDither(false);
                my_paint.setShader(null);
                if (circle.marked) {
                    my_paint.setColor(Color.BLACK);
                    my_paint.setStrokeWidth(circle.radius / 10);
                    //canvas.drawLine(circle.x - circle.radius, circle.y - circle.radius,
                    //        circle.x + circle.radius, circle.y + circle.radius, my_paint);
                    canvas.drawLine(circle.x + circle.radius * (float)Math.cos(Math.PI/4),
                            circle.y + circle.radius * (float)Math.sin(Math.PI/4),
                            circle.x + circle.radius * (float)Math.cos(5*Math.PI/4),
                            circle.y + circle.radius * (float)Math.sin(5*Math.PI/4), my_paint);
                    canvas.drawLine(circle.x + circle.radius * (float)Math.cos(7*Math.PI/4),
                            circle.y + circle.radius * (float)Math.sin(7*Math.PI/4),
                            circle.x + circle.radius * (float)Math.cos(3*Math.PI/4),
                            circle.y + circle.radius * (float)Math.sin(3*Math.PI/4), my_paint);
                }
            } else {
                if (circle.animation) {
                    canvas.drawBitmap(bang, circle.x - radius, circle.y - radius,  my_paint);
                    if (min_x > circle.x) {
                        min_x = circle.x;
                    }
                    if (max_x < circle.x) {
                        max_x = circle.x;
                    }
                    if (min_y > circle.y) {
                        min_y = circle.y;
                    }
                    if (max_y < circle.y) {
                        max_y = circle.y;
                    }
                    count_animated++;
                    circle.animation = false;
                } else {
                    if (circle.with_mine) {
                       canvas.drawBitmap(bomb_pict, circle.x - radius, circle.y - radius, my_paint);
                    } else {
                        my_paint.setColor(Color.BLACK);
                        my_paint.setTextSize(radius);
                        canvas.drawText(Integer.toString(circle.mines_near), circle.x, circle.y, my_paint);
                    }
                }
            }
        }
        if (count_animated != 0) {
            my_paint.setColor(Color.BLACK);
            my_paint.setTextAlign(Paint.Align.CENTER);
            my_paint.setTextSize(radius/2);
            String text = "";
            if (combo) {
                canvas.drawText("Combo!", min_x + (max_x - min_x)/2,
                        min_y + (max_y - min_y)/2 - radius/2, my_paint);
                combo = false;
                score += count_animated*20;
                text += "+"+Integer.toString(count_animated*20);
            } else {
                score += count_animated*10;
                text += "+"+Integer.toString(count_animated*10);
            }
            canvas.drawText(text, min_x + (max_x - min_x)/2,
                                min_y + (max_y - min_y)/2, my_paint);
            out_score.setText(Integer.toString(score));
        }
        game_image.setImageBitmap(out);
    }
    public void set_new_level() {
        if (level < 6) {
            level++;
            set_level();
            while (!set_circles());
        }
        if (level == 6) {
            DialogFragment choose_dialog = new CustomLevelDialogFragment();
            choose_dialog.setCancelable(false);
            choose_dialog.show(getSupportFragmentManager(), "GameActivity");
        }
    }

    @Override
    public void onLevelParamsChosen(int fieldSize, int amountOfMines) {
        amount_of_mines = amountOfMines;
        switch (fieldSize) {
            case 1:
                amount_of_circles = 12;
                break;
            case 2:
                amount_of_circles = 24;
                break;
            case 3:
                amount_of_circles = 60;
                break;
        }
        while (!set_circles());
    }

    @Override
    public void onDismiss() {
        onBackPressed();
    }

    private class Circle implements Parcelable {
        public int x;
        public int y;
        public int color;
        public int radius;
        public int mines_near = 0;
        public boolean animation = false;
        public boolean alive = true;
        public boolean with_mine;
        public boolean marked = false;
        public Circle(int init_x, int init_y, int init_radius,int init_color, boolean init_mine) {
            x = init_x;
            y = init_y;
            radius = init_radius;
            color = init_color;
            with_mine = init_mine;
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flag) {
            dest.writeInt(x);
            dest.writeInt(y);
            dest.writeInt(color);
            dest.writeInt(radius);
            dest.writeInt(mines_near);
            boolean[] array_of_boolean = {animation, alive, with_mine, marked};
            dest.writeBooleanArray(array_of_boolean);
        }
        public final Parcelable.Creator CREATOR
                = new Parcelable.Creator() {
            public Circle createFromParcel(Parcel in) {
                return new Circle(in);
            }

            public Circle[] newArray(int size) {
                return new Circle[size];
            }
        };

        private Circle(Parcel in) {
            x = in.readInt();
            y = in.readInt();
            color = in.readInt();
            radius = in.readInt();
            mines_near = in.readInt();
            boolean[] array_of_boolean = new boolean[4];
            in.readBooleanArray(array_of_boolean);
            animation = array_of_boolean[0];
            alive = array_of_boolean[1];
            with_mine = array_of_boolean[2];
            marked = array_of_boolean[3];
        }

    }


}
