package com.todokanai.composepractice.compose.presets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.todokanai.composepractice.data.dataclass.ProgressState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/** 아직 불안정 **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDialog(
    modifier:Modifier,
    state: StateFlow<ProgressState>,
    onConfirm:()->Unit
    //progress:Float
) {
//    var state by remember{mutableStateOf(progressState)}     // var 로 사용하는거 맞음. val로 수정하지 말것

    val stateTemp = state.collectAsStateWithLifecycle()
 //   val progress= (stateTemp.value.progress!!.toDouble()/100).toFloat()
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        content = {
            Column() {

                LinearProgressIndicator(
                    modifier = Modifier.size(48.dp), // 원하는 크기로 조정합니다.
                    color = Color.Blue, // 원하는 색상으로 조정합니다.
                    progress = (stateTemp.value.progress!!.toDouble()/100).toFloat(),
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.Red
                )
                CustomLinearProgressIndicator(progress = (stateTemp.value.progress!!.toDouble()/100).toFloat())
                TextButton(
                    onClick = {onConfirm()}
                ){
                    Text(text = "Confirm")
                }
            }
        }
    )
}

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = Color.Red,
    backgroundColor: Color = Color.Red.copy(0.24f),
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}

@Preview
@Composable
private fun ProgressDialogPreview(){
    Surface() {
        ProgressDialog(
            modifier = Modifier,
            state = MutableStateFlow(ProgressState(progress = 20)),
            onConfirm = {}
        )
    }
}

@Preview
@Composable
private fun LPreview(){
    Surface() {
        CustomLinearProgressIndicator(
            modifier = Modifier
                .width(200.dp)
                .height(10.dp),
            progress = 0.9f)
    }
}