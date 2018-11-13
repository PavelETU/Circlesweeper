package com.wordpress.lonelytripblog.circlesminesweeper.data;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CellsGeneratorTests {

    private CellsGenerator cellsGenerator;

    @Before
    public void setUp() {
        cellsGenerator = new CellsGeneratorImpl();
    }

    @Test
    public void lengthFor3X4IsCorrect() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(100, 200, 0);

        assertEquals(3, generatedGameCells.length);
        assertEquals(4, generatedGameCells[0].length);
    }

    @Test
    public void lengthFor4X6IsCorrect() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(100, 200, 0);

        assertEquals(4, generatedGameCells.length);
        assertEquals(6, generatedGameCells[0].length);
    }

    @Test
    public void lengthFor6X10IsCorrect() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(100, 200, 0);

        assertEquals(6, generatedGameCells.length);
        assertEquals(10, generatedGameCells[0].length);
    }

    @Test
    public void verifyCellProperPositionsFor3X4() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(300, 400, 0);

        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor3X4WithShiftDown() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(300, 400 + 100, 0);

        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100 + 50, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor3X4WithShiftRight() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(300 + 100, 400, 0);

        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100 + 50, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor4X6() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(400, 600, 0);

        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void verifyCellProperPositionsFor6X10() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(600, 1000, 0);

        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                assertEquals(col * 100, generatedGameCells[row][col].getTopLeftX());
                assertEquals(row * 100, generatedGameCells[row][col].getTopLeftY());
            }
        }
    }

    @Test
    public void noNeighborsWithSameColorFor3X4Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(300, 400, 0);

        assertNoNeighborsWithSameColorForCells(generatedGameCells);
    }

    @Test
    public void noNeighborsWithSameColorFor4X6Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(400, 600, 0);

        assertNoNeighborsWithSameColorForCells(generatedGameCells);
    }

    @Test
    public void noNeighborsWithSameColorFor6X10Field() {
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(600, 1000, 0);

        assertNoNeighborsWithSameColorForCells(generatedGameCells);
    }

    @Test
    public void verifyAmountOfMines() {
        final int minesToGenerate = 10;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField3X4(300,
                400, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test
    public void verifyZeroMines() {
        final int minesToGenerate = 0;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(400,
                600, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test
    public void verifyMaxAmountOfMines() {
        final int minesToGenerate = 60;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField6X10(600,
                1000, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test
    public void verifyMinesAmountFor4X6Field() {
        final int minesToGenerate = 20;
        GameCell[][] generatedGameCells = cellsGenerator.generateCellsForField4X6(400,
                600, minesToGenerate);

        int generatedMines = countMinesInCells(generatedGameCells);

        assertEquals(minesToGenerate, generatedMines);
    }

    @Test(expected = RuntimeException.class)
    public void verifyOfLimitAmountsThrowsExceptionsTopLimit() {
        final int minesToGenerate = 3 * 4 + 1;
        cellsGenerator.generateCellsForField3X4(300,400, minesToGenerate);
    }

    @Test(expected = RuntimeException.class)
    public void verifyOfLimitAmountsThrowsExceptionsBottomLimit() {
        final int minesToGenerate = -1;
        cellsGenerator.generateCellsForField3X4(300,400, minesToGenerate);
    }

    private int countMinesInCells(final GameCell[][] generatedGameCells) {
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

    private void assertNoNeighborsWithSameColorForCells(final GameCell[][] generatedGameCells) {
        // To check for colors it would be sufficient to check right circle and bottom circle
        // cause left and top circles already checked
        for (int row = 0; row < generatedGameCells.length; row++) {
            for (int col = 0; col < generatedGameCells[0].length; col++) {
                if (col != generatedGameCells[0].length - 1) {
                    assertNotEquals(generatedGameCells[row][col].getCircle().getColor(),
                            generatedGameCells[row][col + 1].getCircle().getColor());
                }
                if (row != generatedGameCells.length - 1) {
                    assertNotEquals(generatedGameCells[row][col].getCircle().getColor(),
                            generatedGameCells[row + 1][col].getCircle().getColor());
                }
            }
        }
    }


}
