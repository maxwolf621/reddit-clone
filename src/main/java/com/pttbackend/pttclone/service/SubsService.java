package com.pttbackend.pttclone.service;

import java.util.List;
import static java.util.stream.Collectors.toList;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.mapper.SubMapper;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.repository.SubRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 * Server for find/create sub
 */
@Service
@AllArgsConstructor
public class SubsService {
    private final SubRepository subRepo;
    private final AuthenticationService authService;
    private final SubMapper subMapper;
    
    /**
     * @return {@code List<SubDTO>}
     */
    @Transactional(readOnly =  true)
    public List<SubDTO> getAllSubs(){
        return subRepo.findAll()
               .stream()
               .map(subMapper::mapToSubDTO)
               .collect(toList());
    }
    
    /** 
     * @param id {@link Sub}'s id
     * @return {@code SubDTO}
     */
    @Transactional(readOnly =  true)
    public SubDTO getSubID(long id){
        Sub sub = subRepo.findById(id).orElseThrow(() -> new RuntimeException("Sub Not Found"));
        return subMapper.mapToSubDTO(sub);
    }
    
    /**
     * @param subDTO {@link SubDTO}
     * @return {@code SubDTO}
     */
    public SubDTO save(SubDTO subDTO){
        Sub sub = subRepo.save(this.subMapper.mapToSub(subDTO, authService.getCurrentUser()));
        subDTO.setId(sub.getId());
        return subDTO;
    }

    /**
     * @param subname {@link Sub}'s name
     * @return {@code SubDTO}
     */
    public SubDTO getSubname(String subname){
        Sub sub = subRepo.findBySubname(subname).orElseThrow(()-> new RuntimeException("Sub :" + subname + "Not Found"));
        return subMapper.mapToSubDTO(sub);
    }
}