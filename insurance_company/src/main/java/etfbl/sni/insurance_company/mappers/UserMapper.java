package etfbl.sni.insurance_company.mappers;

import etfbl.sni.insurance_company.dtos.ViewUserDto;
import etfbl.sni.insurance_company.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ViewUserDto toUserDto(User user);
}
