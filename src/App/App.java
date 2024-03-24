package App;

import java.util.*;
import java.util.concurrent.*;
import Model.Post;

public class App {

    private static List<Post> generatePosts(int numberOfPosts, int numberOfOwners) {
        Random random = new Random();
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < numberOfPosts; i++) {
            posts.add(new Post(i, random.nextInt(numberOfOwners)));
        }
        
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                int ownerCompare = Integer.compare(p1.getOwnerId(), p2.getOwnerId());
                if (ownerCompare != 0) {
                    return ownerCompare;
                }
                return Integer.compare(p1.getId(), p2.getId());
            }
        });
        return posts;
    }
    
    

    private static void benchmark(String approachName, Runnable approachMethod) {
        long startTime = System.nanoTime();
        approachMethod.run();
        long endTime = System.nanoTime();
        System.out.println(approachName + " took " + (endTime - startTime) / 1_000_000.0 + " ms");
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final int numberOfPosts = 5000000;
        final int numberOfOwners = 4000;
        List<Post> posts = generatePosts(numberOfPosts, numberOfOwners);
        //System.out.println(posts);

        // Sequential
        benchmark("Sequential", () -> PostMixer.mixByOwners(new ArrayList<>(posts)));
        //System.out.println(PostMixer.mix_by_owners(new ArrayList<>(posts)));
        System.out.println(Validator.validateMix(posts, PostMixer.mixByOwners(new ArrayList<>(posts))));

        // ExecutorService
        benchmark("ExecutorService", () -> {
            try {
               ParallelPostMixer.mixByOwners(new ArrayList<>(posts), 8); // Example with 8 threads
               
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        System.out.println(Validator.validateMix(posts, ParallelPostMixer.mixByOwners(new ArrayList<>(posts),8)));
        try {
			//System.out.println(ParallelPostMixer.mixByOwners(new ArrayList<>(posts),8));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Parallel Stream
        benchmark("Parallel Stream", () -> StreamPostMixer.mixByOwners(new ArrayList<>(posts), 8));
    }
}
