package com.procurement.evaluation.service;
import com.procurement.evaluation.model.dto.award.AwardBidDto;
import com.procurement.evaluation.model.dto.award.AwardRequestDto;
import com.procurement.evaluation.model.dto.award.AwardResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseAwardDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AwardService {

    AwardResponseDto updateAwardDto(AwardRequestDto dataDto);

    void saveAwards(List<SelectionsResponseAwardDto> awards, String ocId);

    AwardEntity updateAward(AwardBidDto awards, String ocId);

    List<AwardBidDto> getAwardsDtoFromEntity(List<AwardEntity> awardPeriodEntities);

    List<AwardEntity> updateAwards(List<AwardBidDto> awards, String ocId);




}
