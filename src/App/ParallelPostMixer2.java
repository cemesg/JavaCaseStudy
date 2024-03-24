package App;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import Model.Post;


public class ParallelPostMixer2 {

    static class OwnerInfo {
        int ownerId;
        int firstIndex;
        int postCount;

        OwnerInfo(int ownerId, int firstIndex, int postCount) {
            this.ownerId = ownerId;
            this.firstIndex = firstIndex;
            this.postCount = postCount;
        }
    }

    public static List<Post> mixByOwners(List<Post> posts, int numOfThreads) {
        LinkedList<OwnerInfo> ownerInfos = getOwnerInfos(posts);
        int maxPostsPerUser = getMaxPostsPerUser(ownerInfos);
        List<List<Post>> results = new ArrayList<>();
        for (int i = 0; i < maxPostsPerUser; i++) {
            results.add(new ArrayList<>());
        }

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        Object lock = new Object(); // Lock object for synchronization

        for (int i = 0; i < numOfThreads; i++) {
            final int threadOffset = i;
            executor.submit(() -> {
                int offset = threadOffset;
                while (offset < maxPostsPerUser) {
                    List<Post> tempList = new ArrayList<>();
                    synchronized (lock) {
                        var iter = ownerInfos.iterator();
                        while (iter.hasNext()) {
                            OwnerInfo ownerInfo = iter.next();
                            int postIndex = ownerInfo.firstIndex + offset;
                            if (postIndex < ownerInfo.firstIndex + ownerInfo.postCount) {
                                tempList.add(posts.get(postIndex));
                            }
                            if (offset >= ownerInfo.postCount - 1) {
                                iter.remove(); // Remove the owner whose posts are fully processed
                            }
                        }
                    }
                    if (offset < results.size()) {
                        results.set(offset, tempList);
                    }
                    offset += numOfThreads;
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        List<Post> mixedPosts = new ArrayList<>();
        for (List<Post> resultList : results) {
            mixedPosts.addAll(resultList);
        }

        return mixedPosts;
    }

    private static LinkedList<OwnerInfo> getOwnerInfos(List<Post> posts) {
        LinkedList<OwnerInfo> ownerInfos = new LinkedList<>();
        int currentOwnerId = -1;
        int firstIndex = 0;
        int postCount = 0;

        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getOwnerId() != currentOwnerId) {
                if (currentOwnerId != -1) {
                    ownerInfos.add(new OwnerInfo(currentOwnerId, firstIndex, postCount));
                }
                currentOwnerId = posts.get(i).getOwnerId();
                firstIndex = i;
                postCount = 1;
            } else {
                postCount++;
            }
        }
        if (currentOwnerId != -1) {
            ownerInfos.add(new OwnerInfo(currentOwnerId, firstIndex, postCount));
        }

        return ownerInfos;
    }

    private static int getMaxPostsPerUser(LinkedList<OwnerInfo> ownerInfos) {
        int maxPosts = 0;
        for (OwnerInfo ownerInfo : ownerInfos) {
            maxPosts = Math.max(maxPosts, ownerInfo.postCount);
        }
        return maxPosts;
    }


	}




