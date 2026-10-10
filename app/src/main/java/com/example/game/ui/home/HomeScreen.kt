package com.example.game.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.R
import com.example.game.model.QuizCategory
import com.example.game.ui.components.CircularProgressBadge
import com.example.game.ui.components.CoinBadge
import com.example.game.ui.components.SettingsDialog
import com.example.game.theme.CardBackgroundWhite
import com.example.game.theme.CardBorderStroke
import com.example.game.theme.CoinPillPurple
import com.example.game.theme.QuizHomeBackground
import com.example.game.theme.TextDarkNavy
import com.example.game.theme.TextMuted

import com.example.game.model.SchoolCatalog
import com.example.game.ui.components.SchoolSelectionDialog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartQuiz: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSchoolDialog by remember { mutableStateOf(false) }
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    if (showSchoolDialog) {
        SchoolSelectionDialog(
            currentSchoolId = userProfile.schoolId,
            onSelectSchool = { school ->
                viewModel.selectSchool(school)
            },
            onDismiss = { showSchoolDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentSettings = settings,
            userProfile = userProfile,
            onSaveSettings = { sound, timer ->
                viewModel.updateSettings(sound, timer)
            },
            onChangeSchool = {
                showSettingsDialog = false
                showSchoolDialog = true
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuizHomeBackground,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = selectedBottomTab,
                onTabSelected = { selectedBottomTab = it }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header: Greeting and Coins
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hello, ${userProfile.name}",
                                color = TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            // Interactive School Pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFEDE9FE))
                                    .clickable { showSchoolDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${userProfile.school} ▾",
                                    color = CoinPillPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showSettingsDialog = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_settings),
                                    contentDescription = "Settings",
                                    tint = CoinPillPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            CoinBadge(coins = userProfile.coins)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = when (selectedBottomTab) {
                            1 -> "NSMQ Rounds Mode 🎯"
                            2 -> "National Hall of Fame 🏆"
                            else -> "National Science & Maths Quiz"
                        },
                        color = TextDarkNavy,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp
                    )
                }
            }

            when (selectedBottomTab) {
                0 -> {
                    // TAB 0: HOME FEED
                    // Featured Carousel
                    item {
                        FeaturedCarouselSection(
                            categories = viewModel.featuredCategories,
                            onCategoryClick = { onStartQuiz(it.id) }
                        )
                    }

                    // Subjects & Rounds Header
                    item {
                        Text(
                            text = "NSMQ Subjects & Practice",
                            color = TextDarkNavy,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }

                    // Subjects List
                    items(viewModel.unfinishedCategories) { category ->
                        UnfinishedGameCard(
                            category = category,
                            onClick = { onStartQuiz(category.id) }
                        )
                    }
                }
                1 -> {
                    // TAB 1: ALL ROUNDS MODE
                    item {
                        Text(
                            text = "Compete through all 5 official rounds of the National Science & Maths Quiz:",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }

                    items(viewModel.roundsCategories) { category ->
                        UnfinishedGameCard(
                            category = category,
                            onClick = { onStartQuiz(category.id) }
                        )
                    }
                }
                2 -> {
                    // TAB 2: HALL OF FAME / LEADERBOARD
                    item {
                        // User's School Spotlight Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.5.dp, CoinPillPurple, RoundedCornerShape(20.dp)),
                            color = Color(0xFFF8F5FF)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, CardBorderStroke, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = SchoolCatalog.findSchool(userProfile.schoolId)?.emoji ?: "🎓",
                                        fontSize = 26.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Your School: ${userProfile.school}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextDarkNavy
                                    )
                                    Text(
                                        text = userProfile.schoolMoniker,
                                        fontSize = 13.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "Your contribution: +${userProfile.coins} points",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CoinPillPurple,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "All-Time Championship Table",
                            color = TextDarkNavy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    val sortedSchools = SchoolCatalog.schools.sortedByDescending { it.titles }
                    items(sortedSchools) { school ->
                        val isUserSchool = school.id == userProfile.schoolId
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isUserSchool) Color(0xFFF3F0FF) else CardBackgroundWhite,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isUserSchool) 1.5.dp else 1.dp,
                                    color = if (isUserSchool) CoinPillPurple else CardBorderStroke,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, CardBorderStroke, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = school.emoji, fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = school.shortName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextDarkNavy
                                    )
                                    Text(
                                        text = "${school.moniker} • ${school.region}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }

                                if (school.titles > 0) {
                                    Text(
                                        text = "🏆 ${school.titles}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color(0xFFD97706)
                                    )
                                } else {
                                    Text(
                                        text = "Contender",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FeaturedCarouselSection(
    categories: List<QuizCategory>,
    onCategoryClick: (QuizCategory) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { categories.size })

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(end = 40.dp),
            pageSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val category = categories[page]
            FeaturedCard(
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pager Indicators
        Row(
            modifier = Modifier.padding(start = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(categories.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label = "IndicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(width)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isSelected) CoinPillPurple else Color(0xFFD6DBEC))
                )
            }
        }
    }
}

@Composable
fun FeaturedCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1F000000))
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = CardBackgroundWhite
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF3F5FA)),
                contentAlignment = Alignment.Center
            ) {
                category.bannerRes?.let { banner ->
                    Image(
                        painter = painterResource(id = banner),
                        contentDescription = category.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Bottom Metadata
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = category.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkNavy
                    )
                    Text(
                        text = category.subtitle,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { category.progressPercentage / 100f },
                    modifier = Modifier
                        .width(80.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(category.ringColorHex),
                    trackColor = Color(category.ringColorHex).copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun UnfinishedGameCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .border(1.dp, CardBorderStroke, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = CardBackgroundWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Image(
                painter = painterResource(id = category.iconRes),
                contentDescription = category.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )
                Text(
                    text = "${category.subtitle} • ${category.totalQuestions} Qs",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            // Ring Badge
            CircularProgressBadge(
                percentage = category.progressPercentage,
                ringColor = Color(category.ringColorHex),
                size = 48.dp,
                strokeWidth = 3.5.dp
            )
        }
    }
}

@Composable
fun HomeBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Subjects
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(0) }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_puzzle),
                    contentDescription = "Subjects",
                    tint = if (selectedTab == 0) CoinPillPurple else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Subjects",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 0) CoinPillPurple else Color(0xFF94A3B8)
                )
            }

            // Tab 1: Rounds Mode
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(1) }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dice),
                    contentDescription = "Rounds",
                    tint = if (selectedTab == 1) CoinPillPurple else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Rounds",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 1) CoinPillPurple else Color(0xFF94A3B8)
                )
            }

            // Tab 2: Hall of Fame
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(2) }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_award),
                    contentDescription = "Standings",
                    tint = if (selectedTab == 2) CoinPillPurple else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Standings",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 2) CoinPillPurple else Color(0xFF94A3B8)
                )
            }
        }
    }
}
