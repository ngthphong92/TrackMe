package com.ngthphong92.trackme.di

import com.ngthphong92.trackme.viewmodels.TrackMeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TrackMeViewModel() }
}