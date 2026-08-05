package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.GlassBorderLight
import com.example.todoapp.ui.theme.GlassSurface

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = GlassSurface.copy(alpha = 0.15f),
    borderColor: Color = GlassBorderLight,
    cornerRadius: Int = 16,
    elevation: Int = 4,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(cornerRadius.dp)
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}
