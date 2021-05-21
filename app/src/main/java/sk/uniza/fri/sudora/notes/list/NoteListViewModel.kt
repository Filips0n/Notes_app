package sk.uniza.fri.sudora.notes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sk.uniza.fri.sudora.notes.Note

/**
 * Uklada poznamky do zoznamov pocas behu aplikacie
 */
class NoteListViewModel : ViewModel() {

    /**
     * Prida poznamku do zadaneho zoznamu na zadane miesto
     *
     * @param note poznamka na pridanie
     * @param type typ zoznamu kam sa ma poznamka pridat
     * @param position na aku poziciu sa ma poznamka pridat
     */
    fun addNote(note: Note, type: ListType, position: Int = -1) {
        when(type) {
            ListType.NOTE ->{
                //ak sa nenastavi hodnota position prida na koniec zoznamu
                if (position == -1) {
                    __notesList.add(note)
                } else {
                    __notesList.add(position, note)
                }
                //nastavi zoznam poznamok z vedlajsieho vlakna
                _notesList.postValue(__notesList)
            }
            ListType.ARCHIVE ->{
                __archiveList.add(note)
                //nastavi zoznam poznamok z vedlajsieho vlakna
                _archiveList.postValue(__archiveList)
            }
            ListType.TRASH ->{
                __trashList.add(note)
                //nastavi zoznam poznamok z vedlajsieho vlakna
                _trashList.postValue(__trashList)
            }
        }
    }

    /**
     * Odstrani zadanu poznamku zo zadaneho zoznamu
     *
     * @param note poznamka na odstranenie
     * @param type typ zoznamu, z ktoreho sa odstrani poznamka
     */
    fun removeNote(note: Note, type: ListType) {
        when(type) {
            ListType.NOTE ->{
                //odstrani poznamku zo zoznamu
                __notesList.remove(note)
                //je potrebne si zaznamenavat aj poznamky, ktore boli odstranene zo zoznamu
                //kedze v safe args zostava hodnota z CreateNoeFragment, poznamky by sa inak duplikovali
                __noteListAllRemoved.add(note)
                //nastavi zoznam poznamok z vedlajsieho vlakna
                _notesList.postValue(__notesList)
            }
            ListType.ARCHIVE ->{
                __archiveList.remove(note)
                //nastavi zoznam poznamok z vedlajsieho vlakna
                _archiveList.postValue(__archiveList)
            }
            ListType.TRASH ->{
                __trashList.remove(note)
                //nastavi zoznam poznamok z vedlajsieho vlakna
                _trashList.postValue(__trashList)
            }
        }
    }

    /**
     * Nacita do viewmodelu vsetky zoznamy
     *
     * @param noteData zoznam hlavnych poznamok
     * @param archiveData zoznam archivovanych poznamok
     * @param trashData zoznam poznamok v kosi
     */
    fun addToViewModel(noteData: MutableList<Note?>, archiveData: MutableList<Note?>, trashData: MutableList<Note?>) {
        //prirad data k zoznamu
        __notesList = noteData
        //nastavi zoznam poznamok z vedlajsieho vlakna
        _notesList.postValue(__notesList)
        //prirad data k zoznamu
        __archiveList = archiveData
        //nastavi zoznam poznamok z vedlajsieho vlakna
        _archiveList.postValue(__archiveList)
        //prirad data k zoznamu
        __trashList = trashData
        //nastavi zoznam poznamok z vedlajsieho vlakna
        _trashList.postValue(__trashList)
        //zisti pocet pripnutych poznamok v hlavnom zozname
        _numberOfPinnedNotes = 0
        for (item in __notesList) {
            if (item!!.isPinned) {
                _numberOfPinnedNotes +=1
            }
        }
    }

    /**
     * Zvysi o 1 pocet prvkov v pripnutych poznamkach
     */
    fun increaseNumberOfPinnedNotes() {
        _numberOfPinnedNotes+=1
    }

    /**
     * Znizi o 1 pocet prvkov v pripnutych poznamkach
     */
    fun decreaseNumberOfPinnedNotes() {
        _numberOfPinnedNotes-=1
    }

    private var __notesList = mutableListOf<Note?>()
    private var __archiveList = mutableListOf<Note?>()
    private var __trashList = mutableListOf<Note?>()

    private var __noteListAllRemoved = mutableListOf<Note?>()

    private var _numberOfPinnedNotes : Int = 0
    val numberOfPinnedNotes : Int
        get() = _numberOfPinnedNotes

    private var _notesList = MutableLiveData<MutableList<Note?>>().apply { this.value = __notesList }
    val noteList : LiveData<MutableList<Note?>>
        get() = _notesList

    private var _archiveList = MutableLiveData<MutableList<Note?>>().apply { this.value = __archiveList }
    val archiveList : LiveData<MutableList<Note?>>
        get() = _archiveList

    private var _trashList = MutableLiveData<MutableList<Note?>>().apply { this.value = __trashList }
    val trashList : LiveData<MutableList<Note?>>
        get() = _trashList

    private var _noteListAllRemoved = MutableLiveData<MutableList<Note?>>().apply { this.value = __noteListAllRemoved }
    val noteListAllRemoved : LiveData<MutableList<Note?>>
        get() = _noteListAllRemoved
}