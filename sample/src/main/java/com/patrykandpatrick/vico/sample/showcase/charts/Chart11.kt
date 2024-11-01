/*
 * Copyright 2024 by Patryk Goworowski and Patrick Michalik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatrick.vico.sample.showcase.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.databinding.Chart1Binding
import com.patrykandpatrick.vico.sample.showcase.UIFramework
import com.patrykandpatrick.vico.sample.showcase.rememberMarker
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun Chart11(uiFramework: UIFramework, modifier: Modifier) {
  val modelProducer = remember { CartesianChartModelProducer() }
  LaunchedEffect(Unit) {
    withContext(Dispatchers.Default) {
      modelProducer.runTransaction {
        /* Learn more:
        https://patrykandpatrick.com/vico/wiki/cartesian-charts/layers/line-layer#data. */
        lineSeries { series(x, x.map { Random.nextFloat() * 15 }) }
      }
    }
  }
  when (uiFramework) {
    UIFramework.Compose -> ComposeChart11(modelProducer, modifier)
    UIFramework.Views -> ViewChart11(modelProducer, modifier)
  }
}

@Composable
private fun ComposeChart11(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
  val marker = rememberMarker()
  val lineThickness = 16F
  val requiredPaddingForLine = lineThickness / 2F
  val pointSize = 32F
  val requiredPaddingForPoint = pointSize / 2F
  val requiredUnscalableLayerPadding = maxOf(requiredPaddingForPoint, requiredPaddingForLine).dp

  CartesianChartHost(
    chart =
    rememberCartesianChart(
      rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(
          LineCartesianLayer.rememberLine(
            remember { LineCartesianLayer.LineFill.single(fill(Color(0xffa485e0))) },
            pointProvider = LineCartesianLayer.PointProvider.single(
              point = LineCartesianLayer.Point(
                component = ShapeComponent(),
                sizeDp = pointSize,
              )
            )
          ),
        ),
      ),
      startAxis = VerticalAxis.rememberStart(),
      bottomAxis =
      HorizontalAxis.rememberBottom(
        guideline = null,
        itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
      ),
      marker = marker,
      layerPadding = cartesianLayerPadding(
        unscalableStart = requiredUnscalableLayerPadding,
        unscalableEnd = requiredUnscalableLayerPadding,
        unscalableTop = requiredUnscalableLayerPadding,
        unscalableBottom = requiredUnscalableLayerPadding,
      ),
    ),
    modelProducer = modelProducer,
    modifier = modifier,
    zoomState = rememberVicoZoomState(zoomEnabled = false),
  )
}

@Composable
private fun ViewChart11(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
  val marker = rememberMarker()
  AndroidViewBinding(
    { inflater, parent, attachToParent ->
      Chart1Binding.inflate(inflater, parent, attachToParent).apply {
        with(chartView) {
          this.modelProducer = modelProducer
        }
      }
    },
    modifier,
  )
}

private val x = (1..10).toList()
