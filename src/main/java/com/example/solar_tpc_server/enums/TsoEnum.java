package com.example.solar_tpc_server.enums;

import com.example.solar_tpc_server.util.TsoMessageUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public interface TsoEnum {

    String getNameKey();

    String getName2();

    default String getName() {
        String translated = TsoMessageUtil.getMessage(getNameKey());
        if (translated.equals(getNameKey())) {
            return getName2();
        }
        return translated;
    }

    static <E extends Enum<E> & TsoEnum> Optional<E> findById(
            E[] values,
            Integer id,
            Function<E, Integer> idExtractor) {
        if (id == null) {
            return Optional.empty();
        }
        return Arrays.stream(values)
                .filter(item -> id.equals(idExtractor.apply(item)))
                .findFirst();
    }

    static <E extends Enum<E> & TsoEnum> String getNameById(
            E[] values,
            Integer id,
            Function<E, Integer> idExtractor) {
        return findById(values, id, idExtractor)
                .map(TsoEnum::getName)
                .orElse("");
    }
}
