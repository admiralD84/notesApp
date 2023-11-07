package uz.admiraldev.noteandtodoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.models.User;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private final List<User> users;
    private final UsersAdapter.UserItemButtonClick clickListener;

    public UsersAdapter(List<User> users, UserItemButtonClick clickListener) {
        this.users = users;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvLogin.setText(user.getUsername());
        holder.deleteBtn.setOnClickListener(deleteView -> clickListener.onDeleteBtnClicked(user));
        holder.editBtn.setOnClickListener(deleteView -> clickListener.onEditBtnClicked(position, user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogin;
        ImageView deleteBtn, editBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.tv_login);
            deleteBtn = itemView.findViewById(R.id.iv_user_delete);
            editBtn = itemView.findViewById(R.id.iv_user_edit);
        }
    }

    public interface UserItemButtonClick {
        void onDeleteBtnClicked(User user);

        void onEditBtnClicked(int position, User user);
    }
}
