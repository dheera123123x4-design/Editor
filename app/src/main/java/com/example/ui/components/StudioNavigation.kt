package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioCyanAccent
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioPrimaryViolet
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.viewmodel.StudioTab

@Composable
fun StudioBottomNav(
    activeTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("studio_bottom_nav"),
        containerColor = StudioSurface,
        contentColor = StudioTextPrimary
    ) {
        StudioNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            selected = activeTab == StudioTab.HOME,
            tag = "nav_home",
            onClick = { onTabSelected(StudioTab.HOME) }
        )

        StudioNavItem(
            label = "AI Tools",
            icon = Icons.Default.AutoAwesome,
            selected = activeTab == StudioTab.AI_TOOLS,
            tag = "nav_ai_tools",
            onClick = { onTabSelected(StudioTab.AI_TOOLS) }
        )

        StudioNavItem(
            label = "Projects",
            icon = Icons.Default.Folder,
            selected = activeTab == StudioTab.PROJECTS,
            tag = "nav_projects",
            onClick = { onTabSelected(StudioTab.PROJECTS) }
        )

        StudioNavItem(
            label = "Templates",
            icon = Icons.Default.ViewCarousel,
            selected = activeTab == StudioTab.TEMPLATES,
            tag = "nav_templates",
            onClick = { onTabSelected(StudioTab.TEMPLATES) }
        )

        StudioNavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            selected = activeTab == StudioTab.PROFILE,
            tag = "nav_profile",
            onClick = { onTabSelected(StudioTab.PROFILE) }
        )
    }
}

@Composable
private fun RowScope.StudioNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = StudioCyanAccent,
            indicatorColor = StudioPrimaryViolet,
            unselectedIconColor = StudioTextMuted,
            unselectedTextColor = StudioTextMuted
        ),
        modifier = Modifier.testTag(tag)
    )
}
