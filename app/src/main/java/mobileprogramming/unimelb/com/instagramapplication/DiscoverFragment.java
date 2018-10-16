package mobileprogramming.unimelb.com.instagramapplication;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.UsersAdapter;
import mobileprogramming.unimelb.com.instagramapplication.adapter.UsersAdapterSuggest;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsers;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsersFollowing;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;

public class DiscoverFragment extends Fragment {

    @BindView(R.id.discoverRecyclerView)
    RecyclerView mDiscoverRecyclerView;
    @BindView(R.id.edt_search)
    EditText edt_search;
    @BindView(R.id.recyclerViewSuggestions)
    RecyclerView recyclerViewSuggestions;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UsersAdapter adapter;
    private UsersAdapterSuggest adapterSuggest;
    private ArrayList<ModelUsers> feeds = new ArrayList<>();
    final private ArrayList<ModelUsers> feedsSuggest = new ArrayList<>();
    private ArrayList<ModelUsers> feedsSearchResult = new ArrayList<>();
    private ArrayList<ModelUsers> feedsSearchResultSuggest = new ArrayList<>();
    private ArrayList<ModelUsersFollowing> usersFollowings = new ArrayList<>();
    private ArrayList<ModelUsersFollowing> usersSuggested = new ArrayList<>();
    private View view;
    private RecyclerViewLoadMoreScroll scrollListener;
    private FragmentManager fm;
    private String TAG = "UserFeedsFragment";
    private String uuid;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discoder, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerViewSuggestions.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagerSug = new LinearLayoutManager(getContext(), OrientationHelper.HORIZONTAL, false);
        recyclerViewSuggestions.setLayoutManager(horizontalLayoutManagerSug);
        adapterSuggest = new UsersAdapterSuggest((AppCompatActivity) getActivity(), feedsSearchResultSuggest);
        recyclerViewSuggestions.setAdapter(adapterSuggest);
        scrollListener = new RecyclerViewLoadMoreScroll(horizontalLayoutManagerSug);
        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadMoreData();
            }
        });
        recyclerViewSuggestions.addOnScrollListener(scrollListener);
        adapterSuggest.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position, int in) {

                if (in == 1) {

                    Map<String, Object> user = new HashMap<>();
                    user.put("followerid", uuid);
                    user.put("uid", feedsSuggest.get(position).getUuid());
                    //data2.put("regions", Arrays.asList("west_coast", "socal"));
                    //user.put("username", feeds.get(position).getUsername());
                    db.collection("follower")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    adapterSuggest.followed(position);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else if (in == 2) {

                    feedsSuggest.get(position).setFollwing(false);
                    adapterSuggest.notifyItemChanged(position);
                    CollectionReference citiesRef = db.collection("follower");
                    final Query query = citiesRef.whereEqualTo("followerid", uuid).whereEqualTo("uid", feedsSuggest.get(position).getUuid());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                WriteBatch batch = query.getFirestore().batch();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    batch.delete(document.getReference());
                                }
                                batch.commit();
                            } else {
                                Log.d(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

                }


            }

            @Override
            public void onItemLongClick(int position) {

            }
        });





        mDiscoverRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false);
        mDiscoverRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UsersAdapter((AppCompatActivity) getActivity(), feedsSearchResult);
        mDiscoverRecyclerView.setAdapter(adapter);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadMoreData();
            }
        });
        mDiscoverRecyclerView.addOnScrollListener(scrollListener);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position, int in) {

                if (in == 1) {
                    /*
                     * Code here to follow user
                     */
                    Map<String, Object> user = new HashMap<>();
                    user.put("followerid", uuid);
                    user.put("uid", feeds.get(position).getUuid());
                    //data2.put("regions", Arrays.asList("west_coast", "socal"));
                    //user.put("username", feeds.get(position).getUsername());
                    db.collection("follower")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    adapter.followed(position);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else if (in == 2) {
                    /*
                     * Code here to unfollow user
                     */
                    feeds.get(position).setFollwing(false);
                    adapter.notifyItemChanged(position);
                    CollectionReference citiesRef = db.collection("follower");
                    final Query query = citiesRef.whereEqualTo("followerid", uuid).whereEqualTo("uid", feeds.get(position).getUuid());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                WriteBatch batch = query.getFirestore().batch();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    batch.delete(document.getReference());
                                }
                                batch.commit();
                            } else {
                                Log.d(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

                }


            }

            @Override
            public void onItemLongClick(int position) {

            }
        });


        feeds.clear();
        feedsSuggest.clear();
        usersFollowings.clear();
        usersSuggested.clear();
        feedsSearchResult.clear();
        feedsSearchResultSuggest.clear();
        //Resume user suggestion from here...
        getFollowingUserssuggestions();
        getFollowingUsers();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                feedsSearchResult.clear();
                for (int i = 0; i < feeds.size(); i++) {
                    if (feeds.get(i).getUsername().contains(s.toString())) {
                        feedsSearchResult.add(feeds.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getFollowingUsers() {
        CommonUtils.showLoadingDialog(getContext());
        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", uuid).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            final ModelUsersFollowing m = new ModelUsersFollowing();
                            m.setUuid(document.getData().get("uid").toString());
                            m.setFollowerid(document.getData().get("followerid").toString());
                            usersFollowings.add(m);
                        }
                    }
                }
                getUsers();
            }
        });


    }

    private void LoadMoreData() {
        adapter.addLoadingView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeLoadingView();
                scrollListener.setLoaded();

            }
        }, 1000);

    }

    private void getUsers() {

        CollectionReference citiesRef = db.collection("Users");
        Query query = citiesRef.orderBy("username").limit(200);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                CommonUtils.dismissProgressDialog();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            final ModelUsers m = new ModelUsers();
                            m.setUuid(document.getId());
                            m.setUsername(document.getData().get("username").toString());
                            m.setImage(document.getData().get("image").toString());

                            for (int i = 0; i < usersFollowings.size(); i++) {
                                if (usersFollowings.get(i).getUuid().equals(document.getId())) {
                                    //Set true fo users which this user folllows -u.
                                    m.setFollwing(true);
                                    break;
                                }
                            }

                            //If user exists in suggested list, do not add him -u.
                            boolean userExistsInSuggestedList = false;
                            for (int i = 0; i < feedsSuggest.size(); i++) {
                                if (feedsSuggest.get(i).getUuid().equals(document.getId())) {
                                    userExistsInSuggestedList = true;
                                    break;
                                }
                            }
                            //If user exists in suggested list, do not add him -u.
                            if(!userExistsInSuggestedList) {
                                feeds.add(m);
                            }

                        }

                    }
                    feedsSearchResult.addAll(feeds);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }

    private void getFollowingUserssuggestions() {
        //CommonUtils.showLoadingDialog(getContext());
        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", uuid).limit(100);
        //Query query = citiesRef.whereGreaterThan("followerid",uuid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            final ModelUsersFollowing m = new ModelUsersFollowing();
                            m.setUuid(document.getData().get("uid").toString());
                            m.setFollowerid(document.getData().get("followerid").toString());
                            usersSuggested.add(m);
                        }
                    }
                }
                getUsersSuggested();
            }
        });


    }

    private void getUsersSuggested() {

        CollectionReference citiesRef = db.collection("Users");
        Query query = citiesRef.orderBy("username").limit(200);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //CommonUtils.dismissProgressDialog();
                if (task.isSuccessful()) {
                    int someCount= 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            final ModelUsers m = new ModelUsers();
                            m.setUuid(document.getId());
                            m.setUsername(document.getData().get("username").toString());
                            m.setImage(document.getData().get("image").toString());
                            boolean doesUserFollowThisUser = false;
                            for (int i = 0; i < usersSuggested.size(); i++) {
                                if (usersSuggested.get(i).getUuid().equals(document.getId())) {
                                    m.setFollwing(true);
                                    doesUserFollowThisUser = true;

                                    //Checking only for users that this user does follows -u.

                                    /**Add suggestions based on algorithm here
                                     * Any users added here will not show up
                                     * in search list, as per instagram's orignal
                                     * behaviour. --(M. Umair)
                                     */
                                    if(uuid.equals(m.getUuid()))
                                    {
                                        continue;
                                    }
                                    //TODO-Implement suggestionalgohere.All else ready
                                    //ArrayList<ModelUsers> thisFeedIn = new ArrayList<>();
                                    //thisFeedIn = suggestionAlgorithm(m.getUuid());
                                    //Log.d("SuggestionSize*------>",Integer.toString(feedsSuggest.size()));
                                    feedsSuggest.add(m);
                                    break;
                                }
                            }
                        }

                    }

                    feedsSearchResultSuggest.addAll(feedsSuggest);
                    adapterSuggest.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }

            }
        });


    }

    private ArrayList<ModelUsers> suggestionAlgorithm(String followedUserUuid)
    {

        final ArrayList<ModelUsers> thisfeed = new ArrayList<>();

        try {

            //Get followers of users that this user is following.
            CollectionReference citiesRef = db.collection("follower");
            Query query = citiesRef.whereEqualTo("followerid", followedUserUuid).limit(100);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        int someCount= 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (task.isSuccessful()) {
                                final ModelUsers m = new ModelUsers();
                                m.setUuid(document.getId());
                                try {
                                    m.setUsername(document.getData().get("username").toString());
                                }
                                catch (Exception e) {
                                    Log.d("UsrParse Exception: ",e.getMessage());
                                }
                                try {
                                    m.setImage(document.getData().get("image").toString());
                                }
                                catch (Exception e) {
                                    Log.d("ImgParse Exception: ",e.getMessage());
                                }
                                //Add only if user is not added previously
                                //TODO-Implement check here! -Done (M. Umair)
                                boolean userFound = false;
                                for (int i = 0; i < feedsSuggest.size(); i++) {
                                    if (feedsSuggest.get(i).getUuid().equals(document.getId())) {
                                        userFound=true;
                                    }
                                }
                                //Add only if user is not added previously
                                if(!userFound)
                                {
                                    feedsSuggest.add(m);
                                    thisfeed.add(m);
                                }

                            }
                        }

                        Log.d("SuggestionSize------->",Integer.toString(feedsSuggest.size()));

                    }
                }
            });

        }
        catch (Exception e) {
            Log.d("Suggestion Exception: ",e.getMessage());
        }
        finally {
            //Release connections here (if any acquired).
        }

        return thisfeed;

    }


}
