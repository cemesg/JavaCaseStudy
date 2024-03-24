package App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import Model.Post;


import java.util.Set;
import java.util.stream.Collectors;

public class Validator {

    public static boolean validateMix(List<Post> input, List<Post> output) {
        // Check if input and output lists have the same size
        if (input.size() != output.size()) {
            return false;
        }

        // Check if output contains all the posts from input
        Set<Post> inputSet = new HashSet<>(input);
        Set<Post> outputSet = new HashSet<>(output);
        if (!inputSet.equals(outputSet)) {
            return false;
        }

        // Create a mapping from owner to list of posts in the input list
        Map<Integer, List<Post>> postsByOwner = new HashMap<>();
        for (Post post : input) {
            postsByOwner.computeIfAbsent(post.getOwnerId(), k -> new ArrayList<>()).add(post);
        }

        // Track the current index in the posts for each owner
        Map<Integer, Integer> currentIndexByOwner = new HashMap<>();
        for (Integer ownerId : postsByOwner.keySet()) {
            currentIndexByOwner.put(ownerId, 0);
        }

        // Check the order of posts in the output list
        for (Post post : output) {
            List<Post> ownerPosts = postsByOwner.get(post.getOwnerId());
            if (ownerPosts == null || currentIndexByOwner.get(post.getOwnerId()) >= ownerPosts.size()) {
                continue; // Skip if the owner has no more posts
            }

            Post expectedPost = ownerPosts.get(currentIndexByOwner.get(post.getOwnerId()));
            if (!post.equals(expectedPost)) {
                return false; // Post order mismatch
            }

            currentIndexByOwner.put(post.getOwnerId(), currentIndexByOwner.get(post.getOwnerId()) + 1);
        }

        return true;
    }

}
