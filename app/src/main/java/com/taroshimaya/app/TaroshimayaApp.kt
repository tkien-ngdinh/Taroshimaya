package com.taroshimaya.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.taroshimaya.app.core.data.AppLanguagePreference
import com.taroshimaya.app.core.designsystem.TaroshimayaTheme
import com.taroshimaya.app.core.di.AppContainer
import com.taroshimaya.app.core.navigation.AppNavHost
import com.taroshimaya.app.core.navigation.AppRoute
import com.taroshimaya.app.core.ui.LanguagePickerDialog

@Composable
fun TaroshimayaApp(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
) {
    val languagePreference by appContainer.languageManager.preference.collectAsStateWithLifecycle()

    TaroshimayaTheme {
        TaroshimayaShell(
            languagePreference = languagePreference,
            onSelectLanguage = appContainer.languageManager::selectLanguage,
            modifier = modifier,
        )
    }
}

@Composable
fun TaroshimayaShell(
    languagePreference: AppLanguagePreference,
    onSelectLanguage: (AppLanguagePreference) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    AppNavHost(navController = navController) { selectedRoute, onSelectRoute, destinationContent ->
        TaroshimayaScaffold(
            languagePreference = languagePreference,
            onSelectLanguage = onSelectLanguage,
            selectedRoute = selectedRoute,
            onSelectRoute = onSelectRoute,
            modifier = modifier,
            destinationContent = destinationContent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaroshimayaScaffold(
    languagePreference: AppLanguagePreference,
    onSelectLanguage: (AppLanguagePreference) -> Unit,
    selectedRoute: AppRoute?,
    onSelectRoute: (AppRoute) -> Unit,
    modifier: Modifier = Modifier,
    destinationContent: @Composable (Modifier) -> Unit,
) {
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                text = stringResource(R.string.shell_version),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = stringResource(R.string.shell_language_button_desc),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                AppRoute.topLevelRoutes.forEach { destination ->
                    val label = stringResource(destination.labelRes)
                    NavigationBarItem(
                        selected = selectedRoute == destination,
                        onClick = { onSelectRoute(destination) },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = label,
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        destinationContent(Modifier.padding(innerPadding))
    }

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentPreference = languagePreference,
            onSelectPreference = { selected ->
                showLanguageDialog = false
                onSelectLanguage(selected)
            },
            onDismiss = { showLanguageDialog = false },
        )
    }
}

@Preview(showBackground = true, locale = "vi")
@Composable
private fun TaroshimayaShellPreviewVi() {
    TaroshimayaTheme {
        TaroshimayaShell(
            languagePreference = AppLanguagePreference.SYSTEM,
            onSelectLanguage = {},
        )
    }
}

@Preview(showBackground = true, locale = "en")
@Composable
private fun TaroshimayaShellPreviewEn() {
    TaroshimayaTheme {
        TaroshimayaShell(
            languagePreference = AppLanguagePreference.ENGLISH,
            onSelectLanguage = {},
        )
    }
}
