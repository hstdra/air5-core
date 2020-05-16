package com.example.air5core.helpers;

import com.example.air5core.models.others.Paging;
import org.springframework.data.domain.Page;

public class MapperManager {
    public static <T> Paging<T> pageToPaging(Page<T> page) {
        Paging<T> paging = new Paging<>();
        paging.setData(page.getContent());
        paging.setHasNext(page.hasNext());
        paging.setTotalElements(page.getTotalElements());
        paging.setTotalPages(page.getTotalPages());

        return paging;
    }
}
