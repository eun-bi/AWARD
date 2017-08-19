package com.example.user.myapplication.MyFeedAward;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pools;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.AwardResult.AwardResultActivity;
import com.example.user.myapplication.Nominate;
import com.example.user.myapplication.NominateActivity;
import com.example.user.myapplication.R;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-08.
 */
public class MyFeedAwardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final static String nominate_URL = Award.AWARD_URL + "Award_server/Award/nominate_header/";
    private final static String IMAGE_URL = Award.IMAGE_URL + "Image/";

    private static final int TYPE_HEADER = 2;

    private static final int TYPE_ITEM = 1;

    private static final int MAX_POOL_SIZE = 10;

    private Context mContext;
    private Pools.SimplePool<View> mMyViewPool;
    private ArrayList<MyFeedAward> myFeedAward_list;
    private ArrayList<Nominate> nominate_list;

    String item_field = null;

    public MyFeedAwardAdapter(Context context, ArrayList<MyFeedAward> awards_list, String nominates) {
        mContext = context;
        myFeedAward_list = awards_list;
        mMyViewPool = new Pools.SynchronizedPool< >(MAX_POOL_SIZE);
        nominate_list = new ArrayList<>();
        getNominates(nominates);
    }

    private void getNominates(String nominates) {
        try {
            Gson gson = new Gson();
            Log.d("nominates", "func : " + nominates);
            JSONArray jArrNominates = new JSONArray(nominates);

            if(jArrNominates.length() == 0) {
                Log.d("nominates", "func : 비었다");
            }
            else {
                for(int i = 0; i < jArrNominates.length(); i++) {
                    Log.d("nominates", "for : " + Integer.toString(jArrNominates.length()));
                    String nominatesInfo = jArrNominates.getJSONObject(i).toString();
                    Nominate nominate = gson.fromJson(nominatesInfo, Nominate.class);
                    nominate_list.add(nominate);
                //    Log.d("nominates", "data : " + nominate_list.get(i).getNominate_img_path());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_feed_myaward, parent, false);
            return new MyAwardItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.header_myaward, parent, false);
            return new MyAwardHeaderViewHolder(view);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!isPositionHeader(position)) {
            /* content */
            final MyFeedAward award = myFeedAward_list.get(position - 1);

            int field = award.getField();
            switch (field) {
                case 1 :
                    item_field = "영화 어워드";
                    break;
                case 2 :
                    item_field = "애니메이션 어워드";
                    break;
                case 3 :
                    item_field = "드라마 어워드";
                    break;
                case 4 :
                    item_field = "웹툰 어워드";
                    break;
                case 5 :
                    item_field = "만화 어워드";
                    break;
                case 6 :
                    item_field = "소설 어워드";
                    break;
                case 7 :
                    item_field = "음악 어워드";
                    break;
                case 8 :
                    item_field = "연극 어워드";
                    break;
                case 9 :
                    item_field = "뮤지컬 어워드";
                    break;
                case 10 :
                    item_field = "게임 어워드";
                    break;
                default :
                    item_field = "어워드";
            }

            MyAwardItemViewHolder vi = (MyAwardItemViewHolder) viewHolder;
            vi.award_field.setText(item_field);
            vi.award_title.setText(award.getTitle());

            Glide
                        .with(mContext)
                        .load(IMAGE_URL + award.getAward_img())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .fitCenter()
                        .centerCrop()
                        .crossFade()
                        .thumbnail(0.1f)
                        .into(vi.award_item_img);



            vi.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* 아이템을 클릭했을 시 */
                    //Toast.makeText(mContext, award.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext.getApplicationContext(), AwardResultActivity.class);

                    Bundle awardBundle = new Bundle();
                    awardBundle.putString("award_img_path", award.getAward_img());
                   // Log.d("imggggg", award.getAward_img());
                    awardBundle.putString("award_title", award.getTitle());
                    awardBundle.putString("award_field", item_field);
                    awardBundle.putString("award_caption", award.getCaption());
                    awardBundle.putString("award_id",award.getAward_id());
                    intent.putExtra("awardResultBundle", awardBundle);
                    mContext.startActivity(intent);
                }
            });
        } else {
            /* Header (viewpager) */
            final MyAwardHeaderViewHolder vh = (MyAwardHeaderViewHolder) viewHolder;

            vh.viewPagerDot.setNumOfCircles(nominate_list.size(), mContext.getResources().getDimensionPixelSize(R.dimen.height_very_small));

            HeaderPagerAdapter headerPagerAdapter = new HeaderPagerAdapter();
            vh.viewPager.setAdapter(headerPagerAdapter);
            vh.viewPager.setCurrentItem(0);

            /* ViewPager indicator 의 onChangeListener */
            vh.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    vh.viewPagerDot.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    vh.viewPagerDot.onPageSelected(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    vh.viewPagerDot.onPageScrollStateChanged(state);
                }
            });

        }
    }


    /* item count */
    public int getBasicItemCount() {
        return myFeedAward_list == null ? 0 : myFeedAward_list.size();
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1;
    }

    /* Returns viewType for a given position (Header or Item) */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    /*  Check given position is a header */
    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    private class MyAwardItemViewHolder extends RecyclerView.ViewHolder {

        /* Initiate each data item */
        TextView award_title;
        TextView award_field;
        ImageView award_item_img;
        CardView cardView;

        public MyAwardItemViewHolder(View view) {
            super(view);
            award_title = (TextView) view.findViewById(R.id.txtAward_title);
            award_field = (TextView) view.findViewById(R.id.txtAward_field);
            award_item_img = (ImageView) view.findViewById(R.id.imgAward_path);
            cardView = (CardView) itemView.findViewById(R.id.item_card);
        }
    }

    public static class MyAwardHeaderViewHolder extends RecyclerView.ViewHolder {
        /* Initiate List header */
        ViewPager viewPager;
        ViewPageDotView viewPagerDot;

        public MyAwardHeaderViewHolder(View view) {
            super(view);
            viewPager = (ViewPager) view.findViewById(R.id.header_viewPager);
            viewPagerDot = (ViewPageDotView) view.findViewById(R.id.dot_view);
        }
    }

    private class HeaderPagerAdapter extends PagerAdapter {
        /* 리스트의 header 부분 adapter */
        @Override
        public int getCount() {
            return nominate_list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//          final View view = LayoutInflater.from(mContext).inflate(R.layout.header_child_myaward, container, false);
            View view = getPagerItemView();

            Nominate nominate;
            /* set viewPager item data */
            nominate = nominate_list.get(position);

            ImageView imgNominate = (ImageView) view.findViewById(R.id.img_viewpager_childimage);
//            Glide
//                    .with(mContext)
//                    .load(nominate_URL + nominate.getNominate_img_path())
//                    .centerCrop()
//                    .thumbnail(0.1f)
//                    .into(imgNominate);
            imgNominate.setImageResource(R.drawable.feed_header_1);

            container.addView(view, 0);

            final String str = String.valueOf(position);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(mContext, NominateActivity.class);
                    mContext.startActivity(intent);
                }
            });

            return view;
        }


        private View getPagerItemView() {
            /* View 를 pool 에서 가져오고 없으면 inflate 한다. */
            View view = mMyViewPool.acquire();
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.header_child_myaward, null);
            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            mMyViewPool.release((View) object);
            container.removeView((ViewGroup) object);
        }
    }

}
