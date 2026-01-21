package com.example.electronicgradebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private final List<Group> groups;

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.textGroupName.setText(group.getName());
        
        // Получаем количество студентов в группе
        int studentCount = 0;
        for (Student student : MockData.getStudents().values()) {
            if (student.getGroupId().equals(group.getId())) {
                studentCount++;
            }
        }
        holder.textStudentCount.setText(studentCount + " студентов");
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textGroupName;
        TextView textStudentCount;

        GroupViewHolder(View itemView) {
            super(itemView);
            textGroupName = itemView.findViewById(R.id.textGroupName);
            textStudentCount = itemView.findViewById(R.id.textStudentCount);
        }
    }
} 