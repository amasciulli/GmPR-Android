package com.genymobile.pr.ui;

import com.genymobile.pr.R;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {
    private static final int TYPE_REPO = 0;
    private static final int TYPE_PULL_REQUEST = 1;
    private static final String FORMAT_TAG_LOGIN_START = "<font color=#333333>";
    private static final String FORMAT_TAG_LOGIN_END = "</font>";

    private List<Object> list = new ArrayList<>();
    private ItemClickListener<PullRequest> pullRequestClickListener;
    private ItemClickListener<Repo> repoClickListener;
    private Transformation circleTransformation = new CircleTransformation();

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

    private void bindRepoViewHolder(RepoViewHolder holder, final Repo repo) {
        holder.nameView.setText(repo.getName());

        if (repoClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repoClickListener.onClick(repo);
                }
            });
        }
    }

    private void bindPullRequestViewHolder(PullRequestViewHolder holder, final PullRequest pullRequest) {
        Context context = holder.itemView.getContext();
        holder.titleView.setText(pullRequest.getTitle());

        holder.avatarView.setImageDrawable(null);
        Picasso.with(context)
                .load(pullRequest.getUser().getAvatarUrl())
                .transform(circleTransformation)
                .into(holder.avatarView);

        String body = pullRequest.getBody();
        String login = formatLogin(pullRequest.getUser().getLogin());
        if (body.isEmpty()) {
            body = context.getString(R.string.no_description_provided);
        }
        holder.bodyView.setText(Html.fromHtml(context.getString(R.string.pr_login_body, login, body)));


        if (pullRequestClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pullRequestClickListener.onClick(pullRequest);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    pullRequestClickListener.onLongClick(pullRequest);
                    return true;
                }
            });
        }
    }

    private String formatLogin(String login) {
        return FORMAT_TAG_LOGIN_START + login + FORMAT_TAG_LOGIN_END;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setPullRequestClickListener(ItemClickListener<PullRequest> listener) {
        pullRequestClickListener = listener;
    }

    public void setRepoClickListener(ItemClickListener<Repo> listener) {
        repoClickListener = listener;
    }

    public void addRepo(Repo repo, List<PullRequest> pullRequests) {
        int previousCount = getItemCount();
        list.add(repo);
        list.addAll(pullRequests);
        notifyItemRangeInserted(previousCount, pullRequests.size() + 1);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) instanceof Repo ? TYPE_REPO : TYPE_PULL_REQUEST;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
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
        private final TextView bodyView;
        private final ImageView avatarView;

        public PullRequestViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
            bodyView = (TextView) itemView.findViewById(R.id.body);
            avatarView = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }
}
