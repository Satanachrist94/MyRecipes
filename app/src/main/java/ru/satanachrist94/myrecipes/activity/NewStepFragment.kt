package ru.satanachrist94.myrecipes.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import ru.satanachrist94.myrecipes.R
import ru.satanachrist94.myrecipes.databinding.FragmentNewStepBinding
import ru.satanachrist94.myrecipes.viewmodel.RecipeViewModel

class NewStepFragment : Fragment() {
    var imageUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewStepBinding.inflate(inflater, container, false)
        val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

        viewModel.currentStep.observe(viewLifecycleOwner) { step ->
            if (step != null) {
                with(binding) {
                    textStep.setText(step.content)
                    viewModel.imageUriStep.value = step.image
                }
            }
        }

        viewModel.imageUriStep.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.imageStep.isVisible = true
                Glide
                    .with(requireActivity())
                    .load(it)
                    .into(binding.imageStep)
            }
        }

        val image = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.imageUriStep.value = it.toString()
            }
        }

        binding.buttonImage.setOnClickListener {
            image.launch(arrayOf("image/*"))
        }

        binding.buttonSave.setOnClickListener {
            if (binding.textStep.text.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    R.string.toast_empty_text,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.onSaveStepListener(binding.textStep.text.toString())
            viewModel.currentRecipe.value = viewModel.currentRecipe.value?.copy(steps = viewModel.steps)
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.close_newRecipeFragment)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.OK) { dialog, _ ->
                    viewModel.imageUriStep.value = null
                    viewModel.currentStep.value = null
                    findNavController().navigateUp()
                    dialog.dismiss()
                }
                .show()
        }

        return binding.root
    }



}