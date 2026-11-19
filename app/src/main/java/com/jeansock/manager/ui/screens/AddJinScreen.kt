package com.jeansock.manager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeansock.manager.R
import com.jeansock.manager.ui.components.AdBannerSection
import com.jeansock.manager.ui.components.AppHeaderCard
import com.jeansock.manager.ui.theme.AccentRed
import com.jeansock.manager.ui.theme.BackButtonGray
import com.jeansock.manager.ui.theme.BackgroundGray
import com.jeansock.manager.ui.theme.BorderGray
import com.jeansock.manager.ui.theme.CalendarCellGray
import com.jeansock.manager.ui.theme.CardWhite
import com.jeansock.manager.ui.theme.PrimaryGradientEnd
import com.jeansock.manager.ui.theme.PrimaryGradientStart
import com.jeansock.manager.ui.theme.TextDarkGray
import com.jeansock.manager.ui.theme.TextHintGray
import com.jeansock.manager.ui.theme.TextLightGray
import com.jeansock.manager.ui.theme.TextMediumGray
import com.jeansock.manager.ui.theme.TextPrimaryBlack
import com.jeansock.manager.ui.theme.VazirFontFamily
import com.jeansock.manager.util.JalaliCalendarHelper
import com.jeansock.manager.util.JalaliDate
import com.jeansock.manager.util.NumberFormatter

@Composable
fun AddJinScreen(
    onAddConfirmed: (Int, JalaliDate) -> Unit,
    onBackToHome: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val today = remember { JalaliCalendarHelper.today() }

    var countInput by remember { mutableStateOf("") }
    var displayedYear by remember { mutableIntStateOf(today.year) }
    var displayedMonth by remember { mutableIntStateOf(today.month) }
    var selectedDate by remember { mutableStateOf(today) }

    val primaryGradient = Brush.verticalGradient(
        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // 1. Top Header Card
        AppHeaderCard(
            title = "افزودن جین جدید",
            onSettingsClick = onSettingsClick
        )

        // 2. Scrollable Form + Jalali Calendar + Action Buttons
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Label: "تعداد جین"
            Text(
                text = "تعداد جین",
                fontFamily = VazirFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = TextMediumGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Input Box (48dp height, 1dp #E5E7EB border, 8dp radius)
            BasicTextField(
                value = countInput,
                onValueChange = { raw ->
                    val normalized = NumberFormatter.normalizeDigits(raw)
                    if (normalized.all { it.isDigit() } && normalized.length <= 7) {
                        countInput = normalized
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TextPrimaryBlack,
                    textAlign = TextAlign.Left
                ),
                cursorBrush = SolidColor(PrimaryGradientStart),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (countInput.isEmpty()) {
                            Text(
                                text = "مثال: 10",
                                fontFamily = VazirFontFamily,
                                fontSize = 13.sp,
                                color = TextHintGray
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Label: "انتخاب تاریخ"
            Text(
                text = "انتخاب تاریخ",
                fontFamily = VazirFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = TextMediumGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Jalali Calendar Box (1dp #E5E7EB border, 8dp radius, 16dp internal padding)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardWhite)
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                // Month Navigation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right button in RTL: Previous Month
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CalendarCellGray)
                            .clickable {
                                if (displayedMonth == 1) {
                                    displayedMonth = 12
                                    displayedYear -= 1
                                } else {
                                    displayedMonth -= 1
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = "ماه قبل",
                            tint = TextDarkGray,
                            modifier = Modifier.size(width = 9.dp, height = 16.dp)
                        )
                    }

                    Text(
                        text = "${JalaliCalendarHelper.monthName(displayedMonth)} $displayedYear",
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = TextPrimaryBlack
                    )

                    // Left button in RTL: Next Month
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CalendarCellGray)
                            .clickable {
                                if (displayedMonth == 12) {
                                    displayedMonth = 1
                                    displayedYear += 1
                                } else {
                                    displayedMonth += 1
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = "ماه بعد",
                            tint = TextDarkGray,
                            modifier = Modifier.size(width = 9.dp, height = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weekday Headers Row (ش to ج, 8dp spacing)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JalaliCalendarHelper.WEEKDAY_SHORT_NAMES.forEachIndexed { index, label ->
                        val isFriday = index == 6
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFriday) AccentRed else TextLightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = VazirFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Month Days Grid (8dp spacing)
                val firstWeekdayOffset = JalaliCalendarHelper.persianWeekdayIndex(
                    displayedYear,
                    displayedMonth,
                    1
                )
                val totalDays = JalaliCalendarHelper.daysInJalaliMonth(
                    displayedYear,
                    displayedMonth
                )
                val totalCells = firstWeekdayOffset + totalDays
                val rowCount = (totalCells + 6) / 7

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in 0 until rowCount) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNumber = cellIndex - firstWeekdayOffset + 1
                                if (dayNumber in 1..totalDays) {
                                    val cellDate = JalaliDate(displayedYear, displayedMonth, dayNumber)
                                    val isToday = cellDate == today
                                    val isSelected = cellDate == selectedDate
                                    val isFriday = col == 6

                                    val cellModifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .then(
                                            when {
                                                isSelected || isToday -> Modifier.background(primaryGradient)
                                                isFriday -> Modifier.background(AccentRed)
                                                else -> Modifier.background(CalendarCellGray)
                                            }
                                        )
                                        .clickable {
                                            selectedDate = cellDate
                                        }

                                    val textColor = when {
                                        isSelected || isToday || isFriday -> Color.White
                                        else -> TextDarkGray
                                    }

                                    Box(
                                        modifier = cellModifier,
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayNumber.toString(),
                                            fontFamily = VazirFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = textColor
                                        )
                                    }
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = BorderGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Calendar Footer: "برو به امروز" button (Right) & Formatted Selected Date (Left)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryGradientStart)
                            .clickable {
                                val now = JalaliCalendarHelper.today()
                                displayedYear = now.year
                                displayedMonth = now.month
                                selectedDate = now
                            }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "برو به امروز",
                            fontFamily = VazirFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = selectedDate.formatWeekdayDayMonth(),
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = TextHintGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Action Buttons Row: "برگشت" & "افزودن" (aligned to End/Left in RTL, 8dp gap)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "برگشت" button (130dp x 48dp, #E0E3E8)
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackButtonGray)
                        .clickable { onBackToHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "برگشت",
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = TextMediumGray
                    )
                }

                // "افزودن" button (130dp x 48dp, gradient #667EEA -> #764BA2)
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryGradient)
                        .clickable {
                            val dozen = NumberFormatter.parseIntSafe(countInput)
                            if (dozen <= 0) {
                                Toast.makeText(
                                    context,
                                    "لطفاً تعداد جین معتبر وارد کنید",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                onAddConfirmed(dozen, selectedDate)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "افزودن",
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Bottom Ad Banner
        AdBannerSection()
    }
}
