package huka.com.repli;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hugo.myapplication.backend.replyInfoApi.ReplyInfoApi;
import com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfoCollection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapters.MyRecyclerReplyAdapter;
import servercalls.ServiceBuilder;

public class RepliesFragment extends android.support.v4.app.Fragment {

    protected RecyclerView mRecyclerView;
    protected static MyRecyclerReplyAdapter mAdapter;
    protected ArrayList<ReplyInfo> mDataset = new ArrayList<>();
    FragmentActivity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static RepliesFragment newInstance() {
        return new RepliesFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (FragmentActivity) activity;
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_reply_frag, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerReplyView);
        mAdapter = new MyRecyclerReplyAdapter(mDataset);
        return rootView;
    }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.SetOnItemClickListener(new MyRecyclerReplyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v , int position) {

                if(!mDataset.get(position).isReplied()) {
                    return;
                }
                Bitmap image = mDataset.get(position).getBitmapImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();

                Intent intent = new Intent(getActivity(), ViewReplyActivity.class);
                intent.putExtra("picture", imageBytes);
                // TODO change from username to email
                intent.putExtra("accountName", mDataset.get(position).getUsername());
                startActivityForResult(intent, 0);
            }

            @Override
            public boolean onItemLongClicked(final int position) {
                final int itemPosition = position;
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getActivity());
                dlgAlert.setTitle("Remove Conversation");
                dlgAlert.setMessage("Do you want to remove this Conversation? (Cannot be undone)");
                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeItem(itemPosition);
                    }
                });
                dlgAlert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
                return true;
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_dark));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        initDataset();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Data for RecyclerView's adapter.
     */
    private void initDataset() {
        String email = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(LoginActivity.ACCOUNT_NAME, "none");
        if(!email.equals("none")) {
            new GetReplyListAsyncTask().execute(email);
        }
    }

    private class GetReplyListAsyncTask extends AsyncTask<String, Void, Void> {

        private ReplyInfoApi replyService;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String email = params[0];
            mDataset = new ArrayList<>();
            if (replyService == null) {
                replyService = ServiceBuilder.buildReplyInfoService();
                try {
                    ReplyInfoCollection replyInfoCollection = replyService.get(email).execute();
                    List<com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo> replyInfoList = replyInfoCollection.getItems();
                    if(replyInfoList != null) {
                        for (com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo replyInfo : replyInfoList) {
                            ReplyInfo replyInfoDataSet = new ReplyInfo(replyInfo.getAccountName());
                            replyInfoDataSet.setDate(replyInfo.getTimeStamp());
                            replyInfoDataSet.setReplied(replyInfo.getReplied());
                            replyInfoDataSet.setImage(replyInfo.getPictureUrl());
                            replyInfoDataSet.setProfilePicture(replyInfo.getProfilePictureUrl());
                            replyInfoDataSet.setThumbnail(replyInfo.getPictureUrl());
                            mDataset.add(replyInfoDataSet);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            mAdapter.setDataSet(mDataset);

            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        this.getActivity().registerReceiver(mMessageReceiver, new IntentFilter("repli"));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String repliAcc = intent.getStringExtra("accName");
            new GetReplyListAsyncTask().execute(repliAcc);
        }
    };
}
