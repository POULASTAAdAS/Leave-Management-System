package com.poulastaa.lms.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.poulastaa.lms.R

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    url: String? = null,
    cookie: String,
    sex: String,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .size(140.dp)
            .clip(RoundedCornerShape(1000f))
            .border(
                width = 2.5.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(1000f)
            ),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .addHeader(
                    name = "Cookie",
                    value = cookie
                )
                .data(url)
                .crossfade(true)
                .error(if (sex == "M") R.drawable.ic_profile_male else R.drawable.ic_profile_female)
                .placeholder(if (sex == "M") R.drawable.ic_profile_male else R.drawable.ic_profile_female)
                .build(),
            contentDescription = null,
            clipToBounds = true,
            modifier = Modifier
                .clip(RoundedCornerShape(1000f))
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary),
            contentScale = ContentScale.Crop
        )
    }
}