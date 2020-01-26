/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.tasks

import com.almasb.fxgl.app.AssetLoader
import com.almasb.fxgl.app.services.AssetLoaderService
import com.almasb.fxgl.core.EngineTask
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.localization.Language
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.ui.FontType
import com.almasb.fxgl.ui.UIFactoryService
import com.almasb.sslogger.Logger
import javafx.beans.property.ObjectProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LocalLoadingTask : EngineTask() {

    private val log = Logger.get(javaClass)

    @Inject("language")
    private lateinit var language: ObjectProperty<Language>

    @Inject("fontUI")
    private lateinit var fontUI: String
    @Inject("fontGame")
    private lateinit var fontGame: String
    @Inject("fontMono")
    private lateinit var fontMono: String
    @Inject("fontText")
    private lateinit var fontText: String

    private lateinit var local: LocalizationService
    private lateinit var uiFactoryService: UIFactoryService

    private lateinit var assetLoaderService: AssetLoaderService

    private lateinit var assetLoader: AssetLoader

    override fun onInit() {
        assetLoader = assetLoaderService.assetLoader

        initAndLoadLocalization()
        initAndRegisterFontFactories()

        // TODO: refactor
        IOTask.setDefaultExecutor(Async)
        IOTask.setDefaultFailAction { FXGL.getDisplay().showErrorBox(it) }
    }

    private fun initAndLoadLocalization() {
        log.debug("Loading localizations")

        Language.builtInLanguages.forEach {
            local.addLanguageData(it, assetLoader.loadResourceBundle("languages/${it.name.toLowerCase()}.properties"))
        }

        local.selectedLanguageProperty().bind(language)
    }

    private fun initAndRegisterFontFactories() {
        log.debug("Registering font factories with UI factory")

        val uiFactory = uiFactoryService

        uiFactory.registerFontFactory(FontType.UI, assetLoader.loadFont(fontUI))
        uiFactory.registerFontFactory(FontType.GAME, assetLoader.loadFont(fontGame))
        uiFactory.registerFontFactory(FontType.MONO, assetLoader.loadFont(fontMono))
        uiFactory.registerFontFactory(FontType.TEXT, assetLoader.loadFont(fontText))
    }
}