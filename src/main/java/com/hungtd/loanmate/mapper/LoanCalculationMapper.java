package com.hungtd.loanmate.mapper;

import com.hungtd.loanmate.dto.LoanCalculationDto;
import com.hungtd.loanmate.entity.LoanCalculationEntity;
import com.hungtd.loanmate.model.LoanCalculationModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanCalculationMapper {
    LoanCalculationModel toLoanCalculationModel(LoanCalculationEntity entity) ;
    LoanCalculationEntity toLoanCalculationEntity(LoanCalculationModel model);
    LoanCalculationDto toLoanCalculationDto(LoanCalculationModel model);
    LoanCalculationDto toLoanCalculationDto(LoanCalculationEntity entity);
}
