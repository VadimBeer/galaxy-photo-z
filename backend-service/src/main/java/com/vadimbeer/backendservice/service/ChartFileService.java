package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.jpa.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartFileService {

    private final FileRepository fileRepository;


}
