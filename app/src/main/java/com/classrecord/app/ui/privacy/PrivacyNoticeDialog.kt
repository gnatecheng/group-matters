package com.classrecord.app.ui.privacy

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.classrecord.app.R

@Composable
fun PrivacyNoticeDialog(
    onDismiss: () -> Unit,
    onReadFullPolicy: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.privacy_notice_title)) },
        text = { Text(stringResource(R.string.privacy_notice_body)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.privacy_notice_got_it))
            }
        },
        dismissButton = {
            TextButton(onClick = onReadFullPolicy) {
                Text(stringResource(R.string.privacy_notice_read_full))
            }
        },
    )
}
