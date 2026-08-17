package com.faveit.app.ui.components

import android.view.SoundEffectConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faveit.app.model.GemPalette
import com.faveit.app.ui.theme.colors

@Composable
fun GemTile(
    title: String,
    subtitle: String,
    emoji: String,
    palette: GemPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    editable: Boolean = false,
    muted: Boolean = false,
    compact: Boolean = false,
    selectionMode: Boolean = false,
    favoriteState: Boolean? = null,
    onRemove: (() -> Unit)? = null,
    removeLabel: String = "Remove favorite",
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        when {
            pressed -> 0.965f
            selected && selectionMode -> 1.012f
            else -> 1f
        },
        label = "gem press and selection",
    )
    val borderWidth by animateDpAsState(
        if (selected && selectionMode) 2.dp else 1.dp,
        label = "gem selection border",
    )
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val gems = palette.colors()
    val tileShape = RoundedCornerShape(if (compact) 18.dp else 22.dp)
    val activate = {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        view.playSoundEffect(SoundEffectConstants.CLICK)
        onClick()
    }
    val removeWithFeedback = {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        view.playSoundEffect(SoundEffectConstants.CLICK)
        onRemove?.invoke()
        Unit
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = if (pressed) 3.dp.toPx() else 0f
                shadowElevation = when {
                    pressed -> 2.dp.toPx()
                    selected && selectionMode -> 15.dp.toPx()
                    else -> 10.dp.toPx()
                }
                shape = tileShape
                clip = false
            }
            .clip(tileShape)
            .background(
                Brush.linearGradient(
                    colors = if (muted) {
                        listOf(Color(0xFF303442), Color(0xFF4B5160), Color(0xFF252A36))
                    } else listOf(gems.light, gems.base, gems.dark),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
            )
            .border(
                borderWidth,
                when {
                    muted -> Color.White.copy(alpha = 0.12f)
                    selected && selectionMode -> gems.glint.copy(alpha = 0.92f)
                    else -> Color.White.copy(alpha = 0.42f)
                },
                tileShape,
            )
            .then(
                if (selectionMode) {
                    Modifier.toggleable(
                        value = selected,
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Checkbox,
                        onValueChange = { activate() },
                    )
                } else {
                    Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = activate,
                    )
                },
            )
            .semantics {
                favoriteState?.let {
                    stateDescription = if (it) "Favorite" else "Not a favorite"
                }
            }
            .alpha(if (muted) 0.74f else 1f),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val facet = Path().apply {
                moveTo(size.width * 0.44f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.30f)
                close()
            }
            drawPath(facet, Color.White.copy(alpha = if (muted) 0.035f else 0.14f))
            drawLine(
                color = Color.White.copy(alpha = if (muted) 0.05f else 0.20f),
                start = Offset(size.width * 0.05f, size.height * 0.12f),
                end = Offset(size.width * 0.64f, size.height * 0.02f),
                strokeWidth = 2.dp.toPx(),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = if (muted) 0.04f else 0.10f),
                topLeft = Offset(size.width * 0.03f, size.height * 0.04f),
                size = size.copy(width = size.width * 0.94f, height = size.height * 0.92f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                style = Stroke(1.dp.toPx()),
            )
        }

        Column(
            Modifier.fillMaxSize().padding(if (compact) 11.dp else 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(emoji, fontSize = if (compact) 25.sp else 31.sp)
                when {
                    onRemove != null -> Spacer(Modifier.size(38.dp))
                    editable -> Icon(
                        Icons.Rounded.Edit,
                        "Customize",
                        tint = Color.White.copy(alpha = 0.82f),
                    )
                    selected -> Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color.White),
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            if (selectionMode) null else "Selected",
                            tint = gems.dark,
                            modifier = Modifier.padding(3.dp),
                        )
                    }
                }
            }
            Column {
                Text(
                    title,
                    color = Color.White,
                    fontSize = if (compact) 16.sp else 18.sp,
                    lineHeight = if (compact) 17.sp else 20.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle.uppercase(),
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp,
                    maxLines = 1,
                )
            }
        }

        if (onRemove != null) {
            FilledIconButton(
                onClick = removeWithFeedback,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(44.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFD43B55),
                    contentColor = Color.White,
                ),
            ) {
                Icon(Icons.Rounded.Close, removeLabel)
            }
        }
    }
}
