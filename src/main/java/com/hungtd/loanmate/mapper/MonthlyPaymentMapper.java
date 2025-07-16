package com.hungtd.loanmate.mapper;

import com.hungtd.loanmate.dto.MonthlyPaymentDto;
import com.hungtd.loanmate.entity.MonthlyPaymentEntity;
import com.hungtd.loanmate.model.MonthlyPaymentModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MonthlyPaymentMapper {
    MonthlyPaymentModel toModel(MonthlyPaymentEntity entity);
    MonthlyPaymentEntity toEntity(MonthlyPaymentModel model);
    MonthlyPaymentDto toDto(MonthlyPaymentModel model);
    MonthlyPaymentDto toDto(MonthlyPaymentEntity entity);
    List<MonthlyPaymentModel> toModel(List<MonthlyPaymentEntity> entities);
    List<MonthlyPaymentEntity> toEntity(List<MonthlyPaymentModel> models);
}
