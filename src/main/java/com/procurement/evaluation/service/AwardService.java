package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.UpdateAwardRequestDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public interface AwardService {

    ResponseDto updateAward(String cpId,
                            String stage,
                            String token,
                            String owner,
                            UpdateAwardRequestDto dataDto);

    ResponseDto getAwards(String cpId,
                          String stage,
                          String country,
                          String pmd);

    ResponseDto endAwardPeriod(String cpId,
                               String stage,
                               String country,
                               String pmd,
                               LocalDateTime endPeriod);

}
