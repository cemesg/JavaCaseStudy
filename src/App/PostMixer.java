package App;

import java.util.ArrayList;
import java.util.List;

import Model.Post;


public class PostMixer {

	public static List<Post> mixByOwners(List<Post> posts) {
	    if (posts == null || posts.isEmpty()) return posts;

	    List<Post> mixedPosts = new ArrayList<>();
	    //keep a list of where each user posts starts in array
	    List<Integer> startIndices = new ArrayList<>();
	    //index of current post to process for each user
	    List<Integer> currentIndices = new ArrayList<>();

	    
	    int prevOwnerId = -1;
	    for (int i = 0; i < posts.size(); i++) {
	        if (posts.get(i).getOwnerId() != prevOwnerId) {
	            startIndices.add(i);
	            currentIndices.add(i);
	            prevOwnerId = posts.get(i).getOwnerId();
	        }
	    }
	    startIndices.add(posts.size()); // add final value for last users end

	    boolean done = false;
	    while (!done) {
	        done = true;
	        //cycle all user indices and insert post at current index until index is same as next users start
	        for (int i = 0; i < currentIndices.size(); i++) {
	            int nextOwnerIndex = (i == currentIndices.size() - 1) ? posts.size() : startIndices.get(i + 1);
	            if (currentIndices.get(i) < nextOwnerIndex) {
	                mixedPosts.add(posts.get(currentIndices.get(i)));
	                currentIndices.set(i, currentIndices.get(i) + 1);
	                done = false;
	            }
	        }
	    }

	    return mixedPosts;
	}
}