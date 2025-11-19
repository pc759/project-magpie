package com.spotitworld.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotitworld.R

@Composable
fun MainMenuScreen(
    onStartNewHunt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title
        Text(
            text = "Magpie",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Start New Hunt Button (Enabled)
        Button(
            onClick = onStartNewHunt,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.start_new_hunt),
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }

        // Load Previous Hunt Button (Disabled)
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = stringResource(R.string.load_previous_hunt),
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }

        // Join Shared Hunt Button (Disabled)
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = stringResource(R.string.join_shared_hunt),
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }

        // Options Button (Disabled)
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = stringResource(R.string.options),
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }
    }
}