package msa.devmix.service;

import msa.devmix.domain.user.User;
import msa.devmix.dto.BoardDto;
import msa.devmix.dto.BoardPositionDto;
import msa.devmix.dto.BoardTechStackDto;
import msa.devmix.dto.BoardWithPositionTechStackDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    BoardWithPositionTechStackDto getBoard(Long boardId);
    Page<BoardDto> getBoards(Pageable pageable);
    void saveBoard(BoardDto boardDto, List<BoardPositionDto> boardPositionDtos, List<BoardTechStackDto> boardTechStackDtos);
    void updateBoard(Long boardId, BoardDto boardDto, List<BoardPositionDto> boardPositionDtos, List<BoardTechStackDto> boardTechStackDtos);
//    void deleteBoard(Long boardId);
}
