package com.genymobile.pr.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genymobile.pr.R;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import java.util.ArrayList;
import java.util.List;

public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {
    private List<Object> list = new ArrayList<>();
    private ItemClickListener<PullRequest> itemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repo, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Object item = list.get(position);
        if (item instanceof Repo) {
            holder.nameView.setText(((Repo) item).getName());
        } else {
            holder.nameView.setText(((PullRequest) item).getTitle());
            if (itemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick((PullRequest) item, position);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(ItemClickListener<PullRequest> listener) {
        itemClickListener = listener;
    }

    public void addRepo(Repo repo, List<PullRequest> pullRequests) {
        int previousCount = getItemCount();
        list.add(repo);
        list.addAll(pullRequests);
        notifyItemRangeInserted(previousCount - 1, pullRequests.size() + 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
