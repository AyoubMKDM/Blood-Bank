package com.AyoubMKDM.github.bloodbank;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager ourInstance = null;
    private List<PostDataModel> mPosts = new ArrayList<>();

    public static DataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
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
