package com.procurement.evaluation.infrastructure.handler.v2

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.exception.EmptyStringException
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CreateAwardRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateAwardResult
import com.procurement.evaluation.lib.errorIfBlank
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateAwardHandler(
    private val awardService: AwardService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CreateAwardResult>(
    logger = logger,
    transform = transform,
    historyRepository = historyRepository
) {

    override val action: Action = CommandTypeV2.CREATE_AWARD

    override fun execute(descriptor: CommandDescriptor): Result<CreateAwardResult, Failure> =
        descriptor.body.asJsonNode
            .params<CreateAwardRequest>()
            .flatMap { it.validateTextAttributes() }
            .flatMap { it.convert() }
            .onFailure { return it }
            .let { params ->
                awardService.createAward(params)
            }

    private fun CreateAwardRequest.validateTextAttributes(): Result<CreateAwardRequest, DataErrors.Validation.EmptyString> {
        try {
            awards.forEachIndexed { i, award ->
                award.internalId.checkForBlank("awards[$i].internalId")
                award.description.checkForBlank("awards[$i].description")
                award.suppliers.forEachIndexed { k, supplier ->
                    supplier.name.checkForBlank("awards[$i].suppliers[$k].name")
                    supplier.identifier.scheme.checkForBlank("awards[$i].suppliers[$k].identifier.scheme")
                    supplier.identifier.id.checkForBlank("awards[$i].suppliers[$k].identifier.id")
                    supplier.identifier.legalName.checkForBlank("awards[$i].suppliers[$k].identifier.legalName")
                    supplier.identifier.uri.checkForBlank("awards[$i].suppliers[$k].identifier.uri")
                    supplier.additionalIdentifiers?.forEachIndexed { l, identifier ->
                        identifier.scheme.checkForBlank("awards[$i].suppliers[$k].additionalIdentifiers[$l].scheme")
                        identifier.id.checkForBlank("awards[$i].suppliers[$k].additionalIdentifiers[$l].id")
                        identifier.legalName.checkForBlank("awards[$i].suppliers[$k].additionalIdentifiers[$l].legalName")
                        identifier.uri.checkForBlank("awards[$i].suppliers[$k].additionalIdentifiers[$l].uri")
                    }
                    supplier.address.let { address ->
                        address.streetAddress.checkForBlank("awards[$i].suppliers[$k].address.streetAddress")
                        address.postalCode.checkForBlank("awards[$i].suppliers[$k].address.postalCode")
                        address.addressDetails.country.description.checkForBlank("awards[$i].suppliers[$k].address.addressDetails.country.description")
                        address.addressDetails.region.description.checkForBlank("awards[$i].suppliers[$k].address.addressDetails.region.description")
                        address.addressDetails.locality.description.checkForBlank("awards[$i].suppliers[$k].address.addressDetails.locality.description")
                    }
                    supplier.contactPoint.let { contactPoint ->
                        contactPoint.name.checkForBlank("awards[$i].suppliers[$k].contactPoint.name")
                        contactPoint.email.checkForBlank("awards[$i].suppliers[$k].contactPoint.email")
                        contactPoint.telephone.checkForBlank("awards[$i].suppliers[$k].contactPoint.telephone")
                        contactPoint.faxNumber.checkForBlank("awards[$i].suppliers[$k].contactPoint.faxNumber")
                        contactPoint.url.checkForBlank("awards[$i].suppliers[$k].contactPoint.url")
                    }
                    supplier.persons?.forEachIndexed { i, person ->
                        person.name.checkForBlank("awards[$i].suppliers[$k].persones.name")
                        person.identifier.let { identifier ->
                            identifier.scheme.checkForBlank("awards[$i].suppliers[$k].persones.identifier.scheme")
                            identifier.id.checkForBlank("awards[$i].suppliers[$k].persones.identifier.id")
                            identifier.uri.checkForBlank("awards[$i].suppliers[$k].persones.identifier.uri")
                        }
                        person.businessFunctions.forEachIndexed { l, businessFunction ->
                            businessFunction.id.checkForBlank("awards[$i].suppliers[$k].persones.businessFunctions[$l].id")
                            businessFunction.jobTitle.checkForBlank("awards[$i].suppliers[$k].persones.businessFunctions[$l].jobTitle")
                            businessFunction.documents?.forEachIndexed { m, document ->
                                document.description.checkForBlank("awards[$i].suppliers[$k].persones.businessFunctions[$l].documents[$m].description")
                                document.title.checkForBlank("awards[$i].suppliers[$k].persones.businessFunctions[$l].documents[$m].title")
                            }

                        }
                    }
                    supplier.details.let { details ->
                        details.mainEconomicActivities?.forEachIndexed { l, mainEconomicActivity ->
                            mainEconomicActivity.id.checkForBlank("awards[$i].suppliers[$k].details.mainEconomicActivities[$l].id")
                            mainEconomicActivity.scheme.checkForBlank("awards[$i].suppliers[$k].details.mainEconomicActivities[$l].scheme")
                            mainEconomicActivity.description.checkForBlank("awards[$i].suppliers[$k].details.mainEconomicActivities[$l].description")
                            mainEconomicActivity.uri.checkForBlank("awards[$i].suppliers[$k].details.mainEconomicActivities[$l].uri")
                        }
                        details.permits?.forEachIndexed { l, permit ->
                            permit.scheme.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].scheme")
                            permit.id.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].id")
                            permit.url.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].url")
                            permit.permitDetails.let { permitDetails ->
                                permitDetails.issuedBy.id.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].permitDetails.issuedBy.id")
                                permitDetails.issuedBy.name.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].permitDetails.issuedBy.name")
                                permitDetails.issuedThought.id.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].permitDetails.issuedThought.id")
                                permitDetails.issuedThought.name.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].permitDetails.issuedThought.name")
                            }
                        }
                        details.bankAccounts?.forEachIndexed { l, bankAccount ->
                            bankAccount.description.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].description")
                            bankAccount.bankName.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].bankName")
                            bankAccount.address.let { address ->
                                address.streetAddress.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].address.streetAddress")
                                address.postalCode.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].address.postalCode")
                                address.addressDetails.country.description.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].address.addressDetails.country.description")
                                address.addressDetails.region.description.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].address.addressDetails.region.description")
                                address.addressDetails.locality.description.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].address.addressDetails.locality.description")
                            }
                            bankAccount.identifier.id.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].bankAccount.identifier.id")
                            bankAccount.identifier.scheme.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].bankAccount.identifier.scheme")
                            bankAccount.accountIdentification.id.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].bankAccount.accountIdentification.id")
                            bankAccount.accountIdentification.scheme.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].bankAccount.accountIdentification.scheme")
                            bankAccount.additionalAccountIdentifiers?.forEachIndexed { m, additionalAccountIdentifier ->
                                additionalAccountIdentifier.id.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].additionalAccountIdentifiers[$m].id")
                                additionalAccountIdentifier.scheme.checkForBlank("awards[$i].suppliers[$k].details.permits[$l].bankAccount[$l].additionalAccountIdentifiers[$m].additionalAccountIdentifier.scheme")
                            }
                        }
                        details.legalForm?.let { legalForm ->
                            legalForm.scheme.checkForBlank("awards[$i].suppliers[$k].details.legalForm.scheme")
                            legalForm.id.checkForBlank("awards[$i].suppliers[$k].details.legalForm.id")
                            legalForm.description.checkForBlank("awards[$i].suppliers[$k].details.legalForm.description")
                            legalForm.uri.checkForBlank("awards[$i].suppliers[$k].details.legalForm.uri")
                        }
                    }

                }
                award.documents?.forEachIndexed { k, document ->
                    document.title.checkForBlank("awards[$i].documents[$k].title")
                    document.description.checkForBlank("awards[$i].documents[$k].description")
                }
            }
        } catch (exception: EmptyStringException) {
            return DataErrors.Validation.EmptyString(exception.attributeName).asFailure()
        }

        return this.asSuccess()
    }

    private fun String?.checkForBlank(name: String) = this.errorIfBlank { EmptyStringException(name) }
}

