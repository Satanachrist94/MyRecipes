package ru.satanachrist94.myrecipes.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.satanachrist94.myrecipes.R

import ru.satanachrist94.myrecipes.adapter.StepEditAdapter
import ru.satanachrist94.myrecipes.databinding.FragmentNewRecipeBinding
import ru.satanachrist94.myrecipes.viewmodel.RecipeViewModel

class NewRecipeFragment : Fragment() {
    lateinit var binding: FragmentNewRecipeBinding
    val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewRecipeBinding.inflate(inflater, container, false)

        val adapter = StepEditAdapter(requireContext(), viewModel)
        binding.rvRecipe.adapter = adapter

        viewModel.currentRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe != null) {
                with(binding) {
                    author.setText(recipe.author)
                    nameRecipe.setText(recipe.name)
                    category.setSelection(recipe.categoryId)
                    viewModel.editStepsMode(recipe.steps)
                    viewModel.imageUriRecipe.value = recipe.imageUri

                }
            }
        }

        viewModel.stepsView.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.imageUriRecipe.observe(viewLifecycleOwner) {
            if (it != null) {
                Glide
                    .with(requireActivity())
                    .load(it)
                    .into(binding.recipeImage)
            }
        }

        val image = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.imageUriRecipe.value = it.toString()
            }
        }
        binding.addRecipeImage
            .setOnClickListener {
                image.launch(arrayOf("image/*"))
            }



        binding.buttonStepAdd.setOnClickListener {
            saveData()
            findNavController().navigate(R.id.action_newRecipeFragment_to_newStepFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_recipe_edit, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {

                    android.R.id.home -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.close_newRecipeFragment)
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.OK) { dialog, _ ->
                                clearData()
                                findNavController().navigateUp()
                                dialog.dismiss()
                            }
                            .show()
                        return true
                    }

                    R.id.menu_cancel -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.close_newRecipeFragment)
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.OK) { dialog, _ ->
                                clearData()
                                findNavController().navigateUp()
                                dialog.dismiss()
                            }
                            .show()
                        return true
                    }

                    R.id.menu_save -> {
                        val category = binding.category.selectedItem.toString()
                        val categoryId = binding.category.selectedItemPosition
                        val name = binding.nameRecipe.text.toString()
                        val author = binding.author.text.toString()
                        val steps = viewModel.steps

                        if(author.isBlank()) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.author_name_is_empty),
                                Toast.LENGTH_SHORT
                            )
                        }

                        if (name.isBlank()) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_recipe_name_empty),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return false
                        }

                        if (steps.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_recipe_steps_empty),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return false
                        }

                        viewModel.onSaveRecipeListener(author,name, category, categoryId)
                        viewModel.clearStepsList()
                        findNavController().navigateUp()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun saveData() {
        viewModel.currentRecipe.value = viewModel.currentRecipe.value?.copy(
            author = binding.author.text.toString(),
            name = binding.nameRecipe.text.toString(),
            category = binding.category.selectedItem.toString(),
            categoryId = binding.category.selectedItemPosition,
            imageUri = viewModel.imageUriRecipe.value
        )
    }

    private fun clearData() {
        viewModel.clearStepsList()
        viewModel.currentRecipe.value = null
        viewModel.imageUriRecipe.value = null
    }


}