package sk.uniza.fri.sudora.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sk.uniza.fri.sudora.*
import sk.uniza.fri.sudora.ListType.ARCHIVE
import sk.uniza.fri.sudora.ListType.TRASH
import sk.uniza.fri.sudora.databinding.NoteViewBinding
import sk.uniza.fri.sudora.notes.Note
import sk.uniza.fri.sudora.notes.NoteColor
import java.util.*
import sk.uniza.fri.sudora.ListType.NOTE as NOTE1


class NoteAdapter(
        private val viewModel: NoteListViewModel,
        private val listType: ListType,
        private val clickListener: NoteListener,
        private val context: Context?
)
    : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteAdapterDiffCallback()){

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        try {
            val item = getItem(position)
            //dam data do view holdera
            holder.moveDataToViewHolder(viewModel, item, listType, context!!)
            holder.bind(item, clickListener)
            //nastavim titulok z itemu
            holder.binding.noteTitleView.text = item?.noteTitle
            //nastavim text z itemu
            holder.binding.noteTextView.text = item?.noteText
            //zistim ci je item pripnuty, ak ano nastavim mu specialne podfarbenie
            if (item.isPinned) {
                holder.itemView.setBackgroundResource(R.drawable.note_card_pinned)
            } else {
                //ak nie je pripnuty, nastavim mu podfarbenie podla atributu v poznamke
                holder.itemView.setBackgroundResource(getBackgroundColorOfItem(item))
            }
        } catch (e: NullPointerException) {}
    }

    /**
     * Podla farby nastavenej v poznamke urci akej farby ma byt poznamka
     *
     * @param note poznamka, ktorej chceme zistit farbu
     *
     * @return cestu suboru k podfarbeniu poznamky
     */
    private fun getBackgroundColorOfItem(note: Note): Int {
        when(note.color) {
            NoteColor.YELLOW -> return R.drawable.note_card_yellow
            NoteColor.GREEN -> return R.drawable.note_card_green
            NoteColor.BLUE -> return R.drawable.note_card_blue
            NoteColor.RED -> return R.drawable.note_card_red
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.from(parent)
    }

    class NoteViewHolder constructor(val binding: NoteViewBinding): RecyclerView.ViewHolder(binding.root) {
        lateinit var context: Context
        lateinit var note : Note
        lateinit var viewModel: NoteListViewModel
        lateinit var listType: ListType

        lateinit var deleteDialog : MaterialAlertDialogBuilder
        private val greenColorPickerButton = binding.greenColorPicker
        private val blueColorPickerButton = binding.blueColorPicker
        private val yellowColorPickerButton = binding.yellowColorPicker
        private val redColorPickerButton = binding.redColorPicker
        private val colorPaletteBackground = binding.colorPaletteBackground
        //zoznam vsetkych buttonov a pozadia, ktore sa ukryju/ zobrazia ked sa klikne na tlacidlo palety
        private val colorPaletteButtons : List<ImageView> = listOf(colorPaletteBackground,  greenColorPickerButton, blueColorPickerButton, yellowColorPickerButton, redColorPickerButton)
        var isPaletteOpen = false

        private val titleButton = binding.noteTitleView
        private val textButton = binding.noteTextView

        fun bind(item: Note, clickListener: NoteListener) {
            //nastavim si ako note aktualny item
            binding.note = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NoteViewBinding.inflate(layoutInflater, parent, false)
                return NoteViewHolder(binding).buttonListener()
            }
        }

        /**
         * Pocuvac na vsetky tlacitka poznamky
         */
        private fun buttonListener(): NoteViewHolder {
            //tlacidlo vymazania z aktualneho zoznamu
            binding.btnDelete.setOnClickListener {
                when(listType){
                    NOTE1 -> {
                        viewModel.removeNote(note, NOTE1)
                        //ak je poznamka pripnuta zmenim jej pozadie a zmensim pocet pripnutych poznamok
                        if(note.isPinned) {
                            setColorOfBackground(note)
                            note.isPinned = false
                            viewModel.decreaseNumberOfPinnedNotes()
                        }
                        viewModel.addNote(note, TRASH)
                    }
                    ARCHIVE -> {
                        viewModel.removeNote(note, ARCHIVE)
                        viewModel.addNote(note, TRASH)
                    }
                    //opytam sa ci chce pouzivatel skutocne vymazat poznamku
                    TRASH -> {
                        deleteDialog.setPositiveButton(R.string.delete_dialog_yes_button_label)
                        { _, _ ->
                            viewModel.removeNote(note, TRASH)
                        }.create().show()
                    }
                }
            }
            //tlacidlo pre presunutie poznamky do archivu
            binding.btnArchive.setOnClickListener {
                when(listType){
                    NOTE1 -> {
                        viewModel.removeNote(note, NOTE1)
                        //ak je poznamka pripnuta nastavim farbu zadanu v poznamke a znizim pocet
                        //pripnutych poznamok v zozname
                        if (note.isPinned){
                            setColorOfBackground(note)
                            note.isPinned = false
                            viewModel.decreaseNumberOfPinnedNotes()
                        }
                        viewModel.addNote(note, ARCHIVE)
                    }
                    ARCHIVE -> {
                        viewModel.removeNote(note, ARCHIVE)
                        viewModel.addNote(note, NOTE1)
                    }
                    TRASH -> {
                        viewModel.removeNote(note, TRASH)
                        viewModel.addNote(note, NOTE1)
                    }
                }
            }
            //tlacidlo pre spravu palety, skryje/zobrazi vsetky tlacitka a pozadie, ktorych sa to tyka
            setColorPaletteVisibility(View.INVISIBLE)
            binding.btnPalette.setOnClickListener{
                if (isPaletteOpen) {
                    setColorPaletteVisibility(View.INVISIBLE)
                    isPaletteOpen = false
                } else {
                    setColorPaletteVisibility(View.VISIBLE)
                    isPaletteOpen = true
                }
            }

            //ak je stlacene tlacidlo s danou farbou, zmeni pozadanie aktualnej poznamky
            //a skryje vsetky tlacidla, ktore patria pod paletu
            yellowColorPickerButton.setOnClickListener {
                note.color = NoteColor.YELLOW
                itemView.setBackgroundResource(R.drawable.note_card_yellow)
                setColorPaletteVisibility(View.INVISIBLE)
                isPaletteOpen = false
            }
            greenColorPickerButton.setOnClickListener {
                note.color = NoteColor.GREEN
                itemView.setBackgroundResource(R.drawable.note_card_green)
                setColorPaletteVisibility(View.INVISIBLE)
                isPaletteOpen = false
            }
            blueColorPickerButton.setOnClickListener {
                note.color = NoteColor.BLUE
                itemView.setBackgroundResource(R.drawable.note_card_blue)
                setColorPaletteVisibility(View.INVISIBLE)
                isPaletteOpen = false
            }
            redColorPickerButton.setOnClickListener {
                note.color = NoteColor.RED
                itemView.setBackgroundResource(R.drawable.note_card_red)
                setColorPaletteVisibility(View.INVISIBLE)
                isPaletteOpen = false
            }
            //zisti ci sa stlacil nadpis na poznamke, ak ano prejde na jej upravovanie
            titleButton.setOnClickListener {
                when(listType) {
                    ListType.NOTE -> it.findNavController().navigate(MainFragmentDirections.actionMainFragmentToCreateNoteFragment(note))
                    ARCHIVE -> it.findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToCreateNoteFragment(note))
                    TRASH -> it.findNavController().navigate(TrashFragmentDirections.actionTrashFragmentToCreateNoteFragment(note))
                }
            }
            //zisti ci sa stlacil text na poznamke, ak ano prejde na jej upravovanie
            textButton.setOnClickListener {
                when(listType) {
                    ListType.NOTE -> it.findNavController().navigate(MainFragmentDirections.actionMainFragmentToCreateNoteFragment(note))
                    ARCHIVE -> it.findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToCreateNoteFragment(note))
                    TRASH -> it.findNavController().navigate(TrashFragmentDirections.actionTrashFragmentToCreateNoteFragment(note))
                }
            }
            //tlacidlo pripnutia poznamky
            binding.btnPin.setOnClickListener {
                if(note.isPinned) {
                    //ak bola poznamka pripnuta a pouzivatel sa ju rozhodne odpripnut,
                    note.isPinned = false
                    //nepripnutym poznamkam sa zobrazuje paleta
                    binding.btnPalette.visibility = View.VISIBLE
                    binding.btnPalette.isClickable = true
                    //zisti pocet poznamok pred pripnutim, kvoli osetreniu chyby
                    val previousNumberOfItems = viewModel.noteList.value!!.size
                    val noteMoved = note
                    //odstrani poznamku, znizi pocet pripnutych poznamok a prida poznamku na nove miesto
                    viewModel.removeNote(note, NOTE1)
                    viewModel.decreaseNumberOfPinnedNotes()
                    viewModel.addNote(noteMoved, NOTE1, viewModel.numberOfPinnedNotes)
                    //nastavi pozadie poznamky podla toho aka farba je v poznamke
                    setColorOfBackground(note)
                    //zmeni obrazok pripnutia na nepripnuta
                    binding.btnPin.setImageResource(R.drawable.pin)
                    //zistim kolko je aktualne poznamok v zozname
                    val newNumberOfNotes = viewModel.noteList.value!!.size
                    //ak sa pocet poznamok nezhoduje odstran poznamku a zvys pocet pripnutych poznamok
                    if (previousNumberOfItems != newNumberOfNotes) {
                        viewModel.removeNote(note, NOTE1)
                        viewModel.increaseNumberOfPinnedNotes()
                    }
                } else{
                    //ak nebola poznamka pripnuta a pouzivatel ju pripne
                    note.isPinned = true
                    //pripnutym poznamkam sa nezobrazuje paleta
                    binding.btnPalette.visibility = View.INVISIBLE
                    binding.btnPalette.isClickable = false
                    //zisti pocet poznamok pred pripnutim, kvoli osetreniu chyby
                    val previousNumberOfItems = viewModel.noteList.value!!.size
                    val noteMoved = note
                    //odstrani poznamku, znizi pocet pripnutych poznamok a prida poznamku na nove miesto
                    viewModel.removeNote(note, NOTE1)
                    viewModel.increaseNumberOfPinnedNotes()
                    viewModel.addNote(noteMoved, NOTE1, 0)
                    //nastavi pozadie poznamky na typ pripnutej a zmen obrazok pripnutia
                    itemView.setBackgroundResource(R.drawable.note_card_pinned)
                    binding.btnPin.setImageResource(R.drawable.ic_baseline_push_pin_24)
                    //ak sa pocet poznamok nezhoduje odstran poznamku a zniz pocet pripnutych poznamok
                    val newNumberOfNotes = viewModel.noteList.value!!.size
                    if (previousNumberOfItems != newNumberOfNotes) {
                        viewModel.removeNote(note, NOTE1)
                        viewModel.decreaseNumberOfPinnedNotes()
                    }
                }
            }
            //tlacidlo zdialania
            binding.btnShare.setOnClickListener {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.note_title) + binding.noteTitleView.text + "\n" +context.getString(
                                            R.string.note_text) + binding.noteTextView.text)
                    this.type = context.getString(R.string.text_plaint)
                }
                context.startActivity(shareIntent)
            }
            return this
        }

        //nastavenie pozadia poznamky podla farby ulozenej v poznamke
        private fun setColorOfBackground(note : Note) {
            when(note.color) {
                NoteColor.YELLOW -> itemView.setBackgroundResource(R.drawable.note_card_yellow)
                NoteColor.GREEN -> itemView.setBackgroundResource(R.drawable.note_card_green)
                NoteColor.BLUE -> itemView.setBackgroundResource(R.drawable.note_card_blue)
                NoteColor.RED -> itemView.setBackgroundResource(R.drawable.note_card_red)
            }
        }

        /**
         * Presunie data do view holdera
         *
         * @param viewModel viewModel, z ktoreho sa preberaju zoznamy
         * @param note aktualna poznamka
         * @param listType typ aktulalneho zoznamu
         * @param context context z fragmentu
         */
        fun moveDataToViewHolder(viewModel: NoteListViewModel, note: Note?, listType: ListType, context: Context) {
            this.note = note!!
            this.viewModel = viewModel
            this.listType = listType
            this.context = context
            this.deleteDialog = MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.delete_dialog_title)
                    .setMessage(R.string.delete_dialog_text)
                    .setNegativeButton(R.string.delete_dialog_no_button_label)
                    { _, _ -> }
            if (note.isPinned) {
                binding.btnPin.setImageResource(R.drawable.ic_baseline_push_pin_24)
                binding.btnPalette.visibility = View.INVISIBLE
                binding.btnPalette.isClickable = false
            } else {
                binding.btnPin.setImageResource(R.drawable.pin)
                binding.btnPalette.visibility = View.VISIBLE
                binding.btnPalette.isClickable = true
            }
            when(listType) {
                ARCHIVE -> {
                    binding.btnArchive.setImageResource(R.drawable.ic_baseline_notes_24)
                    binding.btnPin.isClickable = false
                    binding.btnPin.visibility = View.INVISIBLE
                }
                TRASH -> {
                    binding.btnArchive.setImageResource(R.drawable.ic_baseline_notes_24)
                    binding.btnPin.isClickable = false
                    binding.btnPin.visibility = View.INVISIBLE
                    binding.btnShare.isClickable = false
                    binding.btnShare.visibility = View.INVISIBLE
                }
                else ->{}
            }
        }
        fun setColorPaletteVisibility(visibility : Int, colorPaletteViewList : List<View> = colorPaletteButtons){
            for(view in colorPaletteViewList)
                view.visibility = visibility
        }
    }


    class NoteAdapterDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

}
class NoteListener(val clickListener: (noteId: UUID) -> Unit) {
    fun onClick(note: Note) = clickListener(note.noteId)
}