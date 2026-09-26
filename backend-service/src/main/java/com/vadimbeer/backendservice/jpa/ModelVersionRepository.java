package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.DTO.ModelVersionRequest;
import com.vadimbeer.backendservice.models.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, Integer> {

}
