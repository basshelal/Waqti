package uk.whitecrescent.waqti.frontend.appearance

import uk.whitecrescent.waqti.frontend.appearance.Color.AMBER
import uk.whitecrescent.waqti.frontend.appearance.Color.BLUE
import uk.whitecrescent.waqti.frontend.appearance.Color.BLUE_GREY
import uk.whitecrescent.waqti.frontend.appearance.Color.BROWN
import uk.whitecrescent.waqti.frontend.appearance.Color.CYAN
import uk.whitecrescent.waqti.frontend.appearance.Color.DEEP_ORANGE
import uk.whitecrescent.waqti.frontend.appearance.Color.DEEP_PURPLE
import uk.whitecrescent.waqti.frontend.appearance.Color.GREEN
import uk.whitecrescent.waqti.frontend.appearance.Color.GREY
import uk.whitecrescent.waqti.frontend.appearance.Color.INDIGO
import uk.whitecrescent.waqti.frontend.appearance.Color.LIGHT_BLUE
import uk.whitecrescent.waqti.frontend.appearance.Color.LIGHT_GREEN
import uk.whitecrescent.waqti.frontend.appearance.Color.LIME
import uk.whitecrescent.waqti.frontend.appearance.Color.ORANGE
import uk.whitecrescent.waqti.frontend.appearance.Color.PINK
import uk.whitecrescent.waqti.frontend.appearance.Color.PURPLE
import uk.whitecrescent.waqti.frontend.appearance.Color.RED
import uk.whitecrescent.waqti.frontend.appearance.Color.TEAL
import uk.whitecrescent.waqti.frontend.appearance.Color.YELLOW
import uk.whitecrescent.waqti.frontend.appearance.Level.EIGHT_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.FIFTY
import uk.whitecrescent.waqti.frontend.appearance.Level.FIVE_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.FOUR_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.NINE_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.ONE_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.SEVEN_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.SIX_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.THREE_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.TWO_HUNDRED
import uk.whitecrescent.waqti.frontend.appearance.Level.values
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor.Companion.BLACK
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor.Companion.WHITE

class MaterialColor {

