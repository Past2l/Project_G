package me.past2l.minefarm.type.gui

data class GUIData (
    var type: String,
    var id: String,
    var name: String,
    var items: ArrayList<GUIItem>,
    var line: Int,
)