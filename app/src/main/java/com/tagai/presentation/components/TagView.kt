package com.tagai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class TagShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadius = with(density) { 8.dp.toPx() }
        val pointWidth = with(density) { 16.dp.toPx() }
        val holeRadius = with(density) { 3.dp.toPx() }

        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(0f, size.height / 2f)
            lineTo(pointWidth, 0f)
            lineTo(size.width - cornerRadius, 0f)
            quadraticBezierTo(size.width, 0f, size.width, cornerRadius)
            lineTo(size.width, size.height - cornerRadius)
            quadraticBezierTo(size.width, size.height, size.width - cornerRadius, size.height)
            lineTo(pointWidth, size.height)
            close()

            val holeX = pointWidth / 1.5f
            val holeY = size.height / 2f
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = holeX - holeRadius,
                    top = holeY - holeRadius,
                    right = holeX + holeRadius,
                    bottom = holeY + holeRadius
                )
            )
        }
        return Outline.Generic(path)
    }
}

@Composable
fun TagView(
    text: String,
    isSelected: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = if (isSelected) {
            listOf(Color(0xFFFF6E9D), Color(0xFFA044FF)) // Pink to Purple gradient
        } else {
            listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD)) // Grey for unselected
        }
    )

    Box(
        modifier = modifier
            .run {
                if (onClick != null) clickable { onClick() } else this
            }
            .background(brush = gradientBrush, shape = TagShape())
            .padding(start = 24.dp, top = 6.dp, bottom = 6.dp, end = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF5D574D),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
