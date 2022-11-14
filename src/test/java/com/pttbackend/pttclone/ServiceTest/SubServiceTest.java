package com.pttbackend.pttclone.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.SubRepository;

@ExtendWith(MockitoExtension.class)
public class SubServiceTest {

    @Mock
    private SubRepository subRepo; 

    public List<Sub> getSubs(){
        return IntStream.rangeClosed(1, 10).mapToObj(
            i -> this.getSubModel(i)).collect(Collectors.toList());

    }

    public Sub getSubModel(int i){
        Sub sub = new Sub();
        sub.setId(Long.valueOf(i));
        sub.setSubname("test");
        sub.setDescription("TestSub");
        sub.setCreatedDate(java.time.Instant.now());
        sub.setUser(new User());

        return sub;
    }

    @Test
    @Rollback(false)
    public void getSubID(){
        Sub sub = this.getSubModel(3);
        Long i = Long.valueOf(3);
        Mockito.when(subRepo.findById(i)).thenReturn(Optional.of(sub));
        Boolean res = subRepo.findById(i).isPresent();
        assertTrue(res);
    }

    @Test
    @Rollback(false)
    public void getAllSubs(){
        List<Sub> subs = this.getSubs();
        Mockito.when(subRepo.findAll()).thenReturn(subs);
        assertEquals(10 , subRepo.findAll().size());
    }
}
