package com.bobon.mygametop.presentation.common

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.s

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GameCover(uriString: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uriString) {
        bitmap = null
        if (uriString == null) return@LaunchedEffect
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                val uri = android.net.Uri.parse(uriString)
                val resolver = context.contentResolver
                val sourceBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri)) { decoder, info, _ ->
                        val sample = maxOf(info.size.width, info.size.height) / 512
                        decoder.setTargetSampleSize(sample.coerceAtLeast(1))
                    }
                } else {
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                    var sample = 1
                    while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 1024) sample *= 2
                    resolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
                    }
                }
                sourceBitmap?.asImageBitmap()
            }.getOrNull()
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        bitmap?.let {
            Image(bitmap = it, contentDescription = s(R.string.cover_image), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } ?: Icon(
            Icons.Default.SportsEsports,
            contentDescription = if (uriString == null) s(R.string.no_cover) else s(R.string.cover_unavailable),
            modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
