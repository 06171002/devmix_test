package msa.devmix.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.devmix.config.oauth.userinfo.UserPrincipal;
import msa.devmix.domain.board.Board;
import msa.devmix.domain.user.User;
import msa.devmix.dto.BoardDto;
import msa.devmix.dto.BoardPositionDto;
import msa.devmix.dto.BoardTechStackDto;
import msa.devmix.dto.UserDto;
import msa.devmix.dto.request.*;
import msa.devmix.dto.response.BoardWithPositionTechStackResponse;
import msa.devmix.dto.response.ResponseDto;
import msa.devmix.exception.CustomException;
import msa.devmix.exception.ErrorCode;
import msa.devmix.repository.BoardRepository;
import msa.devmix.service.implement.BoardServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;



@RestController
@RequestMapping("/api/v1/boards")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BoardController {

    private final BoardServiceImpl boardService;
    private final BoardRepository boardRepository;

    // 게시물 상세 조회
    @GetMapping("/{board-id}")
    public ResponseEntity<?> Board(@PathVariable("board-id") @Min(1) Long boardId) {

        // 제목, 내용, 게시글 번호(ID), 모집상태, 조회수, 북마크 해간놈 list, 진행방식(지역), 기술스택 dto, 진행기간, 시작일, 모집마감일, 포지션 dto, 작성자정보 dto, BaseTimeEntity, 댓글(따로)
        return ResponseEntity.ok()
                .body(ResponseDto.success(BoardWithPositionTechStackResponse.from(boardService.getBoard(boardId))));

    }

    // 특정 페이지 게시글 조회
    @GetMapping
    public ResponseEntity<?> Boards(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return null;
    }

    // 게시글 생성
    @PostMapping
    public ResponseEntity<?> postBoard(@Valid @RequestBody PostBoardRequest postBoardRequest,
                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {

//        BoardDto boardDto = postBoardRequest.toDto(userPrincipal.getUser().toDto());
        BoardDto boardDto = postBoardRequest.toDto(UserDto.from(userPrincipal.getUser()));

        List<BoardPositionDto> boardPositionDtos = postBoardRequest.getBoardPositionList()
                                                                   .stream()
                                                                   .map(BoardPositionRequest::toDto)
                                                                   .toList();

        List<BoardTechStackDto> boardTechStackDtos  = postBoardRequest.getTechStackList()
                                                                      .stream()
                                                                      .map(TechStackRequest::toDto)
                                                                      .toList();

        boardService.saveBoard(boardDto, boardPositionDtos, boardTechStackDtos);

        return ResponseEntity.ok().body(ResponseDto.success());
    }

    // 게시글 수정
    @PutMapping("/{board-id}")
    public ResponseEntity<?> updateBoard(@PathVariable("board-id") @Min(1) Long boardId,
                                         @Valid @RequestBody UpdateBoardRequest updateBoardRequest,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (Objects.equals(board.getUser().getId(), userPrincipal.getUser().getId())) {
            BoardDto boardDto = updateBoardRequest.toDto(UserDto.from(userPrincipal.getUser()));

            List<BoardPositionDto> boardPositionDtos = updateBoardRequest.getBoardPositionList()
                                                                            .stream()
                                                                                    .map(BoardPositionRequest::toDto)
                                                                                            .toList();

            List<BoardTechStackDto> boardTechStackDtos = updateBoardRequest.getTechStackList()
                                                                            .stream()
                                                                                    .map(TechStackRequest::toDto)
                                                                                            .toList();
            boardService.updateBoard(boardId, boardDto, boardPositionDtos, boardTechStackDtos);
        } else {
            throw new CustomException(ErrorCode.VALIDATION_FAILED);
        }

        return ResponseEntity.ok().body(ResponseDto.success());
    }

    // 특정 게시글 삭제
    // @AuthenticationPrincipal 추가 예정
    @DeleteMapping("/{board-id}")
    public ResponseEntity<?> deleteBoard(@PathVariable("board-id") @Min(1) Long boardId,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {

//        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//
//        if (board.getUser().equals(userPrincipal.getUser())) {
//            boardService.deleteBoard(boardId);
//        } else {
//            throw new CustomException(ErrorCode.VALIDATION_FAILED);
//        }

        return ResponseEntity.ok().body(ResponseDto.success());
    }

}
