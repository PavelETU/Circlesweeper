package com.wordpress.lonelytripblog.circlesminesweeper.data;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CellsGeneratorImpl implements CellsGenerator {

    private static final int INVALID_INDEX = -1;
    private int amountOnSmallerSide;
    private int amountOnBiggerSide;
    private int smallerSideLength;
    private int biggerSideLength;
    private int lengthOfCellSide;
    private int shiftForSmallerSide;
    private int shiftForBiggerSide;
    private int bombsAmount;
    private GameCell[][] gameCells;
    private int[][] colorsForCircles;
    private int radiusForCircles;
    private int[] actualColors;
    private Random random;
    private List<Integer> amountOfCirclesForColor;


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
        generateColors();
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                Circle circle = new Circle(getXForCol(col), getYForRow(row), radiusForCircles,
                        colorsForCircles[row][col]);
                gameCells[row][col] = new GameCell(circle, false);
            }
        }
    }

    private void generateColors() {
        colorsForCircles = new int[amountOnSmallerSide][amountOnBiggerSide];
        final int amountOfDifferentColorsThatWillBeUsed = amountOnSmallerSide;
        final int amountOfCirclesWithSameColor = amountOnBiggerSide;
        ArrayList<Integer> arrayOfAllColors = new ArrayList<>(Arrays.asList(R.drawable.blue_ball, R.drawable.green_ball,
                R.drawable.yellow_ball, R.drawable.orange_ball, R.drawable.red_ball,
                R.drawable.purple_ball
        ));
        actualColors = new int[amountOfDifferentColorsThatWillBeUsed];
        for (int i = 0; i < actualColors.length; i++) {
            actualColors[i] = arrayOfAllColors.remove(getRandom().nextInt(arrayOfAllColors.size()));
        }
        boolean startCreateCircleFromScratch = true;
        while (startCreateCircleFromScratch) {
            amountOfCirclesForColor = new ArrayList<>(amountOfDifferentColorsThatWillBeUsed);
            for (int i = 0; i < amountOfDifferentColorsThatWillBeUsed; i++) {
                amountOfCirclesForColor.add(amountOfCirclesWithSameColor);
            }
            startCreateCircleFromScratch = false;
            for (int row = 0; row < gameCells.length && !startCreateCircleFromScratch; row++) {
                for (int col = 0; col < gameCells[0].length && !startCreateCircleFromScratch; col++) {
                    int colorIndex;
                    if (firstCell(row, col)) {
                        colorIndex = generateRandomColorIndex();
                    } else if (cellInFirstRow(row)) {
                        colorIndex = generateRandomColorIndexTakenLeftCellIntoConsideration(row, col);
                    } else {
                        colorIndex = getIndexWithBiggestAmountOfDedicatedCellsTakenIntoConsiderationTopAndLeftCells(row, col);
                        if (colorIndex == INVALID_INDEX) {
                            startCreateCircleFromScratch = true;
                        }
                    }
                    if (!startCreateCircleFromScratch) {
                        amountOfCirclesForColor.set(colorIndex, amountOfCirclesForColor.get(colorIndex) - 1);
                        colorsForCircles[row][col] = actualColors[colorIndex];
                    }
                }
            }
        }
    }

    private boolean firstCell(final int row, final int col) {
        return row == 0 && col == 0;
    }

    private boolean cellInFirstRow(final int row) {
        return row == 0;
    }

    private int generateRandomColorIndex() {
        return getRandom().nextInt(actualColors.length);
    }

    private int generateRandomColorIndexTakenLeftCellIntoConsideration(final int row, final int col) {
        int indexToTry = random.nextInt(actualColors.length);
        if (actualColors[indexToTry] == colorsForCircles[row][col - 1]) {
            if (indexToTry + 1 == actualColors.length) {
                indexToTry = 0;
            } else {
                indexToTry = indexToTry + 1;
            }
        }
        return indexToTry;
    }

    private int getIndexWithBiggestAmountOfDedicatedCellsTakenIntoConsiderationTopAndLeftCells(
            final int row, final int col) {
        int indexToTry = INVALID_INDEX;
        int maxAmount = 0;
        for (int i = 0; i < amountOfCirclesForColor.size(); i++) {
            if (amountOfCirclesForColor.get(i) > maxAmount
                    && isColorDifferentFromTopAndLeft(row, col, actualColors[i])) {
                maxAmount = amountOfCirclesForColor.get(i);
                indexToTry = i;
            }
        }
        return indexToTry;
    }

    private Random getRandom() {
        if (random == null) random = new Random();
        return random;
    }

    private boolean isColorDifferentFromTopAndLeft(int row, int col, int color) {
        if (col != 0 && colorsForCircles[row][col - 1] == color) {
            return false;
        }
        if (row != 0 && colorsForCircles[row - 1][col] == color) {
            return false;
        }
        return true;
    }

    private int getXForCol(final int col) {
        return radiusForCircles + lengthOfCellSide * col + shiftForSmallerSide;
    }

    private int getYForRow(final int row) {
        return radiusForCircles + lengthOfCellSide * row + shiftForBiggerSide;
    }


}
