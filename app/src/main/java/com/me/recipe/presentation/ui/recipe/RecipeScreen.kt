@file:OptIn(InternalCoroutinesApi::class)

package com.me.recipe.presentation.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.me.recipe.R
import com.me.recipe.domain.features.recipe.model.Recipe
import com.me.recipe.presentation.component.LoadingRecipeShimmer
import com.me.recipe.presentation.component.RecipeView
import com.me.recipe.presentation.component.util.DefaultSnackbar
import com.me.recipe.presentation.ui.navigation.NavigationDestination
import com.me.recipe.presentation.ui.recipe_list.RecipeListContract
import com.me.recipe.util.compose.collectInLaunchedEffect
import com.me.recipe.util.compose.use
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

object RecipeDestination : NavigationDestination {
    override val route = "Recipe"
    override val titleRes = R.string.navigate_recipe_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
    const val deeplinkWithArgs = "recipe://composables.com/{$itemIdArg}"
}

@Composable
fun RecipeScreen() {
    RecipeScreen(hiltViewModel())
}

@Composable
private fun RecipeScreen(
    viewModel: RecipeViewModel
) {
    val (state, effect, event) = use(viewModel = viewModel)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    effect.collectInLaunchedEffect { effect ->
        when (effect) {
            is RecipeContract.Effect.ShowSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(effect.message, "Ok")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            DefaultSnackbar(snackbarHostState = snackbarHostState) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.loading && state.recipe.id != Recipe.EMPTY.id) {
                LoadingRecipeShimmer(imageHeight = 250.dp)
            } else if (state.recipe.id != Recipe.EMPTY.id) {
                RecipeView(recipe = state.recipe)
            }
        }
    }
}