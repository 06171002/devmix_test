package msa.devmix.repository;

import msa.devmix.domain.board.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByUser_Id(Long userId, Pageable pageable);

    List<Comment> findByBoard_Id(Long boardId);

    void deleteByBoard_Id(Long boardId);

    void deleteAllByBoard_Id(Long boardId);
}
