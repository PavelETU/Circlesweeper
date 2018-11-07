package com.wordpress.lonelytripblog.circlesminesweeper.data;

public class CellsGeneratorImpl implements CellsGenerator {

    private int amountOnSmallerSide;
    private int amountOnBiggerSide;
    private int smallerSideLength;
    private int biggerSideLength;
    private int lengthOfCellSide;
    private int shiftForSmallerSide;
    private int shiftForBiggerSide;


    @Override
    public GameCell[][] generateCellsForField3X4(int smallerSide, int biggerSide, int bombsAmount) {
        smallerSideLength = smallerSide;
        biggerSideLength = biggerSide;
        amountOnSmallerSide = 3;
        amountOnBiggerSide = 4;
        return generateCells();
    }

    @Override
    public GameCell[][] generateCellsForField4X6(int smallerSide, int biggerSide, int bombsAmount) {
        return new GameCell[4][6];
    }

    @Override
    public GameCell[][] generateCellsForField6X10(int smallerSide, int biggerSide, int bombsAmount) {
        return new GameCell[6][10];
    }

    private GameCell[][] generateCells() {
        calculateLengthForCellSide();
        calculateShiftForCells();
        GameCell[][] gameCells = new GameCell[amountOnSmallerSide][amountOnBiggerSide];
        int radiusForCircles = lengthOfCellSide / 2;
        int centerForFirstCirceX = radiusForCircles;
        int centerForFirstCirceY = radiusForCircles;
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                Circle circle = new Circle(centerForFirstCirceX + lengthOfCellSide * col + shiftForSmallerSide,
                        centerForFirstCirceY + lengthOfCellSide * row + shiftForBiggerSide, radiusForCircles, 0);
                gameCells[row][col] = new GameCell(circle, false);
            }
        }
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

}
