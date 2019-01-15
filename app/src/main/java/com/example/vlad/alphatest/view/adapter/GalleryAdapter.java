package com.example.vlad.alphatest.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.vlad.alphatest.R;
import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.interfaceses.listeners.OnImageClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private List<Image> images;
    private OnImageClickListener listener;
    private boolean clickable = true;

    public GalleryAdapter(List<Image> urls, OnImageClickListener listener) {
        this.images = urls;
        this.listener = listener;
        Timber.d("Adapter inited, size: = %s", images.size());
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_recyclerview_item, viewGroup, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int i) {
        photoViewHolder.bindHolder(images.get(i));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addPhoto(Image image) {
        images.add(image);
        notifyItemInserted(images.size());
        Timber.d("Add image: size = %s", images.size());
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        @OnClick(R.id.image)
        void onClickImage() {
            if (clickable) {
                if (listener != null)
                    listener.onImageClick(image.getDrawable());
            }
        }

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindHolder(Image model) {
            progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(model.getUrl())
                    .resize(768, 1024)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            //set error icon
                        }
                    });
        }
    }
}
