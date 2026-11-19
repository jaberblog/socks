package com.jeansock.manager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeansock.manager.R
import com.jeansock.manager.model.DashboardStats
import com.jeansock.manager.model.JinEntry
import com.jeansock.manager.model.PeriodStat
import com.jeansock.manager.ui.components.AdBannerSection
import com.jeansock.manager.ui.components.AppHeaderCard
import com.jeansock.manager.ui.components.FloatingPlusButton
import com.jeansock.manager.ui.theme.AccentRed
import com.jeansock.manager.ui.theme.BackgroundGray
import com.jeansock.manager.ui.theme.CardWhite
import com.jeansock.manager.ui.theme.ProfitGreen
import com.jeansock.manager.ui.theme.SoftRedCircle
import com.jeansock.manager.ui.theme.TextDarkGray
import com.jeansock.manager.ui.theme.TextHintGray
import com.jeansock.manager.ui.theme.TextLightGray
import com.jeansock.manager.ui.theme.TextPrimaryBlack
import com.jeansock.manager.ui.theme.TodayGradientEnd
import com.jeansock.manager.ui.theme.TodayGradientStart
import com.jeansock.manager.ui.theme.VazirFontFamily
import com.jeansock.manager.util.NumberFormatter

@Composable
fun StartScreen(
    entries: List<JinEntry>,
    stats: DashboardStats,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDeleteEntry: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // 1. Top Header Card
        AppHeaderCard(
            title = "مدیریت جین جوراب",
            onSettingsClick = onSettingsClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 2x2 Summary Cards Grid (exact 16dp outer margin, 16dp horizontal & vertical gaps)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "تعداد امروز",
                    stat = stats.today,
                    isHighlighted = true,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "تعداد دیروز",
                    stat = stats.yesterday,
                    isHighlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "تعداد هفته",
                    stat = stats.week,
                    isHighlighted = false,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "تعداد کل",
                    stat = stats.total,
                    isHighlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. List Header Row ("لیست جین ها:" & "X مورد")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "لیست جین ها:",
                fontFamily = VazirFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = TextDarkGray
            )
            Text(
                text = "${entries.size} مورد",
                fontFamily = VazirFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = TextHintGray
            )
        }

        // 4. Main Content Area + Floating Plus Button Overlay
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (entries.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sock),
                        contentDescription = null,
                        tint = TextHintGray,
                        modifier = Modifier.size(width = 85.dp, height = 96.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "هنوز جینی اضافه نشده است",
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = TextHintGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = entries,
                        key = { it.id }
                    ) { entry ->
                        JinListItemCard(
                            entry = entry,
                            onDeleteClick = { onDeleteEntry(entry.id) }
                        )
                    }
                }
            }

            // Floating Plus Button at bottom-left (End in RTL) with 16dp margin
            FloatingPlusButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            )
        }

        // 5. Bottom Ad Banner
        AdBannerSection()
    }
}

@Composable
private fun StatCard(
    title: String,
    stat: PeriodStat,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(TodayGradientStart, TodayGradientEnd)
    )
    val titleColor = if (isHighlighted) Color.White else TextPrimaryBlack
    val countColor = if (isHighlighted) Color.White else TextPrimaryBlack
    val profitColor = if (isHighlighted) Color.White else ProfitGreen

    Surface(
        modifier = modifier.height(98.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isHighlighted) Color.Transparent else CardWhite,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isHighlighted) Modifier.background(gradientBrush)
                    else Modifier.background(CardWhite)
                )
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = titleColor
                )
                Text(
                    text = NumberFormatter.formatWithCommas(stat.count),
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = countColor
                )
                Text(
                    text = "سود: ${NumberFormatter.formatWithCommas(stat.profit)} تومان",
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = profitColor
                )
            }
        }
    }
}

@Composable
private fun JinListItemCard(
    entry: JinEntry,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(81.dp),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right side in RTL: Count, Jalali Date, Profit
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "${NumberFormatter.formatWithCommas(entry.dozenCount)} جین جوراب",
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = TextPrimaryBlack
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = null,
                        tint = TextLightGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = entry.formattedJalaliDate(),
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = TextLightGray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = null,
                        tint = ProfitGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سود: ${NumberFormatter.formatWithCommas(entry.totalProfit)} تومان",
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = ProfitGreen
                    )
                }
            }

            // Left side in RTL: Circular delete button (36dp, #FEE2E2, #DC2626 trash icon)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SoftRedCircle)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = "حذف",
                    tint = AccentRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
