package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GameViewModelTests {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private GameViewModel viewModel;
    private CirclesGenerator circlesGenerator = mock(CirclesGenerator.class);

    @Before
    public void setUp() {
        viewModel = new GameViewModel(circlesGenerator);
        @SuppressWarnings("unchecked")
        Observer<Circle[][]> circleObserver = (Observer<Circle[][]>) mock(Observer.class);
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
        viewModel.setLevel(0);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField3X4(100, 100, 0);
    }

    @Test
    public void secondLevelSetup() {
        viewModel.setLevel(1);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField3X4(100, 100, 1);
    }

    @Test
    public void thirdLevelSetup() {
        viewModel.setLevel(2);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField4X6(100, 100, 3);
    }

    @Test
    public void fourthLevelSetup() {
        viewModel.setLevel(3);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField6X10(100, 100, 5);
    }

    @Test
    public void verifyFifthLevelSetup() {
        viewModel.setLevel(4);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField6X10(100, 100, 7);
    }

    @Test
    public void customLevelSetup() {
        viewModel.setCustomLevel(GameViewModel.FIELD_4X6, 4);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField4X6(100, 100, 4);
    }

}
