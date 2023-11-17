package uz.admiraldev.noteandtodoapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.models.Note;
import uz.admiraldev.noteandtodoapp.viewmodels.NotesViewModel;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    List<Note> notes;
    Context context;
    List<Integer> selectedItems;
    NotesViewModel notesViewModel;
    private final OnNoteItemClickListener clickListener;

    public NotesAdapter(List<Note> notes, OnNoteItemClickListener clickListener, Context context, NotesViewModel notesViewModel) {
        this.context = context;
        this.notes = notes;
        this.clickListener = clickListener;
        this.notesViewModel = notesViewModel;
        this.selectedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.noteTitle.setText(note.getNotesTitle());
        holder.noteDescription.setText(note.getNotesSubtitle());
        String addedDays = note.getAddedDate() + " | ";
        String addedTimes = note.getAddedTime();
        holder.addedDay.setText(addedDays);
        holder.addedTime.setText(addedTimes);
        notesViewModel.selectedNotesList.observeForever(selectedNotes -> {
            if (selectedNotes.isEmpty()) {
                holder.itemCheckmark.setVisibility(View.GONE);
                holder.priorityMarker.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundResource(R.drawable.item_bg);
                switch (note.getPriority()) {
                    case 1:
                        holder.priorityMarker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_red)));
                        break;
                    case 2:
                        holder.priorityMarker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange)));
                        break;
                    case 3:
                        holder.priorityMarker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sea_green)));
                        break;
                }
            } else {
                holder.itemCheckmark.setVisibility(View.VISIBLE);
                if (selectedNotes.contains(note.getId())) {
                    holder.itemCheckmark.setImageTintList(ColorStateList.
                            valueOf(ContextCompat.getColor(context, R.color.sea_green)));
                    holder.itemView.setBackgroundResource(R.drawable.completed_item_bg);
                } else {
                    holder.itemCheckmark.setImageTintList(ColorStateList.
                            valueOf(ContextCompat.getColor(context, R.color.dark_grey)));
                    holder.itemView.setBackgroundResource(R.drawable.item_bg);
                }
            }
        });
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                notifyItemChanged(position);
                clickListener.onItemClick(position, note.getId());
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (clickListener != null) {
                notifyItemChanged(position);
                clickListener.onItemLongClicked(position, note.getId());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        if (notes == null) return 0;
        else return notes.size();
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteDescription, addedDay, addedTime;
        ImageView priorityMarker, itemCheckmark;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.tv_title);
            noteDescription = itemView.findViewById(R.id.tv_subtitle);
            addedDay = itemView.findViewById(R.id.tv_add_day);
            addedTime = itemView.findViewById(R.id.tv_add_time);
            priorityMarker = itemView.findViewById(R.id.priority_mark);
            itemCheckmark = itemView.findViewById(R.id.check_mark);
        }
    }

    public interface OnNoteItemClickListener {
        void onItemClick(int position, int id);

        void onItemLongClicked(int position, int id);

    }
}
