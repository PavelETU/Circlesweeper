package com.wordpress.lonelytripblog.circlesminesweeper.data;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class CellsGeneratorTests {

    private CellsGenerator cellsGenerator;

    @Before
    public void setUp() {
        cellsGenerator = new CellsGeneratorImpl();
    }

    @Test
    public void lengthFor3X4IsCorrect() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(100, 200);

        assertEquals(3, generatedGameCells.length);
        assertEquals(4, generatedGameCells[0].length);
    }

    @Test
    public void lengthFor4X6IsCorrect() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(100, 200);

        assertEquals(4, generatedGameCells.length);
        assertEquals(6, generatedGameCells[0].length);
    }

    @Test
    public void lengthFor6X10IsCorrect() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(100, 200);

        assertEquals(6, generatedGameCells.length);
        assertEquals(10, generatedGameCells[0].length);
    }

    @Test
    public void verifyCellProperPositionsFor3X4() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400, 300);

        assertEquals(3, generatedGameCells.length);
        assertEquals(4, generatedGameCells[0].length);
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor3X4WithShiftDown() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400 + 100, 300);

        assertEquals(3, generatedGameCells.length);
        assertEquals(4, generatedGameCells[0].length);
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100 + 50, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor3X4WithShiftRight() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400 + 100, 300);

        assertEquals(3, generatedGameCells.length);
        assertEquals(4, generatedGameCells[0].length);
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100 + 50, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor4X6() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(600, 400);

        assertEquals(4, generatedGameCells.length);
        assertEquals(6, generatedGameCells[0].length);
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor6X10() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(1000, 600);

        assertEquals(6, generatedGameCells.length);
        assertEquals(10, generatedGameCells[0].length);
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellRotatedProperlyFor6X10Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(1000, 600);

        GameCell[][] regeneratedGameCells = cellsGenerator.regenerateCellsForNewSize(generatedGameCells, 600, 1000);

        assertEquals(6, regeneratedGameCells.length);
        assertEquals(10, regeneratedGameCells[0].length);
        for (int row = 0; row < regeneratedGameCells.length; row++) {
            for (int col = 0; col < regeneratedGameCells[0].length; col++) {
                assertEquals((regeneratedGameCells.length - row - 1) * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void noNeighborsWithSameColorFor3X4Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400, 300);

        assertNoNeighborsWithSameColorForCells(generatedGameCells);
    }

    @Test
    public void noNeighborsWithSameColorFor4X6Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(600, 400);

        assertNoNeighborsWithSameColorForCells(generatedGameCells);
    }

    @Test
    public void noNeighborsWithSameColorFor6X10Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(1000, 600);

        assertNoNeighborsWithSameColorForCells(generatedGameCells);
    }

    @Test
    public void verifyAmountOfMines() {
        int minesToGenerate = 10;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400,
                300);
        cellsGenerator.generateMines(generatedGameCells, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test
    public void verifyZeroMines() {
        int minesToGenerate = 0;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(600,
                400);
        cellsGenerator.generateMines(generatedGameCells, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test
    public void verifyMaxAmountOfMines() {
        int minesToGenerate = 60;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(1000,
                600);
        cellsGenerator.generateMines(generatedGameCells, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test
    public void verifyMinesAmountFor4X6Field() {
        int minesToGenerate = 20;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(600,
                400);
        cellsGenerator.generateMines(generatedGameCells, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test(expected = RuntimeException.class)
    public void verifyOfLimitAmountsThrowsExceptionsTopLimit() {
        int minesToGenerate = 3 * 4 + 1;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400, 300);
        cellsGenerator.generateMines(generatedGameCells, minesToGenerate);
    }

    @Test(expected = RuntimeException.class)
    public void verifyOfLimitAmountsThrowsExceptionsBottomLimit() {
        int minesToGenerate = -1;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(400, 300);
        cellsGenerator.generateMines(generatedGameCells, minesToGenerate);
    }

    @Test
    public void verifyAmountOfMinesClosedBy() {
        int minesToGenerate = 12;

        GameCell[][] cells = cellsGenerator.generateCellsForField3X4(400, 300);
        cellsGenerator.generateMines(cells, minesToGenerate);

        verifyThatMinesNearIsMaximumForAllCells(cells);
    }

    @Test
    public void emptyCellsOnRightPositions() {
        GameCell[][] cells = cellsGenerator.generateEmptyCells(10000, 10000, 100, 100);

        assertEquals(100, cells.length);
        assertEquals(100, cells[0].length);
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[0].length; col++) {
                assertEquals(col * 100, cells[row][col].getTopLeftX());
                assertEquals(row * 100, cells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void firstTutorialLevelSetup() {
        GameCell[][] cells = cellsGenerator.generateEmptyCells(500, 500, 5, 5);

        assertEquals(5, cells.length);
        assertEquals(5, cells[0].length);
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[0].length; col++) {
                assertEquals(col * 100, cells[row][col].getTopLeftX());
                assertEquals(row * 100, cells[row][col].getTopLeftY());
                assertEquals(50, cells[row][col].getCircle().getRadius());
                assertEquals(100, cells[row][col].getSideLength());
            }
        }
    }

    @Test
    public void emptyCellsAreIndeedEmpty() {
        GameCell[][] cells = cellsGenerator.generateEmptyCells(1000, 1000, 10, 10);

        assertEquals(10, cells.length);
        assertEquals(10, cells[0].length);
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[0].length; col++) {
                assertFalse(cells[row][col].isCircleInsideAlive());
                assertFalse(cells[row][col].isWithMine());
                assertFalse(cells[row][col].isAnimated());
            }
        }
    }

    private int countMinesInCells(GameCell[][] generatedGameCells) {
        int generatedMines = 0;
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                if (generatedGameCells[row][col].isWithMine()) {
                    generatedMines++;
                }
            }
        }
        return generatedMines;
    }

    private void assertNoNeighborsWithSameColorForCells(GameCell[][] generatedGameCells) {
        // To check for colors it would be sufficient to check right circle and bottom circle
        // cause left and top circles already checked
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                if (col != generatedGameCells[0].length - 1) {
                    assertNotEquals(generatedGameCells[row][col].getCircle().getColorDrawableSrc(),
                            generatedGameCells[row][col + 1].getCircle().getColorDrawableSrc());
                }
                if (row != generatedGameCells.length - 1) {
                    assertNotEquals(generatedGameCells[row][col].getCircle().getColorDrawableSrc(),
                            generatedGameCells[row + 1][col].getCircle().getColorDrawableSrc());
                }
            }
        }
    }

    private void verifyThatMinesNearIsMaximumForAllCells(GameCell[][] generatedGameCells) {
        int lastRow = generatedGameCells.length - 1;
        int lastColumn = generatedGameCells[0].length - 1;
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                int minesNear = 3;
                if (col != 0 && col != lastColumn) {
                    minesNear += 2;
                }
                if (row != 0 && row != lastRow) {
                    minesNear += 2;
                }
                if (minesNear == 7) {
                    minesNear++;
                }
                assertEquals("row is " + row + " col is " + col, minesNear, generatedGameCells[row][col].getMinesNear());
            }
        }
    }


}
