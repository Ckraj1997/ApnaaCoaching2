package com.chandan.apnaacoaching.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chandan.apnaacoaching.data.CarouselImage
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(images: List<CarouselImage>) {

    if (images.isEmpty()) return
    val pageCount = images.size
    val pagerState = rememberPagerState(pageCount = { pageCount })
//    LaunchedEffect(pagerState.currentPage) {
//        delay(3000.milliseconds)
//        val nextPage = (pagerState.currentPage + 1) % pageCount
//        pagerState.animateScrollToPage(nextPage)
//    }

    // THE FIX: Run a continuous loop that doesn't cancel when the page changes
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000.milliseconds)

            // Only auto-scroll if the user isn't currently dragging the carousel
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) { page ->

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[page].imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Promo Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            Modifier
                .height(30.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}
