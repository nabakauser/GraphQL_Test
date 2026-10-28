package com.android.graphqltest.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.graphqltest.domain.model.SimpleCountry

@Preview(showBackground = true)
@Composable
fun HomeCountriesPreview() {
    CountryCard(
        country = SimpleCountry(
            code = "IN",
            name = "India",
            capital = "Delhi",
            emoji = "🇮🇳"
        ),
        isFavorite = true,
        isVisited = true,
        onFavoriteClick = {},
        onVisitedClick = {},
        onClick = {}
    )
}

@Composable
fun HomeCountriesScreen(
    uiState: CountryUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onFavoriteClick: (SimpleCountry) -> Unit = {},
    onVisitedClick: (SimpleCountry) -> Unit = {},
    onCountryClick: (SimpleCountry) -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Loading State
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            // Error State
            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }

            // Empty State
            uiState.isEmpty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No countries available.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text(text = "Refresh")
                    }
                }
            }

            // Success State
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.countries,
                        key = { it.code }
                    ) { country ->
                        var isFavorite by remember { mutableStateOf(value = false) }
                        var isVisited by remember { mutableStateOf(value = false) }

                        CountryCard(
                            country = country,
                            isFavorite = isFavorite,
                            isVisited = isVisited,
                            onFavoriteClick = {
                                isFavorite = !isFavorite
                                onFavoriteClick(country)
                            },
                            onVisitedClick = {
                                isVisited = !isVisited
                                onVisitedClick(country)
                            },
                            onClick = { onCountryClick(country) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CountryCard(
    country: SimpleCountry,
    isFavorite: Boolean,
    isVisited: Boolean,
    onFavoriteClick: () -> Unit,
    onVisitedClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = country.emoji ?: "",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = country.capital ?: "No capital",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onVisitedClick) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = if (isVisited) "Mark as not visited" else "Mark as visited",
                    tint = if (isVisited) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
K                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favourites" else "Add to favourites",
                    tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}