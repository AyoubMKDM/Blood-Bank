package com.AyoubMKDM.github.bloodbank;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager ourInstance = null;
    private List<PostDataModel> mPosts = new ArrayList<>();

    public static DataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new DataManager();
            ourInstance.initializePosts();
        }
        return ourInstance;
    }

    private void initializePosts() {
        final String lorem_ipsum = "Lorem Ipsum is simply dummy text of the printing and typesetting" +
                " industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                " when an unknown printer took a galley";
        mPosts.add(new PostDataModel("Title ", lorem_ipsum));
        mPosts.add(new PostDataModel("Title ", lorem_ipsum));
        mPosts.add(new PostDataModel("Title ", lorem_ipsum));
        mPosts.add(new PostDataModel("Title ", lorem_ipsum));
        mPosts.add(new PostDataModel("Title ", lorem_ipsum));
        mPosts.add(new PostDataModel("Title ", lorem_ipsum));
    }
    
    public  void addPost(PostDataModel post){
        mPosts.add(post);
    }

    public List<PostDataModel> getPosts() {
        return mPosts;
    }

    public void setPosts(List<PostDataModel> posts) {
        mPosts = posts;
    }

}