    companion object {

        fun getMaterialColors(color: Color): List<WaqtiColor> {
            return when (color) {
                RED -> listOf("#FFEBEE", "#FFCDD2", "#EF9A9A", "#E57373", "#EF5350", "#F44336", "#E53935", "#D32F2F", "#C62828", "#B71C1C").map { it.toColor }
                PINK -> listOf("#FCE4EC", "#F8BBD0", "#F48FB1", "#F06292", "#EC407A", "#E91E63", "#D81B60", "#C2185B", "#AD1457", "#880E4F").map { it.toColor }
                PURPLE -> listOf("#F3E5F5", "#E1BEE7", "#CE93D8", "#BA68C8", "#AB47BC", "#9C27B0", "#8E24AA", "#7B1FA2", "#6A1B9A", "#4A148C").map { it.toColor }
                DEEP_PURPLE -> listOf("#EDE7F6", "#D1C4E9", "#B39DDB", "#9575CD", "#7E57C2", "#673AB7", "#5E35B1", "#512DA8", "#4527A0", "#311B92").map { it.toColor }
                INDIGO -> listOf("#E8EAF6", "#C5CAE9", "#9FA8DA", "#7986CB", "#5C6BC0", "#3F51B5", "#3949AB", "#303F9F", "#283593", "#1A237E").map { it.toColor }
                BLUE -> listOf("#E3F2FD", "#BBDEFB", "#90CAF9", "#64B5F6", "#42A5F5", "#2196F3", "#1E88E5", "#1976D2", "#1565C0", "#0D47A1").map { it.toColor }
                LIGHT_BLUE -> listOf("#E1F5FE", "#B3E5FC", "#81D4fA", "#4fC3F7", "#29B6FC", "#03A9F4", "#039BE5", "#0288D1", "#0277BD", "#01579B").map { it.toColor }
                CYAN -> listOf("#E0F7FA", "#B2EBF2", "#80DEEA", "#4DD0E1", "#26C6DA", "#00BCD4", "#00ACC1", "#0097A7", "#00838F", "#006064").map { it.toColor }
                TEAL -> listOf("#E0F2F1", "#B2DFDB", "#80CBC4", "#4DB6AC", "#26A69A", "#009688", "#00897B", "#00796B", "#00695C", "#004D40").map { it.toColor }
                GREEN -> listOf("#E8F5E9", "#C8E6C9", "#A5D6A7", "#81C784", "#66BB6A", "#4CAF50", "#43A047", "#388E3C", "#2E7D32", "#1B5E20").map { it.toColor }
                LIGHT_GREEN -> listOf("#F1F8E9", "#DCEDC8", "#C5E1A5", "#AED581", "#9CCC65", "#8BC34A", "#7CB342", "#689F38", "#558B2F", "#33691E").map { it.toColor }
                LIME -> listOf("#F9FBE7", "#F0F4C3", "#E6EE9C", "#DCE775", "#D4E157", "#CDDC39", "#C0CA33", "#A4B42B", "#9E9D24", "#827717").map { it.toColor }
                YELLOW -> listOf("#FFFDE7", "#FFF9C4", "#FFF590", "#FFF176", "#FFEE58", "#FFEB3B", "#FDD835", "#FBC02D", "#F9A825", "#F57F17").map { it.toColor }
                AMBER -> listOf("#FFF8E1", "#FFECB3", "#FFE082", "#FFD54F", "#FFCA28", "#FFC107", "#FFB300", "#FFA000", "#FF8F00", "#FF6F00").map { it.toColor }
                ORANGE -> listOf("#FFF3E0", "#FFE0B2", "#FFCC80", "#FFB74D", "#FFA726", "#FF9800", "#FB8C00", "#F57C00", "#EF6C00", "#E65100").map { it.toColor }
                DEEP_ORANGE -> listOf("#FBE9A7", "#FFCCBC", "#FFAB91", "#FF8A65", "#FF7043", "#FF5722", "#F4511E", "#E64A19", "#D84315", "#BF360C").map { it.toColor }
                BROWN -> listOf("#EFEBE9", "#D7CCC8", "#BCAAA4", "#A1887F", "#8D6E63", "#795548", "#6D4C41", "#5D4037", "#4E342E", "#3E2723").map { it.toColor }
                GREY -> listOf("#FAFAFA", "#F5F5F5", "#EEEEEE", "#E0E0E0", "#BDBDBD", "#9E9E9E", "#757575", "#616161", "#424242", "#212121").map { it.toColor }
                BLUE_GREY -> listOf("#ECEFF1", "#CFD8DC", "#B0BBC5", "#90A4AE", "#78909C", "#607D8B", "#546E7A", "#455A64", "#37474F", "#263238").map { it.toColor }
            }
        }

        fun getMaterialColors(materialColorLevel: Level): List<WaqtiColor> {
            return when (materialColorLevel) {
                FIFTY -> listOf("#FFEBEE", "#FCE4EC", "#F3E5F5", "#EDE7F6", "#E8EAF6", "#E3F2FD", "#E1F5FE", "#E0F7FA", "#E0F2F1", "#E8F5E9", "#F1F8E9", "#F9FBE7", "#FFFDE7", "#FFF8E1", "#FFF3E0", "#FBE9A7", "#EFEBE9", "#FAFAFA", "#ECEFF1").map { it.toColor }
                ONE_HUNDRED -> listOf("#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#BBDEFB", "#B3E5FC", "#B2EBF2", "#B2DFDB", "#C8E6C9", "#DCEDC8", "#F0F4C3", "#FFF9C4", "#FFECB3", "#FFE0B2", "#FFCCBC", "#D7CCC8", "#F5F5F5", "#CFD8DC").map { it.toColor }
                TWO_HUNDRED -> listOf("#EF9A9A", "#F48FB1", "#CE93D8", "#B39DDB", "#9FA8DA", "#90CAF9", "#81D4fA", "#80DEEA", "#80CBC4", "#A5D6A7", "#C5E1A5", "#E6EE9C", "#FFF590", "#FFE082", "#FFCC80", "#FFAB91", "#BCAAA4", "#EEEEEE", "#B0BBC5").map { it.toColor }
                THREE_HUNDRED -> listOf("#E57373", "#F06292", "#BA68C8", "#9575CD", "#7986CB", "#64B5F6", "#4fC3F7", "#4DD0E1", "#4DB6AC", "#81C784", "#AED581", "#DCE775", "#FFF176", "#FFD54F", "#FFB74D", "#FF8A65", "#A1887F", "#E0E0E0", "#90A4AE").map { it.toColor }
                FOUR_HUNDRED -> listOf("#EF5350", "#EC407A", "#AB47BC", "#7E57C2", "#5C6BC0", "#42A5F5", "#29B6FC", "#26C6DA", "#26A69A", "#66BB6A", "#9CCC65", "#D4E157", "#FFEE58", "#FFCA28", "#FFA726", "#FF7043", "#8D6E63", "#BDBDBD", "#78909C").map { it.toColor }
                FIVE_HUNDRED -> listOf("#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B").map { it.toColor }
                SIX_HUNDRED -> listOf("#E53935", "#D81B60", "#8E24AA", "#5E35B1", "#3949AB", "#1E88E5", "#039BE5", "#00ACC1", "#00897B", "#43A047", "#7CB342", "#C0CA33", "#FDD835", "#FFB300", "#FB8C00", "#F4511E", "#6D4C41", "#757575", "#546E7A").map { it.toColor }
                SEVEN_HUNDRED -> listOf("#D32F2F", "#C2185B", "#7B1FA2", "#512DA8", "#303F9F", "#1976D2", "#0288D1", "#0097A7", "#00796B", "#388E3C", "#689F38", "#A4B42B", "#FBC02D", "#FFA000", "#F57C00", "#E64A19", "#5D4037", "#616161", "#455A64").map { it.toColor }
                EIGHT_HUNDRED -> listOf("#C62828", "#AD1457", "#6A1B9A", "#4527A0", "#283593", "#1565C0", "#0277BD", "#00838F", "#00695C", "#2E7D32", "#558B2F", "#9E9D24", "#F9A825", "#FF8F00", "#EF6C00", "#D84315", "#4E342E", "#424242", "#37474F").map { it.toColor }
                NINE_HUNDRED -> listOf("#B71C1C", "#880E4F", "#4A148C", "#311B92", "#1A237E", "#0D47A1", "#01579B", "#006064", "#004D40", "#1B5E20", "#33691E", "#827717", "#F57F17", "#FF6F00", "#E65100", "#BF360C", "#3E2723", "#212121", "#263238").map { it.toColor }
            }
        }

        fun getAllMaterialColors(): List<WaqtiColor> {
            return Color.values().flatMap { color ->
                values().map { colorLevel ->
                    getMaterialColor(color, colorLevel)
                }
            }
        }

        fun getMaterialColor(materialColorClass: Color, materialColorLevel: Level): WaqtiColor {
            return getMaterialColors(materialColorClass).intersect(getMaterialColors(materialColorLevel)).first()
        }

    }
}

