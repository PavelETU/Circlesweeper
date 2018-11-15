package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import org.junit.Rule;
import org.junit.Test;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapperTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void widthIsAlwaysBigger() {
        Mapper mapper = new Mapper(600, 400);
        Mapper mapper2 = new Mapper(400, 600);

        assertTrue(mapper.initialGameWindowWidth > mapper.initialGameWindowHeight);
        assertTrue(mapper2.initialGameWindowWidth > mapper2.initialGameWindowHeight);
    }

    @Test
    public void heightAndWidthSwappedIfInitialHeightBiggerThenWidth() {
        int height = 600;
        int width = 400;
        Mapper mapper = new Mapper(height, width);

        assertEquals(height, mapper.initialGameWindowWidth, 0.00001);
        assertEquals(width, mapper.initialGameWindowHeight, 0.00001);
    }

    @Test
    public void displayingInDefaultHasNoEffectOnCoords() {
        Mapper mapper = new Mapper(400, 600);

        Point point = mapper.mapXYFromViewToGameWindow(10, 20);

        assertEquals(10, point.x, 0.00001);
        assertEquals(20, point.y, 0.00001);
    }

    @Test
    public void rotatedFieldClickOnTopLeftCorner() {
        int height = 600;
        int width = 400;
        Mapper mapper = new Mapper(height, width);

        Point point = mapper.mapXYFromViewToGameWindow(0, 0);

        assertEquals(0, point.x, 0.00001);
        assertEquals(width - 1, point.y, 0.00001);
    }

    @Test
    public void rotatedFieldClickOnTopRightCorner() {
        int height = 600;
        int width = 400;
        Mapper mapper = new Mapper(height, width);

        Point point = mapper.mapXYFromViewToGameWindow(width - 1, 0);

        assertEquals(0, point.x, 0.00001);
        assertEquals(0, point.y, 0.00001);
    }

    @Test
    public void rotatedFieldClickOnBottomRightCorner() {
        int height = 600;
        int width = 400;
        Mapper mapper = new Mapper(height, width);

        Point point = mapper.mapXYFromViewToGameWindow(width - 1, height - 1);

        assertEquals(height - 1, point.x, 0.00001);
        assertEquals(0, point.y, 0.00001);
    }

    @Test
    public void rotatedFieldClickOnBottomLeftCorner() {
        int height = 600;
        int width = 400;
        Mapper mapper = new Mapper(height, width);

        Point point = mapper.mapXYFromViewToGameWindow(0, height - 1);

        assertEquals(height - 1, point.x, 0.00001);
        assertEquals(width - 1, point.y, 0.00001);
    }

    @Test
    public void fieldMadeTwoTimesSmallerGetsProperlyMapped() {
        Mapper mapper = new Mapper(400, 600);

        mapper.setNewSize(200, 300);
        Point point = mapper.mapXYFromViewToGameWindow(100, 150);

        assertEquals(200, point.x, 0.00001);
        assertEquals(300, point.y, 0.00001);
    }

    @Test
    public void fieldMadeTwoTimesBiggerGetsProperlyMapped() {
        Mapper mapper = new Mapper(400, 600);

        mapper.setNewSize(800, 1200);
        Point point = mapper.mapXYFromViewToGameWindow(100, 200);

        assertEquals(50, point.x, 0.00001);
        assertEquals(100, point.y, 0.00001);
    }

}