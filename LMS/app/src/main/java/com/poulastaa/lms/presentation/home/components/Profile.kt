package com.poulastaa.lms.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.poulastaa.lms.ui.theme.ProfileFemaleIcon
import com.poulastaa.lms.ui.theme.ProfileMaleIcon
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    url: String? = null,
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
        if (url == null)
            Image(
                modifier = Modifier
                    .then(if (sex == "F") Modifier.padding(MaterialTheme.dimens.small3) else Modifier)
                    .clip(RoundedCornerShape(1000f))
                    .fillMaxSize(),
                imageVector = if (sex == "M") ProfileMaleIcon else ProfileFemaleIcon,
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
        else {
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .then(if (sex == "F") Modifier.padding(MaterialTheme.dimens.small3) else Modifier)
                    .clip(RoundedCornerShape(1000f))
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
    }
}