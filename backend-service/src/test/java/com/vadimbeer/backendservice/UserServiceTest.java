package com.vadimbeer.backendservice;

import com.vadimbeer.backendservice.jpa.UserRepository;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.DTO.UserRequest;
import com.vadimbeer.backendservice.service.UserMapperSecurity;
import com.vadimbeer.backendservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperSecurity userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void save_shouldPersistUserAndReturnResponseWithGeneratedId() {
        UserRequest request = new UserRequest("vadim", "vadim@example.com", "raw-password");

        User mappedUser = new User(null,"vadim@example.com", "vadim", "1234", User.Role.USER);

        User savedUser = new User(1L,"vadim@example.com", "vadim", "hashed-password", User.Role.USER);

        when(userMapper.toUser(request)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);

        String id = userService.save(request);

        assertThat(id).isEqualTo("1");

        verify(userRepository).save(mappedUser);
    }

//    @Test
//    void save_shouldCallMapperExactlyOnceWithGivenRequest() {
//        UserRequest request = new UserRequest("vadim", "vadim@example.com", "raw-password");
//        User mappedUser = new User(null, "vadim@example.com", "vadim", "hashed-password", User.Role.USER);
//
//        when(userMapper.toUser(request)).thenReturn(mappedUser);
//        when(userRepository.save(any(User.class)))
//                .thenReturn(new User(1L, "vadim@example.com", "vadim", "hashed-password", User.Role.USER));
//
//        userService.save(request);
//
//        verify(userMapper).toUser(request);
//    }
}