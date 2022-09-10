package ru.satanachrist94.myrecipes.viewmodel

import ru.satanachrist94.myrecipes.utils.SingleLiveEvent
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.satanachrist94.myrecipes.adapter.RecipeInteractionListener
import ru.satanachrist94.myrecipes.db.AppDb
import ru.satanachrist94.myrecipes.dto.Recipe
import ru.satanachrist94.myrecipes.dto.Step
import ru.satanachrist94.myrecipes.repository.RecipeRepository
import ru.satanachrist94.myrecipes.repository.RecipeRepositoryImpl

class RecipeViewModel(application: Application) :
    AndroidViewModel(application),
    RecipeInteractionListener {
    private val repository: RecipeRepository =
        RecipeRepositoryImpl(AppDb.getInstance(application).recipeDao)
    val data = repository.get()
    val currentRecipe = MutableLiveData<Recipe?>(null)
    val currentStep = MutableLiveData<Step?>(null)
    val openRecipeEvent = SingleLiveEvent<Long>()
    val editRecipeEvent = SingleLiveEvent<Boolean>()
    val editStepEvent = SingleLiveEvent<Boolean>()
    var steps = emptyList<Step>()
    val stepsView = MutableLiveData<List<Step>?>(null)
    var imageUriRecipe = MutableLiveData<String?>(null)
    var imageUriStep = MutableLiveData<String?>(null)

    fun onSaveRecipeListener(author :String,
                             name: String, category: String,
                             categoryId: Int
    ) {
        val recipe = currentRecipe.value?.copy(
            author = author,
            name = name,
            category = category,
            categoryId = categoryId,
            imageUri = imageUriRecipe.value,
            steps = steps
        ) ?:Recipe(
            id = RecipeRepository.NEW_REC_ID,
            author =author,
            name = name,
            category = category,
            categoryId = categoryId,
            imageUri = imageUriRecipe.value,
            steps = steps
        )
        repository.save(recipe)
        currentRecipe.value = null
        imageUriRecipe.value = null
    }

    fun onSaveStepListener(content: String) {
        val step = currentStep.value?.copy(
            content = content,
            image = imageUriStep.value
        ) ?: Step(
            id = RecipeRepository.NEW_STEP_ID,
            content = content,
            image = imageUriStep.value
        )
        saveStep(step)
        currentStep.value = null
        imageUriStep.value = null
    }

    private fun saveStep(step: Step) {
        if (step.id == RecipeRepository.NEW_STEP_ID) insertStep(step) else updateStep(step)
    }

    private fun insertStep(step: Step) {
        var currentId = 0L
        if (steps.isEmpty()) {
            currentId = RecipeRepository.NEW_STEP_ID
        } else {
            for (oneStep in steps) {
                if (oneStep.id > currentId) currentId = oneStep.id
            }
        }
        steps = steps + listOf(step.copy(id= ++currentId))
        stepsView.value = steps
    }

    private fun updateStep(step: Step) {
        steps = steps.map { if (it.id == step.id) step else it}
        stepsView.value = steps
    }

    fun clearStepsList() {
        steps = emptyList()
        stepsView.value = null
    }

    fun editStepsMode(list: List<Step>) {
        steps = list
        stepsView.value = steps
    }

    fun clearSearch() = repository.getRecipes()

    override fun onFavoritesAddListener(recipe: Recipe) = repository.like(recipe.id)

    override fun onDeleteAllFromFavoritesListener() = repository.deleteAllFromFavorites()

    override fun onDeleteListener(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditListener(recipe: Recipe) {
        currentRecipe.value = recipe
        editRecipeEvent.value = true
    }

    override fun onRecipeClickListener(recipe: Recipe) {
        openRecipeEvent.value = recipe.id
    }

    override fun onRemoveFromFilterCategoryListener(categoryId: Int) =
        repository.removeCategoryFromFilterChips(categoryId)

    override fun onAddToFilterCategoryListener(categoryId: Int) =
        repository.addCategoryToFilterChips(categoryId)

    override fun onFilterClickedListener() = repository.startFilterChips()

    override fun onSearchListener(query: String) = repository.search(query)

    override fun onDeleteStepListener(step: Step) {
        steps = steps.filterNot { it.id == step.id }
        stepsView.value = steps
        currentRecipe.value = currentRecipe.value?.copy(steps = steps)
    }

    override fun onEditStepListener(step: Step) {
        currentStep.value = step
        editStepEvent.value = true
    }


}