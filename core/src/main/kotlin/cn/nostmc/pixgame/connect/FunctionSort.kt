package cn.nostmc.pixgame.connect

import java.awt.SystemColor.text


enum class FunctionSort(val display: String) {

    LOLLIPOP("Lollipop"), // 棒棒糖
    BUILDANDBREAK("BuildAndPlace"), // 建造和破坏
    SLOPE("Slope"), // 斜坡
    PLANTSUGARBEET("PlantSugarbeet"),
    ALLMUSICADDON("AllMusicAddon"),
    MORPH("Morph"),
    NEWSPAWNENTITY("NewBieSpawnEntity"),
    SOUNDPACK("SoundPack"),
    FINDDIFFERENT("FindDifferent"),
    GROWWHEAT("GrowWheat"),


    ERROR("Error"),
    ;
    companion object {
        fun getFunctionSort(fileName: String): FunctionSort {
            for (value in entries) {
                if (fileName.contains(value.display)) {
                    return value
                }
            }
            return ERROR
        }
    }

}


