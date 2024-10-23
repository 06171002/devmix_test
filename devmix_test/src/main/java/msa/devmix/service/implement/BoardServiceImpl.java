package msa.devmix.service.implement;

import lombok.RequiredArgsConstructor;
import msa.devmix.domain.board.*;
import msa.devmix.domain.common.Position;
import msa.devmix.domain.constant.RecruitmentStatus;
import msa.devmix.domain.user.User;
import msa.devmix.dto.BoardDto;
import msa.devmix.dto.BoardPositionDto;
import msa.devmix.dto.BoardTechStackDto;
import msa.devmix.dto.BoardWithPositionTechStackDto;
import msa.devmix.dto.request.BoardPositionRequest;
import msa.devmix.exception.CustomException;
import msa.devmix.exception.ErrorCode;
import msa.devmix.repository.*;
import msa.devmix.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardPositionRepository boardPositionRepository;
    private final PositionRepository positionRepository;
    private final TechStackRepository techStackRepository;
    private final BoardTechStackRepository boardTechStackRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ApplyRepository applyRepository;
    private final ScrapRepository scrapRepository;

    @Override
    public BoardWithPositionTechStackDto getBoard(Long boardId) {

        // boarddto, boardpositiondto, boardtechstackdto 3개 고정
        BoardDto boardDto = boardRepository
                .findById(boardId)
                .map(BoardDto::from)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND, String.format("boardId: %d", boardId)));

//        List<BoardPositionDto> boardPositionDtoList = new ArrayList<>();
//        List<BoardPosition> boardPositionList = boardPositionRepository.findByBoard_Id(boardId);
//        for (BoardPosition boardPosition : boardPositionList) {
//            BoardPositionDto.from(boardPosition);
//            boardPositionDtoList.add(boardPositionDto);
//        }

        List<BoardPositionDto> boardPositionDtoList = boardPositionRepository
                .findByBoard_Id(boardId).stream()
                .map(BoardPositionDto::from)
                .toList();

