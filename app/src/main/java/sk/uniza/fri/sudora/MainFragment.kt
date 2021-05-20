package sk.uniza.fri.sudora

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import sk.uniza.fri.sudora.adapter.NoteAdapter
import sk.uniza.fri.sudora.adapter.NoteListener
import sk.uniza.fri.sudora.databinding.FragmentMainBinding


/**
 * Zobrazuje aktualny zoznam poznamok, s moznostou tlacitla pre vztvorenie novej poznamky
 */
class MainFragment : Fragment() {
    private val args by navArgs<MainFragmentArgs>()
    private val viewModel: NoteListViewModel by activityViewModels()
    lateinit var binding : FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //zobrazi action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater, R.layout.fragment_main, container, false)
        //tlacitlo pre pridanie dalsej poznamky
        binding.newNoteButton.setOnClickListener { view: View ->
            view.findNavController().navigate(MainFragmentDirections.actionMainFragmentToCreateNoteFragment())
        }
        //pridaj poznamku
        addNoteToNoteList()

        //adapter pre zvladnutie prace s poznamkami
        val adapter = NoteAdapter(viewModel, ListType.NOTE, NoteListener {}, this.context)
        binding.noteList.adapter = adapter
        binding.lifecycleOwner = this
        //observer zmeny zoznamu poznamok
        viewModel.noteList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
            adapter.notifyDataSetChanged()
            if (it.size > 0) {
                binding.textViewNote.text = ""
            } else {
                binding.textViewNote.text = getString(R.string.no_notes_to_display)
            }
        })
        //nastavi layout kazdej poznamky na celu sirku
        val manager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        binding.noteList.layoutManager = manager

        return binding.root
    }

    /**
     * Prida poznamku do zoznamu poznamok, ak sa tam uz nenachadza
     */
    private fun addNoteToNoteList(){
        var note = args.note
        //prida poznamku do zoznamu len ak sa rovnaka poznamka nenachadza v zoznamoch
        if(!isInViewModel() && note != null && note.noteTitle != "" && note.noteText != "") {
            //zisti pouzivatelovu preferenciu o pridani poznamky na prve miesto v zozname alebo na posledne
            val appSettingsPrefs: SharedPreferences = this.requireContext().getSharedPreferences(getString(R.string.app_settings_prefs), 0)
            val isNewNoteTopON: Boolean = appSettingsPrefs.getBoolean(getString(R.string.new_note_top), false)
            if (isNewNoteTopON){
                //ak je pouzivatelom dana preferencia na zobrazovanie novych poznamok hore prida poznamku hned za pripnute poznamky
                viewModel.noteList.value!!.add(viewModel.numberOfPinnedNotes, note)
            } else {
                //prida poznamku na koniec zoznamu
                viewModel.addNote(note, ListType.NOTE)
            }
            Snackbar.make(binding.newNoteButton, getString(R.string.note_added_successfully), Snackbar.LENGTH_SHORT).setAnchorView(binding.newNoteButton).show()
        }
    }

    /**
     * Zisti ci sa uz nachadza poznamku v zozname poznamok
     *
     * @return true ak je v niektorom zo zoznamov
     * @return false  ak sa nenachadza v ziadnom zozname
     */
    private fun isInViewModel() : Boolean {
        for (item in viewModel.noteList.value!!) {
            if (args.note?.noteId == item?.noteId) {
                return true
            }
        }
        for (item in viewModel.archiveList.value!!) {
            if (args.note?.noteId == item?.noteId) {
                return true
            }
        }
        for (item in viewModel.trashList.value!!) {
            if (args.note?.noteId == item?.noteId) {
                return true
            }
        }
        for (item in viewModel.noteListAllRemoved.value!!) {
            if (args.note?.noteId == item?.noteId) {
                return true
            }
        }
        return false
    }
}