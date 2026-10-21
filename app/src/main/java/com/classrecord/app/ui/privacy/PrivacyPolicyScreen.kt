package com.classrecord.app.ui.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.classrecord.app.R
import com.classrecord.app.i18n.AppLanguage
import com.classrecord.app.i18n.LocaleApplier
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val english = remember {
        when (LocaleApplier.readAppliedLanguage(context)) {
            AppLanguage.EN -> true
            AppLanguage.ZH -> false
            AppLanguage.SYSTEM ->
                context.resources.configuration.locales[0].language.startsWith("en")
        }
    }
    val body = remember(english) {
        val resId = if (english) R.raw.privacy_policy_en else R.raw.privacy_policy_zh
        context.resources.openRawResource(resId).bufferedReader(StandardCharsets.UTF_8).readText()
    }
    val webUrl = stringResource(
        if (english) R.string.privacy_policy_web_url_en else R.string.privacy_policy_web_url_zh,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.privacy_policy_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { uriHandler.openUri(webUrl) }) {
                Text(stringResource(R.string.privacy_policy_open_web))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