//        List<BoardTechStackDto> boardTechStackDtoList = new ArrayList<>();
//        List<BoardTechStack> boardTechStackList = boardTechStackRepository.findByBoard_Id(boardId);
//        for (BoardTechStack boardTechStack : boardTechStackList) {
//            BoardTechStackDto.from(boardTechStack);
//            boardTechStackDtoList.add(boardTechStackDto);
//        }

        List<BoardTechStackDto> boardTechStackDtoList = boardTechStackRepository
                .findByBoard_Id(boardId).stream()
                .map(BoardTechStackDto::from)
                .toList();

        //of로
        return BoardWithPositionTechStackDto.of(boardDto, boardPositionDtoList, boardTechStackDtoList);
    }


    @Override
    public void saveBoard(BoardDto boardDto,
                          List<BoardPositionDto> boardPositionDtos,
                          List<BoardTechStackDto> boardTechStackDtos) {

        Board board = boardDto.toEntity();
        board.setRecruitmentStatus(RecruitmentStatus.RECRUITING);
//        board.setUser(userRepository.findById(boardDto.getUserDto().getId()).get());
        board.setUser(userRepository.findById(boardDto.getUserDto().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
        board.setViewCount(0L);

        boardRepository.save(board);

        /**
         * todo: 기술 스택명, 포지션에 대한 검증 로직 + boardxxxDto.toEntity()
         * todo: 최대 인원수 검증 필요
         */

        List<String> positionNames = boardPositionDtos.stream()
                .map(BoardPositionDto::getPositionName)
                        .toList();

        List<Position> existingPositions = positionRepository.findByPositionNameIn(positionNames);

        if (positionNames.size() != existingPositions.size()) {
            throw new CustomException(ErrorCode.POSITION_NOT_FOUND);
        }

        boardPositionDtos.stream()
                .map(boardPositionDto -> {
                    if (positionRepository.findByPositionName(boardPositionDto.getPositionName()) != null) {
                        return boardPositionDto.toEntity(board, positionRepository.findByPositionName(boardPositionDto.getPositionName()));
                    } else {
                        throw new CustomException(ErrorCode.POSITION_NOT_FOUND);
                    }
                })
                .forEach(boardPositionRepository::save);


        boardTechStackDtos.stream()
                .map(boardTechStackDto -> {
                    if (techStackRepository.findByTechStackName(boardTechStackDto.getTechStackName()) != null) {
                        return boardTechStackDto.toEntity(board, techStackRepository.findByTechStackName(boardTechStackDto.getTechStackName()));
                    } else {
                        throw new CustomException(ErrorCode.TECH_STACK_NOT_FOUND);
                    }
                })
                .forEach(boardTechStackRepository::save);

    }

    @Override
    @Transactional
    public void updateBoard(Long boardId,
                            BoardDto boardDto,
                            List<BoardPositionDto> boardPositionDtos,
                            List<BoardTechStackDto> boardTechStackDtos) {


//        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//
//        board.updateBoard(boardWithUserDto);
//
//        boardWithUserDto.getTechStackIdList().stream()
//                .map(techStackId -> techStackRepository
//                        .findById(techStackId).orElseThrow(() -> new CustomException(ErrorCode.TECH_STACK_NOT_FOUND)))
//                .map(techStack -> BoardTechStack.of(board, techStack))
//                .forEach(boardTechStackRepository::save);
//
//        List<BoardPositionRequest> boardPositionUpdateRequestList = boardWithUserDto.getBoardPositionList()
//                .stream().toList();
//
//        for (BoardPositionRequest boardPositionRequest : boardPositionUpdateRequestList) {
//            Position position = positionRepository.findById(boardPositionRequest.getPositionId())
//                    .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_FOUND));
//            BoardPosition boardPosition =
//                    BoardPosition
//                            .of(board, position, boardPositionRequest.getRequiredCount(), boardPositionRequest.getCurrentCount());
//            boardPositionRepository.save(boardPosition);
//        }
    }

//    @Override
//    public void deleteBoard(Long boardId) {
//        boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//
//        boardTechStackRepository.deleteAllByBoard_Id(boardId);
//        boardPositionRepository.deleteAllByBoard_Id(boardId);
//        applyRepository.deleteAllByBoard_Id(boardId);
//        scrapRepository.deleteAllByBoard_Id(boardId);
//        commentRepository.deleteAllByBoard_Id(boardId);
//        boardRepository.deleteById(boardId);
//    }

    @Override
    public Page<BoardDto> getBoards(Pageable pageable) {
        return null;
    }


//    public BoardResponseDto findByBoardId(Long boardId) {
//        // 제목, 내용, 대표이미지, 작성자 닉네임, 모집상태, 모집구분, 포지션리스트, 지역, 프로젝트 진행기간, 기술스택, 댓글, 작성날짜, 조회수
//
//        // 제목, 내용, 게시글 번호(ID), 모집상태, 조회수, 북마크 해간놈 boolean, 진행방식(지역), 기술스택 dto, 진행기간, 시작일, 모집마감일, 포지션 dto, 작성자정보 dto, BaseTimeEntity
//
//        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//
//        List<BoardPositionDto> positions = new ArrayList<>();
//        List<BoardTechStackDto> techStacks = new ArrayList<>();
//        List<CommentResponseDto> comments = new ArrayList<>();
//
//        List<BoardPosition> boardPositions = boardPositionRepository.findByBoard_Id(boardId);
//
//        for (BoardPosition boardPosition : boardPositions) {
//            BoardPositionDto boardPositionResponseDto = BoardPositionDto.from(boardPosition);
//            positions.add(boardPositionResponseDto);
//        }
//
//        List<BoardTechStack> boardTechStackList = boardTechStackRepository.findByBoard_Id(boardId);
//
//        for (BoardTechStack boardTechStack : boardTechStackList) {
//            BoardTechStackDto boardTechStackResponseDto = BoardTechStackDto.from(boardTechStack);
//            techStacks.add(boardTechStackResponseDto);
//        }
//
//        List<Comment> commentList = commentRepository.findByBoard_Id(boardId);
//
//        for (Comment comment : commentList) {
//            CommentResponseDto commentResponseDto = CommentResponseDto.from(comment);
//            comments.add(commentResponseDto);
//        }
//
//        BoardResponseDto getBoardResponseDto = BoardResponseDto.from(board, positions, techStacks, comments);
//
//        board.setViewCount(board.getViewCount()+1);
//
//        return getBoardResponseDto;
//
//    }

//    public List<GetBoardListResponseDto> findBoards(Pageable pageable) {
//
//        // 마감일, 제목, 포지션, 기술, 작성자닉네임, 조회수, 댓글수
//        Page<Board> boardPage = boardRepository.findAll(pageable);
//
//        List<GetBoardListResponseDto> getBoardListResponseDtoList = new ArrayList<>();
//
//        for (Board board : boardPage) {
//
//            List<Comment> commentList = commentRepository.findByBoard_Id(board.getId());
//            Long commentCount = (long) commentList.size();
//
//            List<BoardPosition> boardPositions = boardPositionRepository.findByBoard_Id(board.getId());
//            List<Position> positions = boardPositions.stream()
//                    .map(BoardPosition::getPosition)
//                    .toList();
//
//            List<BoardTechStack> boardTechStackList = boardTechStackRepository.findByBoard_Id(board.getId());
//            List<TechStack> techStacks = boardTechStackList.stream()
//                    .map(BoardTechStack::getTechStack)
//                    .toList();
//
//            GetBoardListResponseDto getBoardListResponseDto = GetBoardListResponseDto.from(board, commentCount, positions, techStacks);
//
//            getBoardListResponseDtoList.add(getBoardListResponseDto);
//        }
//
//        return getBoardListResponseDtoList;
//    }


}
