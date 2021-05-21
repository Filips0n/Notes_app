package sk.uniza.fri.sudora.listDisplay

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sk.uniza.fri.sudora.R
import sk.uniza.fri.sudora.adapter.NoteAdapter
import sk.uniza.fri.sudora.adapter.NoteListener
import sk.uniza.fri.sudora.databinding.FragmentTrashBinding
import sk.uniza.fri.sudora.notes.list.ListType
import sk.uniza.fri.sudora.notes.list.NoteListViewModel

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
        //zobraz action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trash, container, false)

        //adapter pre zvladnutie prace s poznamkami
        adapter = NoteAdapter(viewModel, ListType.TRASH ,NoteListener {}, this.context)
        //nastavenie nech adapter pracuje s tymto zoznamom
        binding.trashList.adapter = adapter
        //ak nejde zmenu v zozname vykona prikazy
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

    /**
     * Nastavi ze fragment ma menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Nastavuje menu na zobrazenie
     *
     * @param menu menu, ktore sa ma zobrazit
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.trash_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Zisti stalacene tlacidlo a podla toho vykona okciu
     *
     * @param item stlacene tlacidlo
     *
     * @return stlaceny item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //skontroluje ci nie je zoznam prazdny
        if (viewModel.trashList.value?.size != 0 && item.itemId == R.id.emptyTrashButton)  {
            //zobrazi dialog ci skutocne chce pouzivatel vymazat vsetky poznamky
            val deleteDialog : MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_dialog_title)
                    .setMessage(R.string.delete_all_dialog_text)
                    .setNegativeButton(R.string.delete_dialog_no_button_label)
                    { _, _ -> }
            deleteDialog.setPositiveButton(R.string.delete_dialog_yes_button_label)
            { _, _ ->
                //vymaze vsetky poznamky z kosa
                //a upozorni adapter
                viewModel.trashList.value?.clear()
                adapter.notifyDataSetChanged()
                binding.textViewTrash.text = getString(R.string.your_trash_is_empty)
            }.create().show()
        }
        return super.onOptionsItemSelected(item)
    }
}