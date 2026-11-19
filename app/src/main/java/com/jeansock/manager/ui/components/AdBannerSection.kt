package com.jeansock.manager.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeansock.manager.R
import com.jeansock.manager.ui.theme.AccentRed
import com.jeansock.manager.ui.theme.TextLightGray
import com.jeansock.manager.ui.theme.TextPrimaryBlack
import com.jeansock.manager.ui.theme.VazirFontFamily

@Composable
fun AdBannerSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .height(48.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right side in RTL: Thumbnail image (48x48, cornerRadius 8dp) + Texts
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ad_banner_thumb),
                contentDescription = "تبلیغ سریال یل",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "قسمت جدید سریال یل رسید",
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TextPrimaryBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "برای تماشای رایگان کلیک کنید",
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp,
                    color = TextLightGray
                )
            }
        }

        // Left side in RTL: "تبلیغ" badge
        Text(
            text = "تبلیغ",
            fontFamily = VazirFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 9.sp,
            color = AccentRed,
            modifier = Modifier.padding(top = 7.dp)
        )
    }
}
