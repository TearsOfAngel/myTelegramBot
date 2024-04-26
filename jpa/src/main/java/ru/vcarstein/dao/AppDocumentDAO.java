package ru.vcarstein.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vcarstein.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
