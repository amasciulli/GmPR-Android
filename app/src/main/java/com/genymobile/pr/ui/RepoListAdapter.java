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
    private static final int TYPE_REPO = 0;
    private static final int TYPE_PULL_REQUEST = 1;

    private List<Object> list = new ArrayList<>();
    private ItemClickListener<PullRequest> itemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root;
        switch (viewType) {
            case TYPE_REPO:
                root = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_repo, parent, false);
                return new RepoViewHolder(root);
            case TYPE_PULL_REQUEST:
                root = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_pull_request, parent, false);
                return new PullRequestViewHolder(root);
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_REPO:
                bindRepoViewHolder((RepoViewHolder) holder, (Repo) list.get(position));
                break;
            case TYPE_PULL_REQUEST:
                bindPullRequestViewHolder((PullRequestViewHolder) holder, (PullRequest) list.get(position));
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }

    private void bindRepoViewHolder(RepoViewHolder holder, Repo repo) {
        holder.nameView.setText(repo.getName());
    }

    private void bindPullRequestViewHolder(PullRequestViewHolder holder, PullRequest pullRequest) {
        holder.titleView.setText(pullRequest.getTitle());
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

    @Override
    public int getItemViewType(int position) {
        return list.get(position) instanceof Repo ? TYPE_REPO : TYPE_PULL_REQUEST;
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class RepoViewHolder extends ViewHolder {
        private final TextView nameView;

        public RepoViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.name);
        }
    }

    class PullRequestViewHolder extends ViewHolder {
        private final TextView titleView;

        public PullRequestViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
