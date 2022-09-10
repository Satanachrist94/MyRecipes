package ru.satanachrist94.myrecipes.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import ru.satanachrist94.myrecipes.db.RecipeDao

import ru.satanachrist94.myrecipes.db.toEntity
import ru.satanachrist94.myrecipes.db.toModel
import ru.satanachrist94.myrecipes.dto.Recipe

class RecipeRepositoryImpl( private val dao: RecipeDao,
) : RecipeRepository {
    private var allRecipes = emptyList<Recipe>()
    private var filteredRecipes = emptyList<Recipe>()
    private val data = MutableLiveData(allRecipes)

    init {
        getRecipes()
    }

    override fun getRecipes() {
        allRecipes = dao.getBase().map { it.toModel() }
        data.value = allRecipes
    }

    override fun get(): LiveData<List<Recipe>> = data

    override fun save(recipe: Recipe) {
        dao.save(recipe.toEntity())
        getRecipes()
    }

    override fun delete(id: Long) {
        dao.delete(id)
        getRecipes()
    }

    override fun like(id: Long) {
        dao.inFavorites(id)
        getRecipes()
    }

    override fun deleteAllFromFavorites() {
        dao.clearFavorites()
        getRecipes()
    }

    override fun removeCategoryFromFilterChips(categoryId: Int) {
        filteredRecipes = filteredRecipes.filterNot { it.categoryId == categoryId }
        data.value = filteredRecipes
    }

    override fun addCategoryToFilterChips(categoryId: Int) {
        filteredRecipes = filteredRecipes + allRecipes.filter { it.categoryId == categoryId }
        data.value = filteredRecipes
    }

    override fun startFilterChips() {
        filteredRecipes = allRecipes
        data.value = allRecipes
    }

    override fun search(query: String) {
        data.value = dao.searchByName(query).map { it.toModel() }
    }

}