package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.application.repository.award.model.AwardEntityFull
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.Award
import org.springframework.stereotype.Service

interface AwardManagementService {
    fun find(cpid: Cpid, ocid: Ocid): Result<List<AwardEntityFull>, Failure>
    fun update(cpid: Cpid, awards: Collection<AwardEntityFull>): Result<Boolean, Failure>
}

@Service
class AwardManagementServiceImpl(
    private val awardRepository: AwardRepository,
    private val transform: Transform,
) : AwardManagementService {
    override fun find(cpid: Cpid, ocid: Ocid): Result<List<AwardEntityFull>, Failure> =
        awardRepository.findBy(cpid, ocid)
            .onFailure { return it }
            .map { entity ->
                AwardEntityFull.create(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    owner = entity.owner,
                    award = transform.tryDeserialization(entity.jsonData, Award::class.java)
                        .mapFailure {
                            Failure.Incident.Transform.ParseFromDatabaseIncident(entity.jsonData, it.exception)
                        }
                        .onFailure { return it }
                )
            }.asSuccess()

    override fun update(cpid: Cpid, awards: Collection<AwardEntityFull>): Result<Boolean, Failure> {
        val updatedAwards = awards.map { AwardEntity.create(it, transform).onFailure { return it } }
        return awardRepository.update(cpid = cpid, updatedAwards = updatedAwards)
    }
}


