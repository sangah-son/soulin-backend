package com.soulin.api.bookmark.service;

import com.soulin.api.bookmark.dto.BookmarkPostResponse;
import com.soulin.api.bookmark.dto.BookmarkResponse;
import com.soulin.api.bookmark.entity.Bookmark;
import com.soulin.api.bookmark.repository.BookmarkRepository;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public BookmarkResponse createBookmark(Long userId, Long postId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Bookmark bookmark=new Bookmark(post, user);

        if(bookmarkRepository.existsByUserAndPost(user, post)){
            throw new IllegalArgumentException("이미 북마크한 게시글입니다.");
        }

        Bookmark savedBookmark=bookmarkRepository.save(bookmark);

        return new BookmarkResponse(
                savedBookmark.getBookmarkId(),
                savedBookmark.getPost().getPostId(),
                savedBookmark.getUser().getId(),
                savedBookmark.getCreatedAt()
        );
    }

    public void deleteBookmark(Long userId, Long postId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Bookmark bookmark=bookmarkRepository.findByUserAndPost(user,post)
                .orElseThrow(()->new IllegalArgumentException("북마크가 존재하지 않습니다."));

        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public List<BookmarkPostResponse> getMyBookmarks(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Bookmark> bookmarks=bookmarkRepository.findAllByUserOrderByCreatedAtDesc(user);
        return bookmarks.stream()
                .map(bookmark -> new BookmarkPostResponse(
                        bookmark.getBookmarkId(),
                        bookmark.getPost().getPostId(),
                        bookmark.getPost().getTitle(),
                        bookmark.getPost().getContent(),
                        bookmark.getPost().getColor().getColorId(),
                        bookmark.getPost().getUser().getUserName(),
                        bookmark.getCreatedAt()
                ))
                .toList();
    }
}
