package com.example.francisco.w3project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapWell;
import com.example.francisco.w3project.models.AmazonBook;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";

    private List<AmazonBook> amazonBookList;
    private Context context;

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context) {
        this.context = context;
        amazonBookList = new ArrayList<>();
    }

    public List<AmazonBook> getMovies() {
        return amazonBookList;
    }

    public void setMovies(List<AmazonBook> amazonBookList) {
        this.amazonBookList = amazonBookList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        AmazonBook amazonBook = amazonBookList.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder amazonBookVH = (ViewHolder) holder;

                if(amazonBook.getTitle() != null && !amazonBook.getTitle().trim().equals(""))
                    amazonBookVH.tvName.setText(amazonBook.getTitle());
                else
                    amazonBookVH.tvNameParent.setVisibility(amazonBookVH.tvNameParent.getRootView().GONE);

                if(amazonBook.getAuthor()!=null && !amazonBook.getAuthor().trim().equals(""))
                    amazonBookVH.tvAuthor.setText(amazonBook.getAuthor());
                else
                    amazonBookVH.tvAuthor.setVisibility(amazonBookVH.tvAuthor.getRootView().GONE);

                Picasso.with(context).load(amazonBook.getImageURL()).into(amazonBookVH.img, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        amazonBookVH.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //amazonBookVH.progress.setVisibility(View.GONE);
                    }
                });

                amazonBookVH.scroll.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        if(amazonBookVH.scroll.getChildAt(0).getHeight() > amazonBookVH.scroll_parent.getMeasuredHeight()) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                        return true;
                    }
                });

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return amazonBookList == null ? 0 : amazonBookList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == amazonBookList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(AmazonBook r) {
        amazonBookList.add(r);
        notifyItemInserted(amazonBookList.size() - 1);
    }

    public void addAll(List<AmazonBook> moveResults) {
        for (AmazonBook amazonBook : moveResults) {
            add(amazonBook);
        }
    }

    public void remove(AmazonBook r) {
        int position = amazonBookList.indexOf(r);
        if (position > -1) {
            amazonBookList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new AmazonBook());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = amazonBookList.size() - 1;
        AmazonBook amazonBook = getItem(position);

        if (amazonBook != null) {
            amazonBookList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public AmazonBook getItem(int position) {
        return amazonBookList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.img)
        BootstrapCircleThumbnail img;

        @Nullable
        @BindView(R.id.progress)
        ProgressBar progress;

        @Nullable
        @BindView(R.id.tvName)
        TextView tvName;

        @Nullable
        @BindView(R.id.tvAuthor)
        TextView tvAuthor;

        @Nullable
        @BindView(R.id.scroll)
        ScrollView scroll;

        @Nullable
        @BindView(R.id.scroll_parent)
        FrameLayout scroll_parent;

        @Nullable
        @BindView(R.id.tvNameParent)
        BootstrapWell tvNameParent;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
