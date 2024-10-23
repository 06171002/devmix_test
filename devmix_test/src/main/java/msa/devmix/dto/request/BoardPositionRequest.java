package msa.devmix.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import msa.devmix.dto.BoardPositionDto;

import java.util.List;

@Data
@AllArgsConstructor
public class BoardPositionRequest {

    private String positionName;

    @Max(5)
    @Min(1)
    private Long requiredCount;
    private Long currentCount;

    public BoardPositionDto toDto() {
        return BoardPositionDto.of(
                positionName,
                requiredCount,
                currentCount
        );
    }
}
