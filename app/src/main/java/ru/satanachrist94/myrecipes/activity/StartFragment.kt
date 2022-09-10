package ru.satanachrist94.myrecipes.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.google.android.material.chip.Chip
import ru.satanachrist94.myrecipes.R
import ru.satanachrist94.myrecipes.adapter.RecipeAdapter
import ru.satanachrist94.myrecipes.databinding.FragmentMainBinding
import ru.satanachrist94.myrecipes.dto.Recipe
import ru.satanachrist94.myrecipes.viewmodel.RecipeViewModel


class StartFragment : Fragment() {
    val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)
    lateinit var binding: FragmentMainBinding
    private var list = mutableListOf<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val adapter = RecipeAdapter(requireContext(), viewModel)
        binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.recyclerView.adapter = adapter

        activateChips()

        with(binding) {

            fab.setOnClickListener {
                findNavController().navigate(R.id.action_recipes_to_newRecipeFragment)
            }

            searchClear.setOnClickListener {
                search.text?.clear()
                viewModel.clearSearch()
            }

            searchButton.setOnClickListener {
                if (search.text.isNullOrBlank()) return@setOnClickListener
                viewModel.onSearchListener(search.text.toString())
            }

        }

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            list = it.toMutableList()
            binding.floppa.isVisible = it.isEmpty()

        }

        viewModel.openRecipeEvent.observe(viewLifecycleOwner) { id ->
            findNavController().navigate(
                R.id.action_recipes_to_recipeFragment,
                Bundle().apply {
                    putLong(KEY_ID, id)
                })
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        with(binding) {
                            filterChipsGroup.isVisible = !filterChipsGroup.isVisible
                            searchGroup.isVisible = false
                            //floppa.isVisible = floppa.isVisible
                            if (!filterChipsGroup.isVisible) {
                                makeAllChipsChecked()
                                //floppa.isVisible =!floppa.isVisible
                            } else {

                                viewModel.onFilterClickedListener()

                            }
                        }
                        true
                    }
                    R.id.action_search -> {
                        makeChipsGroupInvisible()
                        with(binding) {
                            searchGroup.isVisible = !searchGroup.isVisible
                            floppa.isVisible = !floppa.isVisible
                            if (!list.isEmpty()) {
                                floppa.isVisible = !floppa.isVisible
                            }
                            search.requestFocus()
                            true
                        }
                    }

                    else -> false

                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun activateChips() {
        with(binding) {
            chipEuropean.filterRecipes(EUROPEAN)
            chipAsian.filterRecipes(ASIAN)
            chipPanAsiatic.filterRecipes(PAN_ASIATIC)
            chipEastern.filterRecipes(EASTERN)
            chipAmerican.filterRecipes(AMERICAN)
            chipRussian.filterRecipes(RUSSIAN)
            chipMediterranean.filterRecipes(MEDITERRANEAN)
        }
    }

    private fun Chip.filterRecipes(category: Int) {
        setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                viewModel.onRemoveFromFilterCategoryListener(category)
            } else {
                viewModel.onAddToFilterCategoryListener(category)
            }
        }
    }

    private fun makeAllChipsChecked() {
        with(binding) {
            chipAmerican.isChecked = true
            chipMediterranean.isChecked = true
            chipRussian.isChecked = true
            chipEastern.isChecked = true
            chipAsian.isChecked = true
            chipPanAsiatic.isChecked = true
            chipEuropean.isChecked = true
        }
    }

    private fun makeChipsGroupInvisible() {
        binding.filterChipsGroup.isVisible = false
    }

    companion object {
        const val KEY_ID = "id"
        const val EUROPEAN = 0
        const val ASIAN = 1
        const val PAN_ASIATIC = 2
        const val EASTERN = 3
        const val AMERICAN = 4
        const val RUSSIAN = 5
        const val MEDITERRANEAN = 6
    }


}