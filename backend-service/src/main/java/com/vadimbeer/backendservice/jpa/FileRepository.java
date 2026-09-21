package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.models.FilesStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FilesStorage, Long> {
}
