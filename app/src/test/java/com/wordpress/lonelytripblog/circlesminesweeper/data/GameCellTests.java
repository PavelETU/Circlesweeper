package com.wordpress.lonelytripblog.circlesminesweeper.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GameCellTests {

    private Circle circle = mock(Circle.class);
    private final static int DEFAULT_X = 50;
    private final static int DEFAULT_Y = 60;
    private final static int DEFAULT_RADIUS = 100;
    private GameCell gameCell;

    @Test
    public void gameCellShouldContainPointInTheMiddle() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertTrue(gameCell.contains(50, 50));
    }

    @Test
    public void gameCellShouldContainPointInLeftTop() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertTrue(gameCell.contains(26, 26));
    }

    @Test
    public void gameCellShouldContainPointInLeftBottom() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertTrue(gameCell.contains(26, 74));
    }

    @Test
    public void gameCellShouldContainPointInRightBottom() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertTrue(gameCell.contains(74, 74));
    }

    @Test
    public void gameCellShouldContainPointInRightTop() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertTrue(gameCell.contains(74, 26));
    }

    @Test
    public void gameCellDoesNotContainPointOnTopEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertFalse(gameCell.contains(50, 25));
    }

    @Test
    public void gameCellDoesNotContainPointOnLeftEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertFalse(gameCell.contains(25, 50));
    }

    @Test
    public void gameCellDoesNotContainPointOnRightEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertFalse(gameCell.contains(75, 50));
    }

    @Test
    public void gameCellDoesNotContainPointOnBottomEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false, 0);

        assertFalse(gameCell.contains(50, 75));
    }

    @Test
    public void circleMoveProperly() {
        teachCircleToReturnDefaultValueAndCreateGameCellWithIt();

        gameCell.moveCircleTo(100, 120);

        verifyCirclesFieldsRequested();
        verifyCircleMovedTo(100, 120);
        verifyNoMoreInteractions(circle);
    }

    @Test
    public void circleReturnedToDefaultProperlyAfterMoving() {
        teachCircleToReturnDefaultValueAndCreateGameCellWithIt();

        gameCell.moveCircleTo(100, 120);
        gameCell.moveCircleToDefaultPosition();

        verifyCirclesFieldsRequested();
        verifyCircleMovedTo(100, 120);
        verifyCircleMovedTo(DEFAULT_X, DEFAULT_Y);
        verifyNoMoreInteractions(circle);
    }

    @Test
    public void circleGetsSmallerProperly() {
        teachCircleToReturnDefaultValueAndCreateGameCellWithIt();

        gameCell.makeCircleSmaller();

        verifyCirclesFieldsRequested(1);
        verify(circle).makeCircleSmall();
        verifyNoMoreInteractions(circle);
    }

    @Test
    public void circleGetsBiggerProperly() {
        teachCircleToReturnDefaultValueAndCreateGameCellWithIt();

        gameCell.makeCircleBigger();

        verifyCirclesFieldsRequested(1);
        verify(circle).makeCircleBig();
        verifyNoMoreInteractions(circle);
    }

    @Test
    public void testSwapping() {
        Circle circleForFirstCell = new Circle(50, 50, 25, 0);
        Circle circleForSecondCell = new Circle(100, 50, 25, 1);
        GameCell gameCell1 = new GameCell(circleForFirstCell, false, 0);
        GameCell gameCell2 = new GameCell(circleForSecondCell, false, 0);

        gameCell1.swapCirclesWith(gameCell2);

        assertEquals(circleForSecondCell, gameCell1.getCircle());
        assertEquals(circleForFirstCell, gameCell2.getCircle());
    }

    @Test
    public void sameColorNegative() {
        Circle circleForFirstCell = new Circle(50, 50, 25, 0);
        Circle circleForSecondCell = new Circle(100, 50, 25, 1);
        GameCell gameCell1 = new GameCell(circleForFirstCell, false, 0);
        GameCell gameCell2 = new GameCell(circleForSecondCell, false, 0);

        assertFalse(gameCell1.isColorTheSame(gameCell2));
    }

    @Test
    public void sameColorPositive() {
        Circle circleForFirstCell = new Circle(50, 50, 25, 1);
        Circle circleForSecondCell = new Circle(100, 50, 25, 1);
        GameCell gameCell1 = new GameCell(circleForFirstCell, false, 0);
        GameCell gameCell2 = new GameCell(circleForSecondCell, false, 0);

        assertTrue(gameCell1.isColorTheSame(gameCell2));
    }

    private void verifyCirclesFieldsRequested(int timesGetRadiusInvoked) {
        verify(circle).getX();
        verify(circle).getY();
        verify(circle, times(timesGetRadiusInvoked)).getRadius();
    }

    private void verifyCirclesFieldsRequested() {
        verifyCirclesFieldsRequested(1);
    }

    private void verifyCircleMovedTo(int x, int y) {
        verify(circle).setX(x);
        verify(circle).setY(y);
    }

    private void teachCircleToReturnDefaultValueAndCreateGameCellWithIt() {
        when(circle.getX()).thenReturn(DEFAULT_X);
        when(circle.getY()).thenReturn(DEFAULT_Y);
        when(circle.getRadius()).thenReturn(DEFAULT_RADIUS);

        gameCell = new GameCell(circle, false, 0);
    }

}