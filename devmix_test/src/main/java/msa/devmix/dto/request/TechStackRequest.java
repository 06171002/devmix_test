package msa.devmix.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import msa.devmix.dto.BoardTechStackDto;

@Data
@AllArgsConstructor
public class TechStackRequest {

    private String techStackName;
    private String imageUrl;

    public BoardTechStackDto toDto() {
        return BoardTechStackDto.of(techStackName, imageUrl);
    }

}
