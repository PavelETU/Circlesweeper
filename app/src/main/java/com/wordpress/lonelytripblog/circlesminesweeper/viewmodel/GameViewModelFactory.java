package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@Singleton
public class GameViewModelFactory implements ViewModelProvider.Factory {

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public GameViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<ViewModel> viewModelProvider = creators.get(modelClass);
        if (viewModelProvider == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    viewModelProvider = entry.getValue();
                    break;
                }
            }
        }
        if (viewModelProvider == null) throw new RuntimeException("Unknown ViewModel");
        try {
            return (T) viewModelProvider.get();
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
