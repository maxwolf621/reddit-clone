package com.pttbackend.pttclone.service;

import java.util.List;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
//import java.time.ZonedDateTime;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.repository.SubRepository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Server for find/create sub
 */
@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "sub")
public class SubsService {
    private final SubRepository subRepo;
    private final AuthenticationService authenticationService;


    private final RedisTemplate<String, Object> redisTemplate;


    /** 
     * Map Sub to SubDTO
     * @param sub {@link Sub}
     * @return {@link SubDTO}
     */
    private SubDTO mapToSubDTO(Sub sub){
        return SubDTO.builder()
                  .id(sub.getId())
                  .subname(sub.getSubname())
                  .description(sub.getDescription())
                  .postsCount(sub.getPosts().size())
                  .build();
    }
    
    /** 
     * Map SubDTO to Sub
     * @param subdto {@link SubDTO}
     * @return {@link Sub}
     */
    private Sub mapToSub(SubDTO subdto){
        return Sub.builder()
                  .subname(subdto.getSubname())
                  .description(subdto.getDescription())
                  .createdDate(Instant.now())
                  .user(authenticationService.getCurrentUser())
                  .build();
    }
    
    /**
     * Get All Subs 
     * @return {@code List<SubDTO>}
     */
    @Transactional(readOnly =  true)
    @Cacheable()
    public List<SubDTO> getAllSubs(){
        return subRepo.findAll()
               .stream()
               .map(this::mapToSubDTO)
               .collect(toList());
    }
    
    /** 
     * Get A Sub via sub Id
     * @param id {@link Sub}'s id
     * @return {@code SubDTO}
     */
    @Transactional(readOnly =  true)
    public SubDTO getSubid(long id){

        String key = Long.toString(id);
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            log.info("get sub from redis");
            return (SubDTO) redisTemplate.opsForValue().get(key);
        }

        Sub sub = subRepo.findById(id).orElseThrow(() -> new RuntimeException("Sub Not Found"));

        SubDTO subDTO = mapToSubDTO(sub);
        redisTemplate.opsForValue().set(key, subDTO);
        return subDTO;
    }
    
    /**
     * Create A sub 
     * @param subdto {@link SubDTO}
     * @return {@code SubDTO}
     */
    public SubDTO save(SubDTO subdto){
        Sub sub = subRepo.save(mapToSub(subdto));
        subdto.setId(sub.getId());

        return subdto;
    }

    /**
     * Get Sub's name
     * @param subname {@link Sub}'s name
     * @return {@code SubDTO}
     */
    public SubDTO getSubname(String subname){

        if(Boolean.TRUE.equals(redisTemplate.hasKey(subname))){
            return (SubDTO) redisTemplate.opsForValue().get(subname);
        }

        Sub sub = subRepo.findBySubname(subname).orElseThrow(()-> new RuntimeException("Sub :" + subname + "Not Found"));
        SubDTO subDTO = mapToSubDTO(sub);
        redisTemplate.opsForValue().set(subname, subDTO);

        return subDTO;
    }
}