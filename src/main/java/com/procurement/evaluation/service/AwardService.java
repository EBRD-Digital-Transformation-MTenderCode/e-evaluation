package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.award.AwardBidRSDto;
import com.procurement.evaluation.model.dto.award.AwardRequestDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseAwardDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AwardService {

    ResponseDto updateAwardDto(AwardRequestDto dataDto);

    void saveAwards(List<SelectionsResponseAwardDto> awards, String cpId, String stage, String owner);

    AwardEntity updateAward(AwardBidRSDto awards, String cpId);

    List<AwardBidRSDto> getAwardsDtoFromEntity(List<AwardEntity> awardPeriodEntities);
}
