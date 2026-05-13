package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.team16.ui.common.BackButton
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

@Composable
fun FromMenuDefaultScreen(
    header: String,
    body: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(
                start = Team16Theme.padding.outerMedium,
                end = Team16Theme.padding.outerMedium,
                top = Team16Theme.padding.outerMedium
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(
            alignment = Alignment.CenterStart,
            textColor =MaterialTheme.colorScheme.onSurface,
            onNavigateBack = onNavigateBack

        )
        Spacer(modifier=Modifier.height(Team16Theme.padding.outerMedium))
        Text(
            text = header,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier=Modifier.height(Team16Theme.padding.outerMedium))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        content()

    }
}