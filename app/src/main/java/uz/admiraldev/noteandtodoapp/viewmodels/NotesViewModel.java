package uz.admiraldev.noteandtodoapp.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.models.Note;

public class NotesViewModel extends ViewModel {
    private List<Note> notes;
    private Note selectedNote;
    private Note enteredNote;
    private boolean readOnlyMode = false;
    private final ExecutorService myExecutor = Executors.newSingleThreadExecutor();
    public MutableLiveData<List<Integer>> selectedNotesList = new MutableLiveData<>();
    public MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();
    public final MutableLiveData<Note> selectedNoteLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isUpdateNote = new MutableLiveData<>();

    public boolean getReadOnlyMode() {
        return readOnlyMode;
    }

    public boolean getEditMode() {
        if (isUpdateNote.getValue() != null)
            return isUpdateNote.getValue();
        else
            return false;
    }

    public void setReadOnlyMode(boolean isReadOnly) {
        this.readOnlyMode = isReadOnly;
    }

    public void setEditMode(Boolean isUpdateNote) {
        this.isUpdateNote.setValue(isUpdateNote);
    }

    public void setSelectedNote(int position) {
        selectedNoteLiveData.setValue(notes.get(position));
        selectedNote = notes.get(position);
    }

    // bazadan hamma qaydlarni o'qish
    public void getNotesFromDB() {
        myExecutor.execute(() -> {
            try {
                notes = MainActivity.getAppDataBase().noteDao().getNotes();
                notesLiveData.postValue(notes);
            } catch (Exception e) {
                Log.d("myTag", "getNotesLiveData error: " + e.getMessage());
            }
        });
    }

    public void getSortedNoteList(String fieldName, boolean isASC) {
        myExecutor.execute(() -> {
            try {
                if (fieldName.equals("addedDate"))
                    notes = MainActivity.getAppDataBase().noteDao().getSortedNotesByDate(isASC);
                else if (fieldName.equals("priority"))
                    notes = MainActivity.getAppDataBase().noteDao().getSortedNotesByPriority(isASC);
                notesLiveData.postValue(notes);
            } catch (Exception e) {
                Log.d("myTag", "getNotesLiveData error: " + e.getMessage());
            }
        });
    }

    public void clearSelectedNotesList() {
        selectedNotesList.setValue(new ArrayList<>());
    }

    // tanlangan qaydlarni id sini listga qo'shib borish
    public void addSelectedNoteId(int noteId) {
        if (selectedNotesList.getValue() == null) {
            selectedNotesList.setValue(new ArrayList<>());
            selectedNotesList.getValue().add(noteId);
        } else {
            if (selectedNotesList.getValue().contains(noteId))
                selectedNotesList.getValue().remove(Integer.valueOf(noteId));
            else
                selectedNotesList.getValue().add(noteId);
        }
    }

    public void updateNote(String noteTitle, String noteDescription, int priority) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        Objects.requireNonNull(selectedNoteLiveData.getValue()).setNotesTitle(noteTitle);
        selectedNoteLiveData.getValue().setNotesSubtitle(noteDescription);
        selectedNoteLiveData.getValue().setPriority(priority);
        selectedNoteLiveData.getValue().setAddedDate("edited: " + currentDate);
        selectedNoteLiveData.getValue().setAddedTime(currentTime);
        myExecutor.execute(() -> {
            try {
                MainActivity.getAppDataBase().noteDao().updateNote(selectedNoteLiveData.getValue());
            } catch (Exception e) {
                Log.d("myTag", "updateNote error: " + e.getMessage());
            }
        });
    }

    // qaydni bazaga saqlash
    public void saveData(String noteTitle, String noteDescription, int priority) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());
        enteredNote = new Note(noteTitle, noteDescription, currentDate, currentTime, priority);
        myExecutor.execute(() -> {
            try {
                MainActivity.getAppDataBase().noteDao().insert(enteredNote);
            } catch (Exception e) {
                Log.d("myTag", "msg: " + e.getMessage());
            }
        });
    }

    // tanlangan qaydlar listidagi id lar bo'yicha bazadan o'chirvorish
    public void deleteNote() {
        if (selectedNotesList.getValue() == null || selectedNotesList.getValue().isEmpty())
            deleteNoteById();
        else {
            deleteNotesByIds();
        }
    }

    public void deleteNoteById() {
        myExecutor.execute(() -> {
            try {
                MainActivity.getAppDataBase().noteDao().delete(selectedNote);
                new Handler(Looper.getMainLooper()).post(() -> {
                    getNotesFromDB();
                    clearSelectedNotesList();
                });
            } catch (Exception e) {
                Log.d("myTag", "delete note error: " + e.getMessage());
            }
        });
    }

    public void deleteNotesByIds() {
        myExecutor.execute(() -> {
            try {
                MainActivity.getAppDataBase().noteDao()
                        .deleteItemByIds((ArrayList<Integer>) selectedNotesList.getValue());
                new Handler(Looper.getMainLooper()).post(() -> {
                    getNotesFromDB();
                    clearSelectedNotesList();
                });
            } catch (Exception e) {
                Log.d("myTag", "delete notes error: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        myExecutor.shutdown();
    }

    public void searchNotes(String searchNoteText) {
        myExecutor.execute(() -> {
            try {
                List<Note> findNotes = MainActivity.getAppDataBase().noteDao().findNotes(searchNoteText);
                notesLiveData.postValue(findNotes);
            } catch (Exception e) {
                Log.d("myTag", "getAllNotes error: " + e.getMessage());
            }
        });
    }
}
