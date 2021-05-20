package sk.uniza.fri.sudora

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sk.uniza.fri.sudora.adapter.NoteAdapter
import sk.uniza.fri.sudora.adapter.NoteListener
import sk.uniza.fri.sudora.databinding.FragmentTrashBinding

/**
 * Fragment, ktory zobrazuje poznamky ulozene v kosi
 */
class TrashFragment : Fragment() {
    private val viewModel: NoteListViewModel by activityViewModels()
    lateinit var adapter : NoteAdapter
    lateinit var binding : FragmentTrashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trash, container, false)


        adapter = NoteAdapter(viewModel, ListType.TRASH ,NoteListener {}, this.context)
        binding.trashList.adapter = adapter
        viewModel.trashList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
            adapter.notifyDataSetChanged()
            if (it.size > 0) {
                binding.textViewTrash.text = ""
            }  else {
                binding.textViewTrash.text = getString(R.string.your_trash_is_empty)
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.trash_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.trashList.value?.size != 0 && item.itemId == R.id.emptyTrashButton)  {
            val deleteDialog : MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_dialog_title)
                    .setMessage(R.string.delete_all_dialog_text)
                    .setNegativeButton(R.string.delete_dialog_no_button_label)
                    { _, _ -> }
            deleteDialog.setPositiveButton(R.string.delete_dialog_yes_button_label)
            { _, _ ->
                viewModel.trashList.value?.clear()
                adapter.notifyDataSetChanged()
                binding.textViewTrash.text = getString(R.string.your_trash_is_empty)
            }.create().show()
        }
        return super.onOptionsItemSelected(item)
    }
}