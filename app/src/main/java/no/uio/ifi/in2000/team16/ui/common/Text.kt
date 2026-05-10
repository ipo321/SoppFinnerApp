package no.uio.ifi.in2000.team16.ui.common



import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

@Composable
fun WarningBanner(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    linkText: String? = null,
    onLinkClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = MaterialTheme.shapes.large
            ),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(Team16Theme.padding.outer)
                .then(
                    if (onLinkClick != null) Modifier.clickable { onLinkClick() }
                    else Modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(Team16Theme.padding.inner))
            Column {
                Text(
                    text = text,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                if (linkText != null && onLinkClick != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = linkText,
                            style = textStyle,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textDecoration = TextDecoration.Underline
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
