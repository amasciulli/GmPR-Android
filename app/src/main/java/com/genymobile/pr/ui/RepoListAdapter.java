package com.genymobile.pr.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genymobile.pr.R;
import com.genymobile.pr.model.Repo;

import java.util.ArrayList;
import java.util.List;

public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {
    private List<Repo> repos = new ArrayList<>();
    private ItemClickListener<Repo> itemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repo, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Repo repo = repos.get(position);
        holder.nameView.setText(repo.getName());
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(repo, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    public void setRepos(List<Repo> repos) {
        this.repos.addAll(repos);
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener<Repo> listener) {
        itemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
