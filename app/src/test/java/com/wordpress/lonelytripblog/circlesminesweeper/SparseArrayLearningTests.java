package com.wordpress.lonelytripblog.circlesminesweeper;

import androidx.collection.SparseArrayCompat;

import junit.framework.Assert;

import org.junit.Test;

public class SparseArrayLearningTests {

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhileUnboxingNullElement() {
        SparseArrayCompat<Integer> sparseArray = new SparseArrayCompat<>();
        int count = sparseArray.get(50);
    }

    @Test
    public void zeroInitiatedProperly() {
        SparseArrayCompat<Integer> sparseArray = new SparseArrayCompat<>();
        int count = sparseArray.get(50) == null ? 0 : sparseArray.get(50);
        Assert.assertEquals(0, count);
    }

}
