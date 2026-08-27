package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess

@Composable
fun InspectionPhotoSelector(
    title: String,
    frontPhoto: String,
    backPhoto: String,
    leftPhoto: String,
    rightPhoto: String,
    onPhotoSelected: (angle: String, photoTag: String) -> Unit,
    onConfirmAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isComplete = frontPhoto.isNotEmpty() && backPhoto.isNotEmpty() && leftPhoto.isNotEmpty() && rightPhoto.isNotEmpty()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = if (isComplete) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isComplete) "4/4 Captured" else "Condition Check",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isComplete) EmeraldSuccess else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Capture 4 angles before handover to protect deposit & establish return baseline:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PhotoSlot(
                    label = "Front 📸",
                    photoVal = frontPhoto,
                    onTap = { onPhotoSelected("front", "photo_front_captured") },
                    tag = "photo_slot_front"
                )
                PhotoSlot(
                    label = "Back 📸",
                    photoVal = backPhoto,
                    onTap = { onPhotoSelected("back", "photo_back_captured") },
                    tag = "photo_slot_back"
                )
                PhotoSlot(
                    label = "Left Side 📸",
                    photoVal = leftPhoto,
                    onTap = { onPhotoSelected("left", "photo_left_captured") },
                    tag = "photo_slot_left"
                )
                PhotoSlot(
                    label = "Right Side 📸",
                    photoVal = rightPhoto,
                    onTap = { onPhotoSelected("right", "photo_right_captured") },
                    tag = "photo_slot_right"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirmAll,
                enabled = isComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_inspection_photos_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldSuccess
                )
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Confirm")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Verify & Seal Condition Record", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PhotoSlot(
    label: String,
    photoVal: String,
    onTap: () -> Unit,
    tag: String
) {
    val isCaptured = photoVal.isNotEmpty()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isCaptured) EmeraldSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = if (isCaptured) EmeraldSuccess else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onTap() }
                .testTag(tag),
            contentAlignment = Alignment.Center
        ) {
            if (isCaptured) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = label,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCaptured) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
