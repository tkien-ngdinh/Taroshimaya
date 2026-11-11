package com.taroshimaya.app

import android.app.Application
import com.taroshimaya.app.core.di.AppContainer
import com.taroshimaya.app.core.di.DefaultAppContainer

/**
 * Application entry point holding the manual DI container ([AppContainer]) for the app lifecycle.
 */
class TaroshimayaApplication : Application() {
    val container: AppContainer by lazy { DefaultAppContainer() }
}
