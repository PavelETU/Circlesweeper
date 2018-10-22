package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

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
        Observer<List<Circle>> circleObserver = (Observer<List<Circle>>) mock(Observer.class);
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
    public void afterSettingLevelWidthAndHeightCirclesCreated() {
        viewModel.setLevel(0);
        viewModel.setSizeOfGameWindow(100, 100);
        viewModel.startGame();

        verify(circlesGenerator).generateCirclesForField3X4(100, 100, 0);
    }


}
