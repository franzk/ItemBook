package net.franzka.itembook.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.franzka.itembook.ItembookApplication
import net.franzka.itembook.R
import net.franzka.itembook.databinding.FragmentSpaceFormBinding
import net.franzka.itembook.utils.ImageHelper
import net.franzka.itembook.utils.Utils
import net.franzka.itembook.viewmodels.SpaceViewModel
import net.franzka.itembook.viewmodels.SpaceViewModelFactory

class SpaceFormFragment : Fragment() {

    companion object {
        private const val TAG = "SpaceFormFragment"
        enum class Mode { CREATE, EDIT }
    }

    private lateinit var mode: Mode

    private lateinit var binding: FragmentSpaceFormBinding

    private val spaceViewModel: SpaceViewModel by viewModels {
        SpaceViewModelFactory((activity?.application as ItembookApplication).spaceRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            val args = SpaceFormFragmentArgs.fromBundle(it)
            mode = args.mode
            args.space?.let { spaceViewModel.setSpaceData(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_space_form, container, false)

        binding.apply {
            fragmentSpaceForm = this@SpaceFormFragment
            viewModel = spaceViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        spaceViewModel.apply {
            localFilesDir = requireActivity().filesDir.absolutePath

            // set image
            imagePath.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) { binding.imageSpace.setImageURI(Uri.parse(it)) }
            })

            //  database work finished (insert ou update)
            dbUpdated.observe(viewLifecycleOwner, {
                if (it) {
                    binding.progressCircular.visibility = View.INVISIBLE
                    findNavController().navigateUp() // TODO voir pour atterir sur spaceitems quand on fait un insert
                }
            })
        }
        return binding.root
    }



    fun save() {
        when(mode) {
            Mode.CREATE -> insertSpace()
            Mode.EDIT -> updateSpace()
        }
    }

    private fun insertSpace() {
        binding.progressCircular.visibility = View.VISIBLE
        spaceViewModel.insert(if (fileToSave) binding.imageSpace else null)
        Utils.hideKeyboard(binding.editSpaceName)
    }

    private fun updateSpace() {
        binding.progressCircular.visibility = View.VISIBLE
        spaceViewModel.update(if (fileToSave) binding.imageSpace else null)
        Utils.hideKeyboard(binding.editSpaceName)
    }

    /** image **/
    private val imageHelper = ImageHelper()
    private var fileToSave = false

    fun removeImage() {
        binding.imageSpace.setImageResource(R.drawable.ic_cube)
        spaceViewModel.imagePath.value = ""
        fileToSave = false
    }

    fun camera() {
        imageHelper.camera(requireContext(), requireActivity().filesDir)?.let {
            startActivityForResult(it, ImageHelper.RC_CAMERA)
        }
    }

    fun gallery() {
        startActivityForResult(imageHelper.gallery(), ImageHelper.RC_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        binding.progressCircular.visibility = View.VISIBLE
        when (requestCode) {
            ImageHelper.RC_CAMERA -> {
                fileToSave = false
                imageHelper.getImageFile()?.let {
                    binding.imageSpace.setImageURI(Uri.parse(it.absolutePath))
                    spaceViewModel.imagePath.value = it.absolutePath
                }
            }
            ImageHelper.RC_GALLERY ->
                data?.let {
                    requireActivity().let { a ->
                        a.runOnUiThread {
                            // Stuff that updates the UI
                            binding.imageSpace.setImageURI(it.data)
                            fileToSave = true
                        }
                    }
                }
        }
        binding.progressCircular.visibility = View.INVISIBLE
    }
}