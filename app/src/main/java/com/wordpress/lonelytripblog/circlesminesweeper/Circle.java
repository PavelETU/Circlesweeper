package com.wordpress.lonelytripblog.circlesminesweeper;

import android.os.Parcel;
import android.os.Parcelable;

public class Circle implements Parcelable {
    // TODO change to private as soon as the mess in the GameActivity gets cleaned up
    public int x;
    public int y;
    public int color;
    public int radius;
    public int minesNear = 0;
    public boolean animated = false;
    public boolean alive = true;
    public boolean withMine;
    public boolean marked = false;

    public Circle(int initX, int initY, int initRadius,int initColor, boolean initWithMine) {
        x = initX;
        y = initY;
        radius = initRadius;
        color = initColor;
        withMine = initWithMine;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMinesNear() {
        return minesNear;
    }

    public void setMinesNear(int minesNear) {
        this.minesNear = minesNear;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isWithMine() {
        return withMine;
    }

    public void setWithMine(boolean withMine) {
        this.withMine = withMine;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
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
        dest.writeInt(minesNear);
        boolean[] array_of_boolean = {animated, alive, withMine, marked};
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
        minesNear = in.readInt();
        boolean[] array_of_boolean = new boolean[4];
        in.readBooleanArray(array_of_boolean);
        animated = array_of_boolean[0];
        alive = array_of_boolean[1];
        withMine = array_of_boolean[2];
        marked = array_of_boolean[3];
    }

}