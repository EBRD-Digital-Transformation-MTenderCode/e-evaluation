package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.UpdateAwardDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AwardService {

    ResponseDto updateAward(String cpId,
                            String stage,
                            String token,
                            String owner,
                            UpdateAwardDto dataDto);

}
