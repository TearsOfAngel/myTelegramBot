package ru.vcarstein.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vcarstein.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
