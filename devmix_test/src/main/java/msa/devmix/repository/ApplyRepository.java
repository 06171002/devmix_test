package msa.devmix.repository;

import msa.devmix.domain.board.Apply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    List<Apply> findByUser_Id(Long userId);

//    void deleteAllByBoard_Id(Long boardId);
}
