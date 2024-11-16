package SGBD_Project.example.LearnCode.Utils;

import SGBD_Project.example.LearnCode.Models.Post;

import java.util.*;

public class PostUtils {
    public static List<Post> sortPosts(List<Post> posts, String criteria) {
        if (posts == null || posts.isEmpty()) return posts;

        switch (criteria.toLowerCase()) {
            case "most_upvoted":
                posts.sort((p1, p2) -> Integer.compare(
                        p2.getUpVoteNumber() != null ? p2.getUpVoteNumber() : 0,
                        p1.getUpVoteNumber() != null ? p1.getUpVoteNumber() : 0));
                break;
            case "least_downvoted":
                posts.sort((p1, p2) -> Integer.compare(
                        p1.getDownVoteNumber() != null ? p1.getDownVoteNumber() : 0,
                        p2.getDownVoteNumber() != null ? p2.getDownVoteNumber() : 0));
                break;
            case "most_seen":
                posts.sort((p1, p2) -> Integer.compare(
                        p2.getSeenNumber() != null ? p2.getSeenNumber() : 0,
                        p1.getSeenNumber() != null ? p1.getSeenNumber() : 0));
                break;
            case "newest":
                posts.sort((p1, p2) -> p2.getDate().compareTo(p1.getDate()));
                break;
        }
        return posts;
    }

    public static List<Post> sortPostsByAllCriteria(List<Post> posts) {
        if (posts == null || posts.isEmpty()) return posts;

        posts.sort(Comparator.comparing((Post p) -> p.getDate(), Comparator.reverseOrder()) // Newest
                .thenComparing(p -> p.getUpVoteNumber() != null ? p.getUpVoteNumber() : 0, Comparator.reverseOrder()) // Most upvoted
                .thenComparing(p -> p.getDownVoteNumber() != null ? p.getDownVoteNumber() : 0) // Least downvoted
                .thenComparing(p -> p.getSeenNumber() != null ? p.getSeenNumber() : 0, Comparator.reverseOrder()) // Most seen
        );

        return posts;
    }
}
