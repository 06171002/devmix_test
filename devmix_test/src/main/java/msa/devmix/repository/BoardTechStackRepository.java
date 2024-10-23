package msa.devmix.repository;

import msa.devmix.domain.board.BoardTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardTechStackRepository extends JpaRepository<BoardTechStack, Long> {

    List<BoardTechStack> findByBoard_Id(Long boardId);

    void deleteAllByBoard_Id(Long boardId);
}
