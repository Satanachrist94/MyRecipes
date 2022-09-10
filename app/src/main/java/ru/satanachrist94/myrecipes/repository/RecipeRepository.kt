package ru.satanachrist94.myrecipes.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.satanachrist94.myrecipes.db.RecipeEntity
import ru.satanachrist94.myrecipes.dto.Recipe

interface RecipeRepository {
    fun getRecipes()
    fun get(): LiveData<List<Recipe>>
    fun delete(id: Long)
    fun like(id: Long)
    fun deleteAllFromFavorites()
    fun save(recipe: Recipe)
    fun removeCategoryFromFilterChips(categoryId: Int)
    fun addCategoryToFilterChips(categoryId: Int)
    fun startFilterChips()
    fun search(query: String)

    companion object {
        const val NEW_REC_ID = 0L
        const val NEW_STEP_ID = 0L
    }

}