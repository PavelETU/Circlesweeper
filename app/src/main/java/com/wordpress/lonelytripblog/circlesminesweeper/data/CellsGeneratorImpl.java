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
    private int cellSideLength;
    private int shiftForSmallerSide;
    private int shiftForBiggerSide;
    private int bombsAmount;
    private GameCell[][] gameCells;
    private int[][] colorsForCircles;
    private int radiusForCircles;
    private int[] actualColors;
    private Random random;
    private List<Integer> amountOfCellsWithSameColorForIndex;
    private boolean invertCells;
    private boolean populateWithEmptyCells;

    @Override
    public GameCell[][] regenerateCellsForNewSize(GameCell[][] gameCells, int newWidth, int newHeight) {
        setSizeLenghtsAndInvertVariables(newWidth, newHeight);
        amountOnSmallerSide = gameCells.length;
        amountOnBiggerSide = gameCells[0].length;

        calculateLengthForCellSideAndRadius();
        calculateShiftForCells();

        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                gameCells[row][col].setNewCenterPositionAndRadius(getXForCol(row, col),
                        getYForRow(row, col), radiusForCircles);
            }
        }

        return gameCells;
    }

    @Override
    public GameCell[][] generateCellsForField3X4(int width, int height) {
        populateWithEmptyCells = false;
        return saveParametersAndGenerateCells(width, height, 3, 4);
    }

    @Override
    public GameCell[][] generateCellsForField4X6(int width, int height) {
        populateWithEmptyCells = false;
        return saveParametersAndGenerateCells(width, height, 4, 6);
    }

    @Override
    public GameCell[][] generateCellsForField6X10(int width, int height) {
        populateWithEmptyCells = false;
        return saveParametersAndGenerateCells(width, height, 6, 10);
    }

    @Override
    public GameCell[][] generateEmptyCells(int width, int height, int amountOnSmallerSide, int amountOnBiggerSide) {
        populateWithEmptyCells = true;
        return saveParametersAndGenerateCells(width, height, amountOnSmallerSide, amountOnBiggerSide);
    }

    @Override
    public GameCell[][] generateMines(GameCell[][] gameCells, int mines) {
        bombsAmount = mines;
        amountOnSmallerSide = gameCells.length;
        amountOnBiggerSide = gameCells[0].length;
        throwExceptionIfMinesOfLimit();
        generateMinesForCells(gameCells);
        this.gameCells = gameCells;
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                gameCells[row][col].setMinesNear(calculateMinesNearCell(row, col));
            }
        }
        return gameCells;
    }

    private GameCell[][] saveParametersAndGenerateCells(int width, int height,
                                                        int amountOnSmallerSide, int amountOnBiggerSide) {
        setSizeLenghtsAndInvertVariables(width, height);
        this.amountOnSmallerSide = amountOnSmallerSide;
        this.amountOnBiggerSide = amountOnBiggerSide;
        return generateCells();
    }

    private GameCell[][] generateCells() {
        gameCells = new GameCell[amountOnSmallerSide][amountOnBiggerSide];
        calculateLengthForCellSideAndRadius();
        calculateShiftForCells();
        if (populateWithEmptyCells) {
            populateEmptyGameCells();
        } else {
            populateGameCells();
        }
        return gameCells;
    }

    private void setSizeLenghtsAndInvertVariables(int width, int height) {
        if (height > width) {
            invertCells = true;
            smallerSideLength = width;
            biggerSideLength = height;
        } else {
            invertCells = false;
            smallerSideLength = height;
            biggerSideLength = width;
        }
    }

    private void calculateLengthForCellSideAndRadius() {
        cellSideLength = Math.min(smallerSideLength / amountOnSmallerSide,
                biggerSideLength / amountOnBiggerSide);
        radiusForCircles = cellSideLength / 2;
    }

    private void calculateShiftForCells() {
        shiftForSmallerSide = (smallerSideLength - cellSideLength * amountOnSmallerSide) / 2;
        shiftForBiggerSide = (biggerSideLength - cellSideLength * amountOnBiggerSide) / 2;
    }

    private void populateGameCells() {
        generateColors();
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                Circle circle = new Circle(getXForCol(row, col), getYForRow(row, col), radiusForCircles,
                        colorsForCircles[row][col]);
                gameCells[row][col] = new GameCell(circle, false, 0);
            }
        }
    }

    private void populateEmptyGameCells() {
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                Circle circle = new Circle(getXForCol(row, col), getYForRow(row, col), radiusForCircles,
                        0, false);
                gameCells[row][col] = new GameCell(circle, false, 0);
            }
        }
    }

    private void generateColors() {
        final int amountOfDifferentColorsThatWillBeUsed = amountOnSmallerSide;
        final int amountOfCirclesWithSameColor = amountOnBiggerSide;
        colorsForCircles = new int[amountOnSmallerSide][amountOnBiggerSide];
        populateActualColors(amountOfDifferentColorsThatWillBeUsed);
        boolean startCreateColorsFromScratch = true;
        // TODO come up with more efficient algorithm or use safe net after two or three tries, but remove that while loop
        while (startCreateColorsFromScratch) {
            startCreateColorsFromScratch = false;
            populateAmountOfCellsWithSameColorForIndex(amountOfDifferentColorsThatWillBeUsed, amountOfCirclesWithSameColor);
            for (int row = 0; row < gameCells.length && !startCreateColorsFromScratch; row++) {
                for (int col = 0; col < gameCells[0].length && !startCreateColorsFromScratch; col++) {
                    int colorIndex;
                    if (firstCell(row, col)) {
                        colorIndex = generateRandomColorIndex();
                    } else if (cellInFirstRow(row)) {
                        colorIndex = generateRandomColorIndexTakenLeftCellIntoConsideration(row, col);
                    } else {
                        colorIndex = getIndexForColorWithBiggestAmountOfLeftCellsCheckingTopAndLeftCells(row, col);
                        if (colorIndex == INVALID_INDEX) {
                            startCreateColorsFromScratch = true;
                        }
                    }
                    if (!startCreateColorsFromScratch) {
                        amountOfCellsWithSameColorForIndex.set(colorIndex, amountOfCellsWithSameColorForIndex.get(colorIndex) - 1);
                        colorsForCircles[row][col] = actualColors[colorIndex];
                    }
                }
            }
        }
    }

    private void generateMinesForCells(GameCell[][] gameCellsToAddMinesTo) {
        // TODO optimize algorithm
        while (bombsAmount != 0) {
            int row = getRandom().nextInt(gameCellsToAddMinesTo.length);
            int col = getRandom().nextInt(gameCellsToAddMinesTo[0].length);
            GameCell randomGameCell = gameCellsToAddMinesTo[row][col];
            if (!randomGameCell.isWithMine() && randomGameCell.isCircleInsideAlive()) {
                randomGameCell.setWithMine(true);
                bombsAmount--;
            }
        }
    }

    private void populateActualColors(final int amountOfDifferentColorsThatWillBeUsed) {
        actualColors = new int[amountOfDifferentColorsThatWillBeUsed];
        ArrayList<Integer> arrayOfAllColors = new ArrayList<>(Arrays.asList(R.drawable.blue_ball, R.drawable.green_ball,
                R.drawable.yellow_ball, R.drawable.orange_ball, R.drawable.red_ball,
                R.drawable.purple_ball
        ));
        for (int i = 0; i < actualColors.length; i++) {
            actualColors[i] = arrayOfAllColors.remove(getRandom().nextInt(arrayOfAllColors.size()));
        }
    }

    private void populateAmountOfCellsWithSameColorForIndex(int amountOfDifferentColorsThatWillBeUsed,
                                                            int amountOfCirclesWithSameColor) {
        amountOfCellsWithSameColorForIndex = new ArrayList<>(amountOfDifferentColorsThatWillBeUsed);
        for (int i = 0; i < amountOfDifferentColorsThatWillBeUsed; i++) {
            amountOfCellsWithSameColorForIndex.add(amountOfCirclesWithSameColor);
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

    private int generateRandomColorIndexTakenLeftCellIntoConsideration(int row, int col) {
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

    private int getIndexForColorWithBiggestAmountOfLeftCellsCheckingTopAndLeftCells(int row, int col) {
        int indexToTry = INVALID_INDEX;
        int maxAmount = 0;
        for (int i = 0; i < amountOfCellsWithSameColorForIndex.size(); i++) {
            if (amountOfCellsWithSameColorForIndex.get(i) > maxAmount
                    && isColorDifferentFromTopAndLeft(row, col, actualColors[i])) {
                maxAmount = amountOfCellsWithSameColorForIndex.get(i);
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

    private int getXForCol(int row, int col) {
        if (invertCells) {
            return radiusForCircles + cellSideLength * (amountOnSmallerSide - row - 1) + shiftForSmallerSide;
        }
        return radiusForCircles + cellSideLength * col + shiftForBiggerSide;
    }

    private int getYForRow(int row, int col) {
        if (invertCells) {
            return radiusForCircles + cellSideLength * col + shiftForBiggerSide;
        }
        return radiusForCircles + cellSideLength * row + shiftForSmallerSide;
    }

    private void throwExceptionIfMinesOfLimit() {
        if (bombsAmount > amountOnSmallerSide * amountOnBiggerSide || bombsAmount < 0) {
            throw new RuntimeException("Mines should be in range 0..cells.size");
        }
    }

    private int calculateMinesNearCell(int row, int col) {
        return calculateMinesUp(row, col) + calculateMinesDown(row, col)
                + calculateMinesInCurrentRow(row, col);
    }

    private int calculateMinesUp(int row, int col) {
        if (row == 0) return 0;
        return calculateMinesForRowAndCol(row - 1, col);
    }

    private int calculateMinesDown(int row, int col) {
        if (row == gameCells.length - 1) return 0;
        return calculateMinesForRowAndCol(row + 1, col);
    }

    private int calculateMinesInCurrentRow(int row, int col) {
        return calculateMinesLeftAndRight(row, col);
    }

    private int calculateMinesForRowAndCol(int row, int col) {
        int minesInRow = 0;
        if (gameCells[row][col].isWithMine()) {
            minesInRow++;
        }
        return minesInRow + calculateMinesLeftAndRight(row, col);
    }

    private int calculateMinesLeftAndRight(int row, int col) {
        int minesInRow = 0;
        if (col != 0) {
            if (gameCells[row][col - 1].isWithMine()) {
                minesInRow++;
            }
        }
        if (col != gameCells[0].length - 1) {
            if (gameCells[row][col + 1].isWithMine()) {
                minesInRow++;
            }
        }
        return minesInRow;
    }


}
