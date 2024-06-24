package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.Activities.DetailActivity;
import com.example.movieapp.Domain.Slider.SliderItemList;
import com.example.movieapp.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private SliderItemList sliderItems;
    private ViewPager2 viewPager2;
    private Context context;
    private Handler sliderHandler = new Handler();
    private static final int VIRTUAL_ITEM_COUNT = 10000; // Số lượng mục ảo lớn

    public SliderAdapter(SliderItemList sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.slide_item_container, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int actualPosition = position % sliderItems.getSliderItems().size(); // Vị trí thực tế
        holder.setImage(sliderItems.getSliderItems().get(actualPosition).getSliderimg());
    }

    @Override
    public int getItemCount() {
        return VIRTUAL_ITEM_COUNT; // Trả về số lượng mục ảo
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);

            imageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int actualPosition = position % sliderItems.getSliderItems().size(); // Vị trí thực tế
                    String slug = sliderItems.getSliderItems().get(actualPosition).getSliderslug();
                    String link = sliderItems.getSliderItems().get(actualPosition).getSliderlink();
                    if (!TextUtils.isEmpty(slug)) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("slug", slug);
                        context.startActivity(intent);
                    } else if (!TextUtils.isEmpty(link)) {
                        // Kiểm tra và thêm giao thức nếu cần thiết
                        if (!link.startsWith("http://") && !link.startsWith("https://")) {
                            link = "http://" + link; // Thêm http:// nếu không có
                        }
                        Uri webpage = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(webpage);
                        context.startActivity(intent);
                    }

                }
            });
        }

        void setImage(String imageUrl) {
            // Tạo RequestOptions và thêm các hiệu ứng
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(60));

            // Sử dụng Glide để tải ảnh và áp dụng RequestOptions
            Glide.with(context)
                    .load(imageUrl) // Tải ảnh từ đường dẫn ảnh (URL)
                    .apply(requestOptions) // Áp dụng các hiệu ứng đã được cấu hình
                    .into(imageView); // Gán ảnh vào imageView
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager2.getCurrentItem();
            if (currentItem < VIRTUAL_ITEM_COUNT - 1) {
                viewPager2.setCurrentItem(currentItem + 1);
            } else {
                viewPager2.setCurrentItem(0);
            }

        }
    };

    public void startAutoScroll() {
        sliderHandler.postDelayed(runnable, 4000); // Auto-scroll mỗi 4 giây
    }

    public void stopAutoScroll() {
        sliderHandler.removeCallbacks(runnable);
    }
}
