package msa.devmix.repository;

import msa.devmix.domain.board.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Scrap findByUser_IdAndBoard_Id(Long userId, Long boardId);

    void deleteAllByBoard_Id(Long boardId);
}
