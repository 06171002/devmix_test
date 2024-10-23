package msa.devmix.repository;

import msa.devmix.domain.board.BoardPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPositionRepository extends JpaRepository<BoardPosition, Long> {

    List<BoardPosition> findByBoard_Id(Long boardId);

    void deleteAllByBoard_Id(Long boardId);
}
