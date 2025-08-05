package com.my.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.school.tsinjo_std21001.domain.*;
import com.company.school.tsinjo_std21001.repository.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HelpService {
    
    private final HelpRepository helpRepository;
    
    public List<Help> getAllHelpsOrderByDateDesc() {
        return helpRepository.findAllByOrderByCreatedAtDesc();
    }
}
