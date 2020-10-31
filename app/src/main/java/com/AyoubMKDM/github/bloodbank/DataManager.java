package com.AyoubMKDM.github.bloodbank;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager ourInstance = null;
    private List<DataModelPost> mPosts = new ArrayList<>();

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
        mPosts.add(new DataModelPost("Title ", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ", lorem_ipsum));
    }
    
    public  void addPost(DataModelPost post){
        mPosts.add(post);
    }

    public List<DataModelPost> getPosts() {
        return mPosts;
    }

    public void setPosts(List<DataModelPost> posts) {
        mPosts = posts;
    }

}
