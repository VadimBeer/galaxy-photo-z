package com.vadimbeer.backendservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "model_version")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String version;

    @Column(name = "num_quantiles")
    private Integer numQuintiles;

    @Column(name = "val_loss")
    private Double validationLoss;

    @Column(name = "weights_object_key")
    private String weightObjectKey;
}