enum class Color {
    RED, PINK, PURPLE, DEEP_PURPLE, INDIGO, BLUE, LIGHT_BLUE, CYAN,
    TEAL, GREEN, LIGHT_GREEN, LIME, YELLOW, AMBER, ORANGE, DEEP_ORANGE,
    BROWN, GREY, BLUE_GREY
}

enum class Level {
    FIFTY, ONE_HUNDRED, TWO_HUNDRED, THREE_HUNDRED, FOUR_HUNDRED, FIVE_HUNDRED, SIX_HUNDRED,
    SEVEN_HUNDRED, EIGHT_HUNDRED, NINE_HUNDRED
}

data class ColorLevel(val color: Color, val level: Level)

data class ColorScheme(val light: WaqtiColor, val main: WaqtiColor,
                       val dark: WaqtiColor, val text: WaqtiColor = BLACK)

fun getColorScheme(colorLevel: ColorLevel): ColorScheme {
    when (colorLevel.color) {
        //region RED
        RED -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#FFEBEE".c,
                    dark = "#CB9CA1".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#FFFFFF".c, main = "#FFCDD2".c,
                    dark = "#CB9CA1".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#FFCCCB".c, main = "#ED9A9A".c,
                    dark = "#BA6B6C".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#FFA4A2".c, main = "#E57373".c,
                    dark = "#AF4448".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#FF867C".c, main = "#EF5350".c,
                    dark = "#B61827".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#FF7961".c, main = "#F44336".c,
                    dark = "#BA000D".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#FF6F60".c, main = "#E53935".c,
                    dark = "#AB000D".c, text = BLACK)
            SEVEN_HUNDRED -> ColorScheme(light = "#FF6659".c, main = "#D32F2F".c,
                    dark = "#9A0007".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#FF5F52".c, main = "#C62828".c,
                    dark = "#8E0000".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#F05545".c, main = "#B71C1C".c,
                    dark = "#7F0000".c, text = WHITE)
        }
        //endregion RED
        //region PINK
        PINK -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#FCE4EC".c,
                    dark = "#C9B2BA".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#FFEEFF".c, main = "#F8BBD0".c,
                    dark = "#C48B9F".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#FFC1E3".c, main = "#F48FB1".c,
                    dark = "#BF5F82".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#FF94C2".c, main = "#F06292".c,
                    dark = "#BA2D65".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#FF77A9".c, main = "#EC407A".c,
                    dark = "#B4004E".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#FF6090".c, main = "#E91E63".c,
                    dark = "#B0003A".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#FF5C8D".c, main = "#D81B60".c,
                    dark = "#A00037".c, text = WHITE)
            SEVEN_HUNDRED -> ColorScheme(light = "#FA5788".c, main = "#C2185B".c,
                    dark = "#8C0032".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#E35183".c, main = "#AD1457".c,
                    dark = "#78002E".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#BC477B".c, main = "#880E4F".c,
                    dark = "#560027".c, text = WHITE)
        }
        //endregion PINK
        //region PURPLE
        PURPLE -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#FCE4EC".c,
                    dark = "#C9B2bA".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#FFEEFF".c, main = "#F8BBD0".c,
                    dark = "#C48B9F".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#FFC1E3".c, main = "#F48FB1".c,
                    dark = "#BF5F82".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#FF94C2".c, main = "#F06292".c,
                    dark = "#BA2D65".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#FF77A9".c, main = "#EC407A".c,
                    dark = "#B4004E".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#FF6090".c, main = "#E91E63".c,
                    dark = "#B0003A".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#FF5C8D".c, main = "#D81B60".c,
                    dark = "#A00037".c, text = WHITE)
            SEVEN_HUNDRED -> ColorScheme(light = "#FA5788".c, main = "#C2185B".c,
                    dark = "#8C0032".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#E35183".c, main = "#AD1457".c,
                    dark = "#78002E".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#BC477B".c, main = "#880E4F".c,
                    dark = "#560027".c, text = WHITE)
        }
        //endregion PURPLE
        //region DEEP PURPLE
        DEEP_PURPLE -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#EDE7F6".c,
                    dark = "#BBB5C3".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#FFF7FF".c, main = "#D1C4E9".c,
                    dark = "#A094B7".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#E6CEFF".c, main = "#B39DDB".c,
                    dark = "#836FA9".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#C7A4FF".c, main = "#9575CD".c,
                    dark = "#65499C".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#B085F5".c, main = "#7E57C2".c,
                    dark = "#4D2C91".c, text = WHITE)
            FIVE_HUNDRED -> ColorScheme(light = "#9A67EA".c, main = "#673AB7".c,
                    dark = "#320B86".c, text = WHITE)
            SIX_HUNDRED -> ColorScheme(light = "#9162E4".c, main = "#5E35B1".c,
                    dark = "#280680".c, text = WHITE)
            SEVEN_HUNDRED -> ColorScheme(light = "#8559dA".c, main = "#512DA8".c,
                    dark = "#140078".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#7953D2".c, main = "#4527A0".c,
                    dark = "#000070".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#6746C3".c, main = "#311B92".c,
                    dark = "#000063".c, text = WHITE)
        }
        //endregion DEEP PURPLE
        //region INDIGO
        INDIGO -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#E8EAF6".c,
                    dark = "#B6B8C3".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#F8FDFF".c, main = "#C5CAE9".c,
                    dark = "#9499B7".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#D1D9FF".c, main = "#9FA8DA".c,
                    dark = "#6F79A8".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#AAB6FE".c, main = "#7986CB".c,
                    dark = "#49599A".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#8E99F3".c, main = "#5C6BC0".c,
                    dark = "#26418F".c, text = WHITE)
            FIVE_HUNDRED -> ColorScheme(light = "#757DE8".c, main = "#3F51B5".c,
                    dark = "#002984".c, text = WHITE)
            SIX_HUNDRED -> ColorScheme(light = "#6F74DD".c, main = "#3949AB".c,
                    dark = "#00227B".c, text = WHITE)
            SEVEN_HUNDRED -> ColorScheme(light = "#666AD1".c, main = "#303F9F".c,
                    dark = "#001970".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#5F5FC4".c, main = "#283593".c,
                    dark = "#001064".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#534BAE".c, main = "#1A237E".c,
                    dark = "#000051".c, text = WHITE)
        }
        //endregion INDIGO
        //region BLUE
        BLUE -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#E3F2FD".c,
                    dark = "#B1BFCA".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#EEFFFF".c, main = "#BBDEFB".c,
                    dark = "#8AACC8".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#C3FDFF".c, main = "#90CAF9".c,
                    dark = "#5D99C6".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#9BE7FF".c, main = "#64B5F6".c,
                    dark = "#2286C3".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#80D6FF".c, main = "#42A5F5".c,
                    dark = "#0077C2".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#6EC6FF".c, main = "#2196F3".c,
                    dark = "#0069C0".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#6AB7FF".c, main = "#1E88E5".c,
                    dark = "#005CB2".c, text = BLACK)
            SEVEN_HUNDRED -> ColorScheme(light = "#63A4FF".c, main = "#1976D2".c,
                    dark = "#004BA0".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#5E92F3".c, main = "#1565C0".c,
                    dark = "#003C8F".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#5472D3".c, main = "#0D47A1".c,
                    dark = "#002171".c, text = WHITE)
        }
        //endregion BLUE
        //region LIGHT BLUE
        LIGHT_BLUE -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#E1F5FE".c,
                    dark = "#AFC2CB".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#E6FFFF".c, main = "#B3E5FC".c,
                    dark = "#82B3C9".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#b6ffff".c, main = "#81d4fa".c,
                    dark = "#4BA3C7".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#8bF6FF".c, main = "#4FC3F7".c,
                    dark = "#0093C4".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#73E8FF".c, main = "#29B6F6".c,
                    dark = "#0086C3".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#67DAFF".c, main = "#03A9F4".c,
                    dark = "#007AC1".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#63CCFF".c, main = "#039BE5".c,
                    dark = "#006DB3".c, text = BLACK)
            SEVEN_HUNDRED -> ColorScheme(light = "#5eb8ff".c, main = "#0288d1".c,
                    dark = "#005B9F".c, text = BLACK)
            EIGHT_HUNDRED -> ColorScheme(light = "#58A5F0".c, main = "#0277BD".c,
                    dark = "#004C8C".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#4F83CC".c, main = "#01579B".c,
                    dark = "#002F6C".c, text = WHITE)
        }
        //endregion LIGHT BLUE
        //region CYAN
        CYAN -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#E0F7FA".c,
                    dark = "#AEC4C7".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#E5FFFF".c, main = "#B2EBF2".c,
                    dark = "#81B9BF".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#B4FFFF".c, main = "#80DEEA".c,
                    dark = "#4BACB8".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#88FFFF".c, main = "#4DD0E1".c,
                    dark = "#009FAF".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#6FF9FF".c, main = "#26C6DA".c,
                    dark = "#0095A8".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#62EFFF".c, main = "#00BCD4".c,
                    dark = "#008BA3".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#5DDEF4".c, main = "#00ACC1".c,
                    dark = "#007C91".c, text = BLACK)
            SEVEN_HUNDRED -> ColorScheme(light = "#56C8D8".c, main = "#0097A7".c,
                    dark = "#006978".c, text = BLACK)
            EIGHT_HUNDRED -> ColorScheme(light = "#4FB3BF".c, main = "#00838F".c,
                    dark = "#005662".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#428E92".c, main = "#006064".c,
                    dark = "#00363A".c, text = WHITE)
        }
        //endregion CYAN
        //region TEAL
        TEAL -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#E0F2F1".c,
                    dark = "#AEBFBE".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#E5FFFF".c, main = "#B2DFDB".c,
                    dark = "#82ADA9".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#B2FEF7".c, main = "#80CBC4".c,
                    dark = "#4F9A94".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#82E9DE".c, main = "#4DB6AC".c,
                    dark = "#00867D".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#64D8CB".c, main = "#26A69A".c,
                    dark = "#00766C".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#52C7B8".c, main = "#009688".c,
                    dark = "#00675B".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#4EBAAA".c, main = "#00897B".c,
                    dark = "#005B4F".c, text = BLACK)
            SEVEN_HUNDRED -> ColorScheme(light = "#48A999".c, main = "#00796B".c,
                    dark = "#004C40".c, text = WHITE)
            EIGHT_HUNDRED -> ColorScheme(light = "#439889".c, main = "#00695C".c,
                    dark = "#003D33".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#39796B".c, main = "#004D40".c,
                    dark = "#00251A".c, text = WHITE)
        }
        //endregion TEAL
        //region GREEN
        GREEN -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "#FFFFFF".c, main = "#E8F5E9".c,
                    dark = "#B6C2B7".c, text = BLACK)
            ONE_HUNDRED -> ColorScheme(light = "#FBFFFC".c, main = "#C8E6CC9".c,
                    dark = "#97B498".c, text = BLACK)
            TWO_HUNDRED -> ColorScheme(light = "#D7FFD9".c, main = "#A5D6A7".c,
                    dark = "#75A478".c, text = BLACK)
            THREE_HUNDRED -> ColorScheme(light = "#B2FAB4".c, main = "#81C784".c,
                    dark = "#519657".c, text = BLACK)
            FOUR_HUNDRED -> ColorScheme(light = "#98EE99".c, main = "#66BB6A".c,
                    dark = "#338A3E".c, text = BLACK)
            FIVE_HUNDRED -> ColorScheme(light = "#80E27E".c, main = "#4CAF50".c,
                    dark = "#087f23".c, text = BLACK)
            SIX_HUNDRED -> ColorScheme(light = "#76D275".c, main = "#43A047".c,
                    dark = "#00701A".c, text = BLACK)
            SEVEN_HUNDRED -> ColorScheme(light = "#6ABF69".c, main = "#388E3C".c,
                    dark = "#00600F".c, text = BLACK)
            EIGHT_HUNDRED -> ColorScheme(light = "#60AD5E".c, main = "#2E7d32".c,
                    dark = "#005005".c, text = WHITE)
            NINE_HUNDRED -> ColorScheme(light = "#4C8C4A".c, main = "#1B5E20".c,
                    dark = "#003300".c, text = WHITE)
        }
        //endregion GREEN
        LIGHT_GREEN -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        LIME -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        YELLOW -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        AMBER -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        ORANGE -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        DEEP_ORANGE -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        BROWN -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        GREY -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
        BLUE_GREY -> return when (colorLevel.level) {
            FIFTY -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            ONE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            TWO_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            THREE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FOUR_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            FIVE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SIX_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            SEVEN_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            EIGHT_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
            NINE_HUNDRED -> ColorScheme(light = "".c, main = "".c,
                    dark = "".c, text = "".c)
        }
    }
}

fun getAllColorSchemes(color: Color) = Level.values().map { getColorScheme(ColorLevel(color, it)) }

fun getAllColorSchemes(level: Level) = Color.values().map { getColorScheme(ColorLevel(it, level)) }

fun getAllColorSchemes() = Color.values().map { color ->
    Level.values().map { level ->
        getColorScheme(ColorLevel(color, level))
    }
}.flatten()

fun getColorSchemeClosestToColor(color: WaqtiColor): ColorScheme {
    return getAllColorSchemes().sortedBy {
        it.main.toAndroidColor.compareTo(color.toAndroidColor)
    }.first()
}

private inline val String.c: WaqtiColor
    get() = toColor
