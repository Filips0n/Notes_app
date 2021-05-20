package sk.uniza.fri.sudora

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sk.uniza.fri.sudora.notes.Note
import java.util.*

class NoteListViewModel : ViewModel() {
    fun addNote(note: Note, type: ListType, position: Int = -1) {
        when(type) {
            ListType.NOTE ->{
                if (position == -1) {
                    __notesList.add(note)
                } else {
                    __notesList.add(position, note)
                }
                _notesList.postValue(__notesList)
            }
            ListType.ARCHIVE ->{
                __archiveList.add(note)
                _archiveList.postValue(__archiveList)
            }
            ListType.TRASH ->{
                __trashList.add(note)
                _trashList.postValue(__trashList)
            }
        }
    }

    fun removeNote(note: Note, type: ListType) {
        when(type) {
            ListType.NOTE ->{
                __notesList.remove(note)
                __noteListAllRemoved.add(note)
                _notesList.postValue(__notesList)
            }
            ListType.ARCHIVE ->{
                __archiveList.remove(note)
                _archiveList.postValue(__archiveList)
            }
            ListType.TRASH ->{
                __trashList.remove(note)
                _trashList.postValue(__trashList)
            }
        }
    }

    fun addToViewModel(noteData: MutableList<Note?>, archiveData: MutableList<Note?>, trashData: MutableList<Note?>) {
        __notesList = noteData
        _notesList.postValue(__notesList)
        __archiveList = archiveData
        _archiveList.postValue(__archiveList)
        __trashList = trashData
        _trashList.postValue(__trashList)
        _numberOfPinnedNotes = 0
        for (item in __notesList) {
            if (item!!.isPinned) {
                _numberOfPinnedNotes +=1
            }
        }
    }

    fun increaseNumberOfPinnedNotes() {
        _numberOfPinnedNotes+=1
    }

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