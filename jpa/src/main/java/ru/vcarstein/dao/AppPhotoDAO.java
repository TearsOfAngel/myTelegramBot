package ru.vcarstein.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vcarstein.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
