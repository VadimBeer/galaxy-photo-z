package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.models.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, Integer> {

}
