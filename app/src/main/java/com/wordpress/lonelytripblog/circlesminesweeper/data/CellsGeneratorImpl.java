package com.wordpress.lonelytripblog.circlesminesweeper.data;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CellsGeneratorImpl implements CellsGenerator {

    private int amountOnSmallerSide;
    private int amountOnBiggerSide;
    private int smallerSideLength;
    private int biggerSideLength;
    private int lengthOfCellSide;
    private int shiftForSmallerSide;
    private int shiftForBiggerSide;
    private int bombsAmount;
    private GameCell[][] gameCells;
    private int radiusForCircles;


    @Override
    public GameCell[][] generateCellsForField3X4(int smallerSide, int biggerSide, int bombsAmount) {
        return saveParametersAndGenerateCells(smallerSide, biggerSide, bombsAmount, 3, 4);
    }

    @Override
    public GameCell[][] generateCellsForField4X6(int smallerSide, int biggerSide, int bombsAmount) {
        return saveParametersAndGenerateCells(smallerSide, biggerSide, bombsAmount, 4, 6);
    }

    @Override
    public GameCell[][] generateCellsForField6X10(int smallerSide, int biggerSide, int bombsAmount) {
        return saveParametersAndGenerateCells(smallerSide, biggerSide, bombsAmount, 6, 10);
    }

    private GameCell[][] saveParametersAndGenerateCells(int smallerSide, int biggerSide, int bombsAmount,
                                                        int amountOnSmallerSide, int amountOnBiggerSide) {
        smallerSideLength = smallerSide;
        biggerSideLength = biggerSide;
        this.bombsAmount = bombsAmount;
        this.amountOnSmallerSide = amountOnSmallerSide;
        this.amountOnBiggerSide = amountOnBiggerSide;
        return generateCells();
    }

    private GameCell[][] generateCells() {
        gameCells = new GameCell[amountOnSmallerSide][amountOnBiggerSide];
        radiusForCircles = lengthOfCellSide / 2;
        calculateLengthForCellSide();
        calculateShiftForCells();
        populateGameCells();
        return gameCells;
    }

    private void calculateLengthForCellSide() {
        lengthOfCellSide = Math.min(smallerSideLength / amountOnSmallerSide,
                biggerSideLength / amountOnBiggerSide);
    }

    private void calculateShiftForCells() {
        shiftForSmallerSide = (smallerSideLength - lengthOfCellSide * amountOnSmallerSide) / 2;
        shiftForBiggerSide = (biggerSideLength - lengthOfCellSide * amountOnBiggerSide) / 2;
    }

    private void populateGameCells() {
        legacyAlgorithm();
    }

    private void legacyAlgorithm() {
        int amountOfDifferentColorsThatWillBeUsed = amountOnSmallerSide;
        int amountOfCirclesWithSameColor = amountOnBiggerSide;
        ArrayList<Integer> arrayOfAllColors = new ArrayList<>(Arrays.asList(R.drawable.blue_ball, R.drawable.green_ball,
                R.drawable.yellow_ball, R.drawable.orange_ball, R.drawable.red_ball,
                R.drawable.purple_ball
        ));
        Random random = new Random();
        List<Integer> amountOfCirclesForColor = new ArrayList<>(amountOfDifferentColorsThatWillBeUsed);
        for (int i = 0; i < amountOfDifferentColorsThatWillBeUsed; i++) {
            amountOfCirclesForColor.add(amountOfCirclesWithSameColor);
        }
        int[] actualColors = new int[amountOfDifferentColorsThatWillBeUsed];
        for (int i = 0; i < actualColors.length; i++) {
            actualColors[i] = arrayOfAllColors.remove(random.nextInt(arrayOfAllColors.size()));
        }
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                boolean checked = false;
                int colorToTake = 0;
                while (!checked) {
                    int colorIndex;
                    // For first third circles pick up colors pretty much on random
                    if (row * col < (amountOnSmallerSide * amountOnBiggerSide / 3)) {
                        colorIndex = random.nextInt(actualColors.length);
                        colorToTake = actualColors[colorIndex];
                    } else {
                        // after that choose max from the left
                        amountOfCirclesForColor.indexOf(Collections.max(amountOfCirclesForColor));
                        colorIndex = random.nextInt(actualColors.length);
                        colorToTake = actualColors[colorIndex];
                    }
                    if ((row == 0 || (gameCells[row - 1][col].getCircle().getColor() != colorToTake))
                            && (col == 0 || (gameCells[row][col - 1].getCircle().getColor() != colorToTake))) {
                        amountOfCirclesForColor.set(colorIndex, amountOfCirclesForColor.get(colorIndex) - 1);
                        checked = true;
                    }
                }
                Circle circle = new Circle(getXForCol(col), getYForRow(row), radiusForCircles, colorToTake);
                gameCells[row][col] = new GameCell(circle, false);
            }
        }
    }

    private int getXForCol(final int col) {
        return radiusForCircles + lengthOfCellSide * col + shiftForSmallerSide;
    }

    private int getYForRow(final int row) {
        return radiusForCircles + lengthOfCellSide * row + shiftForBiggerSide;
    }


}
