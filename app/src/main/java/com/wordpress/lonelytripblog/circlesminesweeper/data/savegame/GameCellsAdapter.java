package com.wordpress.lonelytripblog.circlesminesweeper.data.savegame;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database.GameCellEntity;

import java.util.ArrayList;
import java.util.List;

public class GameCellsAdapter {
    public static List<GameCellEntity> toCellsForSaving(GameCell[][] gameCells) {
        int rowAmount = gameCells.length;
        int colAmount = gameCells[0].length;
        List<GameCellEntity> gameCellEntities = new ArrayList<>(rowAmount * colAmount);
        for (int row = 0; row < rowAmount; row++) {
            for (int col = 0; col < colAmount; col++) {
                GameCell currentCell = gameCells[row][col];
                gameCellEntities.add(new GameCellEntity(row, col, currentCell.isWithMine(),
                        currentCell.isMarked(), currentCell.getMinesNear(), currentCell.getTopLeftX(),
                        currentCell.getTopLeftY(), currentCell.getSideLength(),
                        currentCell.getDrawableForCircle(), currentCell.isCircleInsideAlive()));
            }
        }
        return gameCellEntities;
    }

    public static GameCell[][] toGameCells(List<GameCellEntity> gameCellEntities) {
        int maxRow = 0;
        int maxCol = 0;
        for (GameCellEntity gameCellEntity : gameCellEntities) {
            maxRow = Math.max(gameCellEntity.getRow(), maxRow);
            maxCol = Math.max(gameCellEntity.getCol(), maxCol);
        }
        GameCell[][] gameCells = new GameCell[maxRow + 1][maxCol + 1];
        for (GameCellEntity gameCellEntity : gameCellEntities) {
            int row = gameCellEntity.getRow();
            int col = gameCellEntity.getCol();
            int radius = gameCellEntity.getSideLength() / 2;
            int x = gameCellEntity.getTopLeftX();
            int y = gameCellEntity.getTopLeftY();
            Circle circle = new Circle(x + radius, y + radius, radius,
                    gameCellEntity.getCircleColorDrawableSrc(), gameCellEntity.isCircleAlive());
            gameCells[row][col] = new GameCell(circle, gameCellEntity.isWithMine(),
                    gameCellEntity.getMinesNear(), gameCellEntity.isMarked());
        }
        return gameCells;
    }
}
