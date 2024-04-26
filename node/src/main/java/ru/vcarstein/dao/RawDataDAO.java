package ru.vcarstein.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vcarstein.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
