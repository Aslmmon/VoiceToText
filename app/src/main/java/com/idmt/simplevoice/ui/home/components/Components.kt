package com.idmt.simplevoice.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.ui.databse.SECIONS
import com.idmt.simplevoice.ui.home.languagesList

@Composable
fun LanguageChooser(modifier: Modifier, onLangugeChoosen: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Button(onClick = { expanded = !expanded }) {
        Text(text = "Choose Langugage")
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "More",
            tint = Color.White
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languagesList.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onLangugeChoosen.invoke(it)
                        expanded = false
                    }
                )
            }
        }
    }

}


@Composable
fun CategoryChooser(modifier: Modifier, onClickCategory: (Int) -> Unit) {
    var expandedCategory by remember { mutableStateOf(false) }

    Button(
        modifier = modifier.padding(horizontal = 10.dp),
        onClick = {
            expandedCategory = !expandedCategory
        }) {
        Text(text = "Choose Category")
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "More",
            tint = Color.White
        )
        DropdownMenu(
            expanded = expandedCategory,
            onDismissRequest = { expandedCategory = false }
        ) {
            SECIONS.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        expandedCategory = false
                        onClickCategory.invoke(it.value)
                    }
                )
            }
        }
    }
}

@Composable
fun EditText(textEnterd: String, onTextChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(start = 15.dp, top = 10.dp, end = 15.dp)
            .background(Color.White, RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(5.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black
        ),
        value = textEnterd,
        onValueChange = { text -> onTextChanged.invoke(text) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        maxLines = 5,
        textStyle = MaterialTheme.typography.titleSmall
    )
}


@Composable
fun Loader(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(150.dp)
            .background(
                Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(25.dp)

            ), contentAlignment = Alignment.Center
    ) {
        Column {
            CircularProgressIndicator(color = Color.White, strokeWidth = 1.dp)
            Spacer(modifier = modifier.height(10.dp))
            androidx.compose.material.Text(text = "loading..")
        }
    }
}


@Composable
fun LoadingButton(
    onClick: () -> Unit,
    loading: Boolean,
    buttonText:String,
    showIcon:Boolean=true
) {
    val transition = updateTransition(
        targetState = loading,
        label = "master transition",
    )
    val horizontalContentPadding by transition.animateDp(
        transitionSpec = {
            spring(
                stiffness = SpringStiffness,
            )
        },
        targetValueByState = { toLoading -> if (toLoading) 12.dp else 24.dp },
        label = "button's content padding",
    )
    Button(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = 1.dp),
        contentPadding = PaddingValues(
            horizontal = horizontalContentPadding,
            vertical = 8.dp,
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            LoadingContent(
                loadingStateTransition = transition,
            )
            PrimaryContent(
                loadingStateTransition = transition,
                buttonText,
                showIcon
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LoadingContent(
    loadingStateTransition: androidx.compose.animation.core.Transition<Boolean>,
) {
    loadingStateTransition.AnimatedVisibility(
        visible = { loading -> loading },
        enter = fadeIn(),
        exit = fadeOut(
            animationSpec = spring(
                stiffness = SpringStiffness,
                visibilityThreshold = 0.10f,
            ),
        ),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            color = Color.White,
            strokeWidth = 1.5f.dp,
            strokeCap = StrokeCap.Round,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PrimaryContent(
    loadingStateTransition: Transition<Boolean>,
    buttonText: String,
    showIcon: Boolean,
) {
    loadingStateTransition.AnimatedVisibility(
        visible = { loading -> !loading },
        enter = fadeIn() + expandHorizontally(
            animationSpec = spring(
                stiffness = SpringStiffness,
                dampingRatio = Spring.DampingRatioMediumBouncy,
                visibilityThreshold = IntSize.VisibilityThreshold,
            ),
            expandFrom = Alignment.CenterHorizontally,
        ),
        exit = fadeOut(
            animationSpec = spring(
                stiffness = SpringStiffness,
                visibilityThreshold = 0.10f,
            ),
        ) + shrinkHorizontally(
            animationSpec = spring(
                stiffness = SpringStiffness,
                // dampingRatio is not applicable here, size cannot become negative
                visibilityThreshold = IntSize.VisibilityThreshold,
            ),
            shrinkTowards = Alignment.CenterHorizontally,
        ),
    ) {

        Row(modifier = Modifier.width(120.dp), horizontalArrangement = Arrangement.Center) {
            Text(text = buttonText)
            if (showIcon) Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "More",
                tint = Color.White
            )
        }

    }
}

private val SpringStiffness = Spring.StiffnessMediumLow
