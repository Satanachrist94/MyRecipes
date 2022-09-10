package ru.satanachrist94.myrecipes.adapter

import android.content.Context
import android.system.Os.remove
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import ru.satanachrist94.myrecipes.databinding.RecipeBinding

import ru.satanachrist94.myrecipes.dto.Recipe

class RecipeAdapter(
    private val context: Context,
    private val listener: RecipeInteractionListener
) : androidx.recyclerview.widget.ListAdapter<Recipe, RecipeViewHolder>(RecipeDiffCallBack()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeBinding.inflate(inflater,parent,false)

        return  RecipeViewHolder(binding,listener,context)

    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}

class RecipeViewHolder(
    private val binding: RecipeBinding,
    private val listener: RecipeInteractionListener,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(recipe: Recipe) {
        binding.apply {
            nameRecipe.text = recipe.name

            recipe.imageUri?.let {
                Glide
                    .with(context)
                    .load(it)
                    .into(recipeImage)
            }


        }

        binding.root.setOnClickListener {
            listener.onRecipeClickListener(recipe)
        }

    }
}
class RecipeDiffCallBack : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }

}
