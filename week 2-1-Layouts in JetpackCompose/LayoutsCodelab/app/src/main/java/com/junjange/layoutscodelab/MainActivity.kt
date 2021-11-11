package com.junjange.layoutscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeast
import coil.compose.rememberImagePainter
import com.junjange.layoutscodelab.ui.theme.LayoutsCodelabTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutsCodelabTheme {
                LayoutsCodelab()

            }
        }
    }
}


// Compose의 규칙 중 하나는 자녀를 한 번만 측정해야 한다는 것입니다.
// 자식을 두 번 측정하면 런타임 예외가 발생합니다.
// 그러나 자녀를 측정하기 전에 자녀에 대한 정보가 필요할 때가 있습니다.
//Intrinsics를 사용하면 실제로 측정되기 전에 자식을 쿼리할 수 있습니다.
@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )

        Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(1.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End),
            text = text2
        )
    }
}

@Preview
@Composable
fun TwoTextsPreview() {
    LayoutsCodelabTheme {
        Surface {
            TwoTexts(text1 = "Hi", text2 = "there")
        }
    }
}


// ConstraintLayout 위 의 첫 번째 예에 적용된 이 API 모양 은 화면 너비에 최적화되어 다음과 같습니다.
@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {
        val constraints = if (maxWidth < maxHeight) {
            decoupledConstraints(margin = 16.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId("button")
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("button")
        val text = createRefFor("text")

        constrain(button) {
            top.linkTo(parent.top, margin= margin)
        }
        constrain(text) {
            top.linkTo(button.bottom, margin)
        }
    }
}

@Composable
fun LargeConstraintLayout() {
    ConstraintLayout {
        val text = createRef()

        val guideline = createGuidelineFromStart(0.5f)
        Text(
            "This is a very very very very very very very long text",
            Modifier.constrainAs(text) {
                linkTo(guideline, parent.end)
                width = Dimension.preferredWrapContent.atLeast(100.dp)
            }
        )
    }
}

@Preview
@Composable
fun LargeConstraintLayoutPreview() {
    LayoutsCodelabTheme {
        LargeConstraintLayout()
    }
}



// ConstraintLayout Composable
@Composable
fun ConstraintLayoutContent(){
    ConstraintLayout{
        val (button1, button2, text) = createRefs()

        Button(
            onClick = {},
        modifier = Modifier.constrainAs(button1){
            top.linkTo(parent.top, margin = 16.dp)
            }
        ){
            Text("Button1")
        }

        Text("Text", Modifier.constrainAs(text){
            top.linkTo(button1.bottom, margin = 16.dp)
            centerHorizontallyTo(parent)
        })

        val barrier = createEndBarrier(button1, text)

        Button(
            onClick = {},
            modifier = Modifier.constrainAs(button2){
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ){
            Text("Button2")
        }
    }
}

@Preview
@Composable
fun ConstraintLayoutContentPreview(){
    LayoutsCodelabTheme {
        ConstraintLayoutContent()

    }
}


// 수정자를 만드는 방법
@Stable
fun Modifier.padding(all: Dp) =
    this.then(
        PaddingModifier(start = all, top = all, end = all, bottom = all, rtlAware = true)
    )

// 수정자 구현 세부사항
private class PaddingModifier(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val rtlAware: Boolean,
) : LayoutModifier {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {

        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }
}






@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content : @Composable () -> Unit
){
    Layout(
        modifier = modifier,
        content = content
    ){
        measurables, constraints ->

        // 여기에 제약 조건 논리가 주어진 자식을 측정하고 배치하십시오.

        // 각행의 너비 측정
        val rowWidths = IntArray(rows){ 0 }

        // 각 행의 최대 높이 측정
        val rowHeights = IntArray(rows) { 0 }

        val placeables = measurables.mapIndexed{ index, measurable ->
            val placeable = measurable.measure(constraints)

            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable
        }


        // 그리드의 너비는 가장 넓은 행
        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        // 그리드의 높이는 각 행의 가장 높은 요소의 합
        // 높이 제약 조건으로 강제 변환
        val height = rowHeights.sumOf{it}
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))


        val rowY = IntArray(rows){0}
        for (i in 1 until rows){
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width, height){
            val rowX = IntArray(rows){ 0 }

            placeables.forEachIndexed{ index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }

    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text : String){
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row (modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp ),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(modifier = Modifier
                .size(16.dp, 16.dp)
                .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)

        }

    }
}

@Preview
@Composable
fun ChipPreview(){
    LayoutsCodelabTheme{
        Chip(text = "Hi there")
    }
}


val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)


fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            // Where the composable gets placed
            placeable.placeRelative(0, placeableY)
        }
    }
)

@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // 자식 보기를 더 이상 제한하지 말고 주어진 제약 조건으로 측정
        // 측정된 자식 목록
        val placeables = measurables.map { measurable ->

            measurable.measure(constraints)
        }


        var yPosition = 0

        // 레이아웃의 크기를 최대한 크게 설정 -> 자식을 올바르게 배치 하기 위해..
        layout(constraints.maxWidth, constraints.maxHeight) {
            // 부모 레이아웃의 자식 배치
            placeables.forEach { placeable ->
                // 화면에서 항목 위치 지정
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}



@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {
    LayoutsCodelabTheme {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview() {
    LayoutsCodelabTheme {
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}
@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun ScrollingList() {
    val listSize = 100
    // 스크롤 위치를 저장
    val scrollState = rememberLazyListState()
    // 애니메이션 스크롤이 실행될 코루틴 범위를 저장
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    // 첫 번째 항목 인덱스
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text("Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    // listSize - 1은 목록의 마지막 인덱스
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }) {
                Text("Scroll to the end")
            }
        }

        LazyColumn(state = scrollState) {
            items(listSize) {
                ImageListItem(it)
            }
        }
    }
}

@Composable
fun ImageList() {
    // 이 상태로 스크롤 위치를 저장
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(100) {
            ImageListItem(it)
        }
    }
}

@Composable
fun SimpleList(){
    val scrollState = rememberScrollState()

    Column(Modifier.verticalScroll(scrollState)){
        repeat(100){
            Text("Ttem #$it")
        }
    }
}

@Composable
fun LazyList() {

    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(100) {
            Text("Item #$it")
        }
    }
}


@Composable
fun LayoutsCodelab(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text (text = "LayoutsCodelab")
            },
            actions = {
                IconButton(onClick = {}){
                    Icon(Icons.Filled.Favorite, contentDescription = null)
                }
            }
                )
        }

    ) { innerPadding ->
        BodyContent(Modifier
            .padding(innerPadding))

        }
    }


@Composable
fun BodyContent(modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .background(color = Color.LightGray, shape = RectangleShape)
            .padding(16.dp)
            .size(200.dp)
            .horizontalScroll(rememberScrollState())
    ){
        StaggeredGrid{
            for (topic in topics){
                Chip(modifier = Modifier.padding(8.dp), text= topic)
            }

        }
    }

}

@Preview
@Composable
fun LayoutCodeLabPreview(){
    LayoutsCodelabTheme {
        LayoutsCodelab()
    }
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier){
    Row (modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colors.surface)
        .clickable(onClick = {})
        .padding(16.dp)
    )

    {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            // 이미지
        }
        Column {
            Text("Alfred Sisley", fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LayoutsCodelabTheme {
        PhotographerCard()
    }
}