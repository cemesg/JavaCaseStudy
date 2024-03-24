package App;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Model.Post;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamPostMixer {


    public static List<Post> mixByOwners(List<Post> posts, int threadCount) {
        // Step 1: Determine start indices and counts for each user
        Map<Integer, List<Integer>> userPostsMap = getUserPostsMap(posts);

        // Step 2: Process posts using parallel stream
        return IntStream.range(0, threadCount).parallel()
                .mapToObj(threadIndex -> collectPostsByThread(posts, userPostsMap, threadIndex, threadCount))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static Map<Integer, List<Integer>> getUserPostsMap(List<Post> posts) {
        Map<Integer, List<Integer>> userPostsMap = new ConcurrentHashMap<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            userPostsMap.computeIfAbsent(post.getOwnerId(), k -> new ArrayList<>()).add(i);
        }
        return userPostsMap;
    }

    private static List<Post> collectPostsByThread(List<Post> posts, Map<Integer, List<Integer>> userPostsMap, int threadIndex, int threadCount) {
        List<Post> threadPosts = new ArrayList<>();
        for (List<Integer> userPostIndices : userPostsMap.values()) {
            for (int i = threadIndex; i < userPostIndices.size(); i += threadCount) {
                threadPosts.add(posts.get(userPostIndices.get(i)));
            }
        }
        return threadPosts;
    }


}
