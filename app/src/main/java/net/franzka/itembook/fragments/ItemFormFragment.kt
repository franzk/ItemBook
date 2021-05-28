package net.franzka.itembook.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import net.franzka.itembook.ItembookApplication
import net.franzka.itembook.R
import net.franzka.itembook.databinding.FragmentItemFormBinding
import net.franzka.itembook.utils.ImageHelper
import net.franzka.itembook.utils.Utils
import net.franzka.itembook.viewmodels.ItemViewModel
import net.franzka.itembook.viewmodels.ItemViewModelFactory
import java.io.File

class ItemFormFragment : Fragment() {

    companion object {
        private const val TAG = "ItemFormFragment"

        enum class Mode { CREATE, EDIT }
    }

    private lateinit var mode: Mode

    private lateinit var binding: FragmentItemFormBinding
    private lateinit var tagAdapter: ArrayAdapter<String>

    private val itemViewModel: ItemViewModel by viewModels {
        ItemViewModelFactory(
                (activity?.application as ItembookApplication).itemRepository,
                (activity?.application as ItembookApplication).tagRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            val args = ItemFormFragmentArgs.fromBundle(it)
            mode = args.mode
            args.item.let { itemViewModel.setItemData(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_form, container, false)
        binding.apply {
            fragmentItemForm = this@ItemFormFragment
            lifecycleOwner = viewLifecycleOwner
            viewModel = itemViewModel
        }


        itemViewModel.apply {

            localFilesDir = requireActivity().filesDir.absolutePath

            // image change
            imagePath.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    binding.imageItem.setImageURI(Uri.parse(it))
                }
            })

            // database work finished (insert or update)
            dbUpdated.observe(viewLifecycleOwner, {
                if (it) {
                    binding.progressCircular.visibility = View.INVISIBLE
                    findNavController().navigateUp()
                }
            })

        }

        if (mode == Mode.EDIT) {
            populateTags()     // item tags --> chipgroup
        }

        setupAutoCompleteTextView() // all tags --> autocomplete

        return binding.root
    }


    private fun setupAutoCompleteTextView() {

        itemViewModel.allTags.observe(viewLifecycleOwner, { tags ->
            tagAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_selectable_list_item, tags.toTypedArray())
            binding.autoCompleteTextview.setAdapter(tagAdapter)
        })

        binding.autoCompleteTextview.apply {
            threshold = 1
            setOnAddTagListener { name ->
                addChip(name)
                this.text = null
            }
        }

    }

    private fun populateTags() {

        itemViewModel.getItemTags().observe(viewLifecycleOwner, { tags ->
            tags.forEach { tag ->
                addChip(tag.tagName)
            }
        })

    }

    private fun addChip(name: String) {
        val chip = Chip(requireContext())
        chip.apply {
            text = name
            isCloseIconVisible = true
            isClickable = true
            isFocusable = true
            setOnClickListener(chipClickListener)
        }
        binding.chipGroup.addView(chip)
    }

    private fun getTags() : List<String> {
        val tags = mutableListOf<String>()
        binding.chipGroup.children.forEach { tags.add((it as Chip).text.toString()) }
        return tags
    }

    fun save() {
        when(mode) {
            Mode.CREATE -> insertItem()
            Mode.EDIT -> updateItem()
        }
    }

    private fun insertItem() {
        binding.progressCircular.visibility = View.VISIBLE
        itemViewModel.insert(getTags(), if (fileToSave) binding.imageItem else null)
        Utils.hideKeyboard(binding.editItemName)
    }

    private fun updateItem() {
        binding.progressCircular.visibility = View.VISIBLE
        itemViewModel.update(getTags(), if (fileToSave) binding.imageItem else null)
        Utils.hideKeyboard(binding.editItemName)
    }


    /** tags display **/
    private fun AutoCompleteTextView.setOnAddTagListener(callback: (name: String) -> Unit) {
        // select from adapter list
        setOnItemClickListener { adapterView, _, position, _ ->
            val name = adapterView.getItemAtPosition(position) as String
            callback(name)
        }

        // done pressed
        setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val name = textView.text.toString()
                callback(name)
                true
            } else false
        }

        doAfterTextChanged { text ->
            if (text != null && text.isEmpty()) {
                return@doAfterTextChanged
            }
            // comma is detected (optional space)
            if (text?.last() == ',') { //  || text?.last() == ' '
                val name = text.substring(0, text.length - 1)
                callback(name)
            }
        }
    }

    private val chipClickListener = View.OnClickListener {

        val anim = AlphaAnimation(1f,0f)
        anim.duration = 250
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                binding.chipGroup.removeView(it)
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        it.startAnimation(anim)
    }

    /** image **/
    private val imageHelper = ImageHelper()
    private var fileToSave:Boolean = false
    private lateinit var photoFile: File

    fun removeImage() {
        binding.imageItem.setImageResource(R.drawable.ic_item)
        itemViewModel.imagePath.value = ""
        fileToSave = false
    }

    fun camera() {

        imageHelper.newImageFile(requireActivity().filesDir)?.let {
            photoFile = it
            val cameraUri = FileProvider.getUriForFile(
                requireContext(),
                "net.franzk.itembook.provider",
                photoFile
            )
            takePicture.launch(cameraUri)
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            fileToSave = false
            itemViewModel.imagePath.value = photoFile.absolutePath
        }
        binding.progressCircular.visibility = View.INVISIBLE
    }

    fun gallery() {
        selectImageLauncher.launch("image/*")
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.imageItem.setImageURI(uri)
            fileToSave = true
        }
        binding.progressCircular.visibility = View.INVISIBLE
    }

}