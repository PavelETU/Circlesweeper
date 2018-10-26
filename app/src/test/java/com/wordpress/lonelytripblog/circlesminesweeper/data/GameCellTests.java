package com.wordpress.lonelytripblog.circlesminesweeper.data;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GameCellTests {

    private Circle circle = mock(Circle.class);
    private final int defaultX = 50;
    private final int defaultY = 60;
    private final int defaultRadius = 100;
    private GameCell gameCell;

    @Test
    public void gameCellShouldContainPointInTheMiddle() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertTrue(gameCell.contains(50, 50));
    }

    @Test
    public void gameCellShouldContainPointInLeftTop() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertTrue(gameCell.contains(26, 26));
    }

    @Test
    public void gameCellShouldContainPointInLeftBottom() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertTrue(gameCell.contains(26, 74));
    }

    @Test
    public void gameCellShouldContainPointInRightBottom() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertTrue(gameCell.contains(74, 74));
    }

    @Test
    public void gameCellShouldContainPointInRightTop() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertTrue(gameCell.contains(74, 26));
    }

    @Test
    public void gameCellDoesNotContainPointOnTopEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertFalse(gameCell.contains(50, 25));
    }

    @Test
    public void gameCellDoesNotContainPointOnLeftEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertFalse(gameCell.contains(25, 50));
    }

    @Test
    public void gameCellDoesNotContainPointOnRightEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

        assertFalse(gameCell.contains(75, 50));
    }

    @Test
    public void gameCellDoesNotContainPointOnBottomEdge() {
        Circle circle = new Circle(50, 50, 25, 0);

        GameCell gameCell = new GameCell(circle, false);

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
        verifyCircleMovedTo(defaultX, defaultY);
        verifyNoMoreInteractions(circle);
    }

    @Test
    public void circleGetsSmallerProperly() {
        teachCircleToReturnDefaultValueAndCreateGameCellWithIt();

        gameCell.makeCircleSmaller();

        verifyCirclesFieldsRequested(2);
        verify(circle).setRadius((int)(defaultRadius * 0.85));
        verifyNoMoreInteractions(circle);
    }

    @Test
    public void circleGetsBiggerProperly() {
        teachCircleToReturnDefaultValueAndCreateGameCellWithIt();

        gameCell.makeCircleBigger();

        verifyCirclesFieldsRequested(2);
        verify(circle).setRadius((int)(defaultRadius * 1.15f));
        verifyNoMoreInteractions(circle);
    }

    private void verifyCirclesFieldsRequested(final int timesGetRadiusInvoked) {
        verify(circle).getX();
        verify(circle).getY();
        verify(circle, times(timesGetRadiusInvoked)).getRadius();
    }

    private void verifyCirclesFieldsRequested() {
        verifyCirclesFieldsRequested(1);
    }

    private void verifyCircleMovedTo(final int x, final int y) {
        verify(circle).setX(x);
        verify(circle).setY(y);
    }

    private void teachCircleToReturnDefaultValueAndCreateGameCellWithIt() {
        when(circle.getX()).thenReturn(defaultX);
        when(circle.getY()).thenReturn(defaultY);
        when(circle.getRadius()).thenReturn(defaultRadius);

        gameCell = new GameCell(circle, false);
    }

}