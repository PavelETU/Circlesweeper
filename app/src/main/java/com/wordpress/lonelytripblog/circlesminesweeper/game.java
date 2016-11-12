package com.wordpress.lonelytripblog.circlesminesweeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class game extends AppCompatActivity implements View.OnTouchListener {
    private ImageView game_image;
    private TextView out_score;
    private TextView out_mine;
    private Paint my_paint;
    private ImageButton checking_btn;
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
    private Bitmap game_almost_over_img;
    private int score_of_mines;
    private int score = 0;
    private int orientation;

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
    protected void onCreate(Bundle savedInstanceState) {
        //myView game_view;
        super.onCreate(savedInstanceState);
        //game_v = new myView(this);
        setContentView(R.layout.game_view);
        //game_view.set_circles(10);
        //View decor = getWindow().getDecorView();
        //decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
        //        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
        //        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        game_image = (ImageView) findViewById(R.id.game_image);
        out_mine = (TextView) findViewById(R.id.mine_out);
        out_score = (TextView) findViewById(R.id.score_out);
        checking_btn = (ImageButton)findViewById(R.id.checking_btn);
        checking_btn.setBackground(null);
        game_image.setOnTouchListener(this);
        my_paint = new Paint();
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
            game_image.post(new Runnable() {
                @Override
                public void run() {
                    set_circles(24);
                }
            });
        }
        orientation = getResources().getConfiguration().orientation;
        out_mine.setText( String.format(Locale.US, "%d", score_of_mines) );
        out_score.setText( String.format(Locale.US, "%d", score) );
        //game_image.Draw
        //set_circles(24);
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
            if ( (current_y + radius) >  game_image.getHeight()) {
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
        //game_over_flag = false;
        bang = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bang),
                2*radius, 2*radius, false);
        game_almost_over_img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.boom),
                game_image.getWidth(), game_image.getHeight(), false);
        redraw_game();
    }
    public void game_over() {
        DialogFragment newDialog = new my_dialog();
        newDialog.setCancelable(false);
        newDialog.show(getFragmentManager(), "game_over");
        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Game over, dude!");
        alertDialog.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                set_circles(24);
            }
        });
        alertDialog.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });
        AlertDialog AD = alertDialog.create();
        AD.show();*/
    }
    public void game_win() {
        DialogFragment newDialog = new my_dialog();
        newDialog.setCancelable(false);
        newDialog.show(getFragmentManager(), "game_win");
        /*game_image.setImageResource(R.drawable.game_win);
        new_game_btn.setVisibility(View.VISIBLE);
        exit_btn.setVisibility(View.VISIBLE);
        game_over_flag = true;*/
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
                            if (amount_of_right_guesses == amount_of_mines) {
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
                            if (amount_of_right_guesses == amount_of_mines) {
                                game_win();
                            }
                            int t_row = (circle.y - margin_height - radius) / (2 * radius);
                            int t_col = (circle.x - margin_width - radius) / (2 * radius);
                            if (check_out(t_row, t_col)) {
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
                        //redraw_game();
                        game_over();
                        /*Handler handler_end = new Handler();
                        handler_end.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                game_over();
                            }
                        }, 1000);*/
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
                            int new_index_for_taken = circles.indexOf(circle);
                            int old_index_for_taken = circles.indexOf(taken_circle);
                            if ( (circles.get(new_index_for_taken).with_mine) ||
                                    (circles.get(old_index_for_taken).with_mine)  ) {
                                game_over_flag = true;
                                game_over();
                                /*redraw_game();
                                Handler handler_end = new Handler();
                                handler_end.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        game_over();
                                    }
                                }, 1000);*/
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
                            //position[prime_row][prime_col] = new_index_for_taken+1;
                            boolean something_deleted = false;
                            if (check_out(prime_row, prime_col) ) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                something_deleted = true;
                            }
                            prime_row = (prime_y - margin_height - radius) / (2 * radius);
                            prime_col = (prime_x - margin_width - radius) / (2 * radius);
                            //position[prime_row][prime_col] = old_index_for_taken+1;
                            if (check_out(prime_row, prime_col) ) {
                                taken_circle.x = prime_x;
                                taken_circle.y = prime_y;
                                taken_circle.radius = prime_r;
                                something_deleted = true;
                            }
                            redraw_game();
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
                        game_over();
                        /*redraw_game();
                        Handler handler_end = new Handler();
                        handler_end.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                game_over();
                            }
                        }, 1000);*/
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row - 1][col];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                }
            }
        }
        if ((row < (size_row - 1) ) && (circles.get(position[row+1][col] - 1).alive) )  { // Down
            if (!circles.get(position[row + 1][col] - 1).marked) {
                if (circles.get(position[row+1][col]-1).color ==
                        circles.get(position[row][col]-1).color) {
                    if (circles.get(position[row + 1][col] - 1).with_mine) {
                        game_over_flag = true;
                        //redraw_game();
                        game_over();
                        /*Handler handler_end = new Handler();
                        handler_end.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                game_over();
                            }
                        }, 1000);*/
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row + 1][col];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                }
            }
        }
        if ((col != 0) && (circles.get(position[row][col - 1] - 1).alive) )  { // left
            if (!circles.get(position[row][col - 1] - 1).marked) {
                if (circles.get(position[row][col-1]-1).color ==
                    circles.get(position[row][col]-1).color) {
                    if (circles.get(position[row][col - 1] - 1).with_mine) {
                        game_over_flag = true;
                        game_over();
                        /*redraw_game();
                        Handler handler_end = new Handler();
                        handler_end.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                game_over();
                            }
                        }, 1000);*/
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row][col - 1];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                }
            }
        }
        if ((col < (size_col - 1) ) && (circles.get(position[row][col + 1] - 1).alive) )  { // right
            if (!circles.get(position[row][col + 1] - 1).marked) {
                if (circles.get(position[row][col + 1] - 1).color ==
                        circles.get(position[row][col] - 1).color) {
                    if (circles.get(position[row][col + 1] - 1).with_mine) {
                        game_over_flag = true;
                        game_over();
                        /*redraw_game();
                        Handler handler_end = new Handler();
                        handler_end.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                game_over();
                            }
                        }, 1000);*/
                        return false;
                    }
                    has_to_be_deleted = true;
                    pos_for_remove = position[row][col + 1];
                    circles.get(pos_for_remove - 1).alive = false;
                    circles.get(pos_for_remove - 1).animation = true;
                }
            }
        }
        if (has_to_be_deleted) {
            circles.get(position[row][col]-1).alive = false;
            circles.get(position[row][col] - 1).animation = true;
            //circles.get(position[row][col]-1).x = prime_x;
            //circles.get(position[row][col]-1).y = prime_y;
            return true;
        }
        return false;
    }
    public void set_circles(int amount_of_circles) {
        int circles_in_short_side, circles_in_long_side;
        int current_x, current_y, current_color = 0;
        int current_row, current_col;
        boolean checked = false;
        int [] colors = new int[6];
        int one_side;
        Random r = new Random();
        circles = new ArrayList<>();
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

        position = new int[size_row][size_col];
        radius = one_side / 2;
        current_x = radius + margin_width;
        current_y = radius + margin_height;
        checked = false;

        for (int i = 0; i < colors.length; i++) {
            do {
                colors[i] = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
                checked = true;
                for (int j = (i - 1); j >= 0; j --) {
                    if (colors[j] == colors[i]) {
                        checked = false;
                    }
                }
            } while (!checked);
        }
        checked = false;
        current_col = 0;
        current_row = 0;
        for (int i=0; i<amount_of_circles; i++ ) {
            if (i !=0) {
                current_y += radius*2;
                current_row ++;
            }
            if ( (current_y + radius) >  game_image.getHeight()) {
                current_y = radius + margin_height;
                current_x += radius*2;
                current_col ++;
                current_row = 0;
            }
            do {
                current_color = colors[r.nextInt(colors.length)];
                if ( ( (current_row == 0) ||                        //Check up
                        (circles.get(position[current_row-1][current_col]-1).color !=
                                current_color) ) &&
                        ( (current_col == 0) ||                        //Check down
                        (circles.get(position[current_row][current_col-1]-1).color !=
                                current_color) ) ) {
                    checked = true;
                }
            } while(!checked);
            checked = false;
            circles.add( new Circle(current_x, current_y, radius, current_color, false)   );
            position[current_row][current_col] = circles.size();
        }
        for (int i = 0; i < amount_of_mines; i++) {
            Circle random_circle;
            do {
                random_circle = circles.get( r.nextInt(amount_of_circles) );
            } while (random_circle.with_mine);
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
        game_over_flag = false;
        amount_of_right_guesses = 0;
        bang = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bang),
                2*radius, 2*radius, false);
        game_almost_over_img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.boom),
                game_image.getWidth(), game_image.getHeight(), false);
        score_of_mines = amount_of_mines;
        out_mine.setText( String.format(Locale.US, "%d", score_of_mines) );
        redraw_game();
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
    }
    public void redraw_game() {
        /*if (game_over_flag) {
            //game_image.setImageBitmap(game_almost_over_img);
            return;
        }*/
        Bitmap out = Bitmap.createBitmap(game_image.getWidth(), game_image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        for (Circle circle:circles) {
            if (circle.alive) {
                my_paint.setColor(circle.color);
                canvas.drawCircle(circle.x, circle.y, circle.radius, my_paint);
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
                    circle.animation = false;
                } else {
                    my_paint.setColor(Color.BLACK);
                    my_paint.setTextSize(radius);
                    canvas.drawText(Integer.toString(circle.mines_near), circle.x, circle.y, my_paint);
                }
            }
        }
        game_image.setImageBitmap(out);
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
