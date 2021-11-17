package com.codelab.theming.ui.start.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codelab.theming.R
import com.codelab.theming.ui.finish.theme.JetnewsShapes
import com.codelab.theming.ui.finish.theme.JetnewsTypography
import com.codelab.theming.ui.finish.theme.Red200
import com.codelab.theming.ui.finish.theme.Red300


private val DarkColors = darkColors(
    primary = Red300,
    primaryVariant = com.codelab.theming.ui.finish.theme.Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.Black,
    error = Red200
)

private val LightColors = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    onPrimary = Color.White,
    secondary = Red700,
    secondaryVariant = Red900,
    onSecondary = Color.White,
    error = Red800
)

@Composable
fun JetnewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit) {

    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        typography = JetnewsTypography,
        shapes = JetnewsShapes,
        content = content
    )
}
