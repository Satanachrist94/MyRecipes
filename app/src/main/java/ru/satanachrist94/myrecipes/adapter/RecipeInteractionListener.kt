package ru.satanachrist94.myrecipes.adapter

import ru.satanachrist94.myrecipes.dto.Recipe
import ru.satanachrist94.myrecipes.dto.Step

interface RecipeInteractionListener {
    fun onFavoritesAddListener(recipe: Recipe)
    fun onDeleteAllFromFavoritesListener()
    fun onDeleteListener(recipe: Recipe)
    fun onEditListener(recipe: Recipe)
    fun onRecipeClickListener(recipe: Recipe)
    fun onRemoveFromFilterCategoryListener(categoryId: Int)
    fun onAddToFilterCategoryListener(categoryId: Int)
    fun onFilterClickedListener()
    fun onSearchListener(query: String)
    fun onDeleteStepListener(step: Step)
    fun onEditStepListener(step: Step)
}