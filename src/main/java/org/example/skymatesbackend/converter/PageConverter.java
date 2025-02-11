package org.example.skymatesbackend.converter;


import org.example.skymatesbackend.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageConverter {

    public <T, DTO> PageDTO<DTO> convertToPageDTO(Page<T> entityPage, Function<T, DTO> converter) {
        List<DTO> dtos = entityPage.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        PageDTO<DTO> pageDTO = new PageDTO<>();
        pageDTO.setContent(dtos);
        pageDTO.setPageNumber(entityPage.getNumber());
        pageDTO.setPageSize(entityPage.getSize());
        pageDTO.setTotalElements(entityPage.getTotalElements());
        pageDTO.setTotalPages(entityPage.getTotalPages());
        pageDTO.setHasNext(entityPage.hasNext());

        return pageDTO;
    }
}



