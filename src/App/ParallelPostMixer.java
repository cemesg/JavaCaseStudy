package App;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import Model.Post;


public class ParallelPostMixer {

	   static class OwnerInfo {
	        int ownerId;
	        int startIndex;

	        OwnerInfo(int ownerId, int startIndex) {
	            this.ownerId = ownerId;
	            this.startIndex = startIndex;
	        }
	    }

	    public static List<Post> mixByOwners(List<Post> posts, int numOfThreads) {
	        List<OwnerInfo> ownerInfos = getOwnerInfos(posts);
	        int maxPostsPerUser = getMaxPostsPerUser(posts, ownerInfos);
	        List<List<Post>> results = new ArrayList<>();
	        for (int i = 0; i < maxPostsPerUser; i++) {
	            results.add(new ArrayList<>());
	        }

	        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
	        for (int i = 0; i < numOfThreads; i++) {
	            final int threadOffset = i;
	            
	            //each thread looks at every owners posts with certain offsets so no synchronization needed
	            executor.submit(() -> {
	                int offset = threadOffset;
	                while (offset < maxPostsPerUser) {
	                    List<Post> tempList = new ArrayList<>();
	                    for (OwnerInfo ownerInfo : ownerInfos) {
	                        int postIndex = ownerInfo.startIndex + offset;
	                        if (postIndex < posts.size() && posts.get(postIndex).getOwnerId() == ownerInfo.ownerId) {
	                            tempList.add(posts.get(postIndex));
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

	    //keep a track of which owner id has posts starting in array makes it more readable vs the approach in sequential
	    private static List<OwnerInfo> getOwnerInfos(List<Post> posts) {
	        List<OwnerInfo> ownerInfos = new ArrayList<>();
	        for (int i = 0; i < posts.size(); i++) {
	            if (i == 0 || posts.get(i).getOwnerId() != posts.get(i - 1).getOwnerId()) {
	                ownerInfos.add(new OwnerInfo(posts.get(i).getOwnerId(), i));
	            }
	        }
	        return ownerInfos;
	    }

	    //number of iterations essentially
	    private static int getMaxPostsPerUser(List<Post> posts, List<OwnerInfo> ownerInfos) {
	        int maxPosts = 0;
	        for (int i = 0; i < ownerInfos.size(); i++) {
	            int postsCount = (i < ownerInfos.size() - 1) ? ownerInfos.get(i + 1).startIndex - ownerInfos.get(i).startIndex : posts.size() - ownerInfos.get(i).startIndex;
	            maxPosts = Math.max(maxPosts, postsCount);
	        }
	        return maxPosts;
	    }


	}




