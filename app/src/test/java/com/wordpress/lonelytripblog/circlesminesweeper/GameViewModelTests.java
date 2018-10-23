package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FifthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FourthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.ThirdLevel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameViewModelTests {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private GameViewModel viewModel;
    private CirclesGenerator circlesGenerator = mock(CirclesGenerator.class);
    private Observer<Circle[][]> circleObserver;

    @Before
    public void setUp() {
        viewModel = new GameViewModel(circlesGenerator);
        circleObserver = (Observer<Circle[][]>) mock(Observer.class);
        viewModel.getCircles().observeForever(circleObserver);
    }

    @Test
    public void circleLiveDataInitiallyEmpty() {
        Assert.assertNull(viewModel.getCircles().getValue());
    }

    @Test(expected = RuntimeException.class)
    public void ifStartGameCalledWithoutWidthAndHeightExceptionIsThrown() {
        viewModel.startGame();
    }

    @Test
    public void firstLevelSetup() {
        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField3X4(100, 100, 0);
    }

    @Test
    public void secondLevelSetup() {
        GameLevel level = new SecondLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField3X4(100, 100, 1);
    }

    @Test
    public void thirdLevelSetup() {
        GameLevel level = new ThirdLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField4X6(100, 100, 3);
    }

    @Test
    public void fourthLevelSetup() {
        GameLevel level = new FourthLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField6X10(100, 100, 5);
    }

    @Test
    public void verifyFifthLevelSetup() {
        GameLevel level = new FifthLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField6X10(100, 100, 7);
    }

    @Test
    public void customLevelSetup() {
        GameLevel level = new CustomLevel(GameViewModel.FIELD_4X6, 4);
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField4X6(100, 100, 4);
    }

    @Test
    public void initialValueOfLiveDataIsCorrect() {
        // blue_ball green_ball
        // green_ball blue_ball
        Circle[][] circles = new Circle[2][2];
        circles[0][0] = new Circle(50, 50, 50, R.color.blue_ball_center, false);
        circles[0][1] = new Circle(150, 50, 50, R.color.green_ball_center, false);
        circles[1][0] = new Circle(50, 150, 50, R.color.green_ball_center, false);
        circles[1][1] = new Circle(150, 150, 50, R.color.blue_ball_center, false);
        // This method is generic and not restricting the size of returned circles,
        // so don't be fool by 3X4 at the end, it's just for readability of CirclesGenerator
        when(circlesGenerator.generateCirclesForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(circles);

        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
        viewModel.startGame();

        assertEquals(circles, viewModel.getCircles().getValue());
    }

    @Test
    public void singleCircleMovesOnActionDown() {
        // blue_ball green_ball
        // green_ball blue_ball
        Circle[][] circles = new Circle[1][1];
        circles[0][0] = new Circle(50, 50, 50, R.color.blue_ball_center, false);
        // This method is generic and not restricting the size of returned circles,
        // so don't be fool by 3X4 at the end, it's just for readability of CirclesGenerator
        when(circlesGenerator.generateCirclesForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(circles);
        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
        viewModel.startGame();

        viewModel.actionDown(60, 90);

        Circle circle = viewModel.getCircles().getValue()[0][0];
        assertEquals(60, circle.getX());
        assertEquals(90, circle.getY());
        assertEquals(50 - 50 / 8, circle.getRadius());
        verify(circleObserver, times(2)).onChanged(any());
    }

}
