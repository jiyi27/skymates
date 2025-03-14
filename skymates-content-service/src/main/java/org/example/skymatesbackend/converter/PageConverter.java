package org.example.skymatesbackend.converter;


import org.example.skymatesbackend.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageConverter {

    // 泛型方法 (<T, DTO>) 不需要手动指定类型，Java 会根据 方法参数 自动推断泛型
    // 调用 convertToPageDTO 时，泛型 T 和 DTO 由 entityPage 和 converter 参数决定
    public <T, DTO> PageDTO<DTO> convertToPageDTO(Page<T> entityPage, Function<T, DTO> converter) {
        List<DTO> dtoList = entityPage.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        PageDTO<DTO> pageDTO = new PageDTO<>();
        pageDTO.setContent(dtoList);
        pageDTO.setPageNumber(entityPage.getNumber());
        pageDTO.setPageSize(entityPage.getSize());
        pageDTO.setTotalElements(entityPage.getTotalElements());
        pageDTO.setTotalPages(entityPage.getTotalPages());
        pageDTO.setHasNext(entityPage.hasNext());

        return pageDTO;
    }
}



