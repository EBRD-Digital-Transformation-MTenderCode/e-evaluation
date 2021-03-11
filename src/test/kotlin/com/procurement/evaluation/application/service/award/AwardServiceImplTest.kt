package com.procurement.evaluation.application.service.award

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.evaluation.application.model.award.consideration.DoConsiderationParams
import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntityFull
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.application.service.GenerationService
import com.procurement.evaluation.application.service.RulesService
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.get
import com.procurement.evaluation.infrastructure.bind.configuration
import com.procurement.evaluation.infrastructure.handler.v2.model.response.DoConsiderationResult
import com.procurement.evaluation.infrastructure.service.JacksonJsonTransform
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class AwardServiceImplTest {

    private lateinit var awardService: AwardService
    private lateinit var generationService: GenerationService
    private lateinit var awardRepository: AwardRepository
    private lateinit var awardManagementService: AwardManagementService
    private lateinit var awardPeriodRepository: AwardPeriodRepository
    private lateinit var transform: Transform
    private lateinit var rulesService: RulesService

    @BeforeEach
    fun init() {
        generationService = mock()
        awardRepository = mock()
        awardManagementService = mock()
        awardPeriodRepository = mock()
        transform = JacksonJsonTransform(ObjectMapper().apply { configuration() })
        rulesService = mock()
        awardService = AwardServiceImpl(
            generationService,
            awardRepository,
            awardManagementService,
            awardPeriodRepository,
            transform,
            rulesService
        )
    }

    companion object {
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!
        private val OCID = Ocid.tryCreateOrNull("ocds-b3wdp1-MD-1581509539187-EV-1581509653044")!!
        private val AWARD_ID = "dccd933c-10d1-463f-97f2-8966dfc211c8"
        private val AWARD_STATUS = AwardStatus.PENDING
        private val RELATED_BID = "bid"
    }

    @Test
    fun doConsideration_success() {
        val params = DoConsiderationParams(
            cpid = CPID,
            ocid = OCID,
            awards = listOf(DoConsiderationParams.Award(AWARD_ID))
        )
        whenever(awardManagementService.find(cpid = params.cpid, ocid = params.ocid)).thenReturn(
            listOf(stubAwardEntity(params, AWARD_ID), stubAwardEntity(params, "randomId")).asSuccess()
        )
        whenever(awardManagementService.update(cpid = eq(params.cpid), awards = any())).thenReturn(
            true.asSuccess()
        )
        val actual = awardService.doConsideration(params).get()
        val expected = DoConsiderationResult(
            awards = listOf(
                DoConsiderationResult.Award(
                id = AWARD_ID,
                status = AWARD_STATUS,
                statusDetails = AwardStatusDetails.CONSIDERATION,
                relatedBid = RELATED_BID
            ))
        )

        assertEquals(expected, actual)
    }

    @Test
    fun doConsideration_awardNotFound_fail() {
        val params = DoConsiderationParams(
            cpid = CPID,
            ocid = OCID,
            awards = listOf(DoConsiderationParams.Award(AWARD_ID))
        )
        whenever(awardManagementService.find(cpid = params.cpid, ocid = params.ocid)).thenReturn(
            listOf(stubAwardEntity(params, "randomId")).asSuccess()
        )
        whenever(awardManagementService.update(cpid = eq(params.cpid), awards = any())).thenReturn(
            true.asSuccess()
        )
        val actual = awardService.doConsideration(params) as Result.Failure

        val expectedErrorCode = "VR-4.14.1"
        val expectedErrorMessage = "Award(s) by id(s) '$AWARD_ID' not found."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedErrorMessage, actual.reason.description)

    }

    private fun stubAwardEntity(params: DoConsiderationParams, id:String) =
        AwardEntityFull.create(
            cpid = params.cpid,
            ocid = params.ocid,
            owner = null,
            award = Award(
                relatedBid = RELATED_BID,
                statusDetails = AwardStatusDetails.ACTIVE,
                status = AWARD_STATUS,
                bidDate = null,
                date = null,
                description = null,
                documents = emptyList(),
                id = id,
                internalId = null,
                items = null,
                relatedLots = emptyList(),
                requirementResponses = emptyList(),
                suppliers = emptyList(),
                title = null,
                token = UUID.randomUUID().toString(),
                value = null,
                weightedValue = null
            )
        )
}