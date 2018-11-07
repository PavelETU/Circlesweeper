package com.wordpress.lonelytripblog.circlesminesweeper.data;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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


}
