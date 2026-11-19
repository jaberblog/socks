package com.jeansock.manager.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeansock.manager.R
import com.jeansock.manager.ui.theme.BackgroundGray
import com.jeansock.manager.ui.theme.TextPrimaryBlack
import com.jeansock.manager.ui.theme.VazirFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(5000L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // Centered 1:1 Sock Image with exact 24dp horizontal margin
        Image(
            painter = painterResource(id = R.drawable.splash_sock),
            contentDescription = "جوراب شمس",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .aspectRatio(1f)
        )

        // Bottom slogan with exact 24dp bottom margin
        Text(
            text = "محاسبه جین جوراب هات با من",
            fontFamily = VazirFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = TextPrimaryBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
