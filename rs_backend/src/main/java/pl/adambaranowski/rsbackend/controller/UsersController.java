package pl.adambaranowski.rsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rsbackend.api.UsersApi;
import pl.adambaranowski.rsbackend.model.dto.UserRequestDto;
import pl.adambaranowski.rsbackend.model.dto.UserResponseDto;
import pl.adambaranowski.rsbackend.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {
    private final UserService userService;

    @Override
    public ResponseEntity<Void> deleteUserById(Integer userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(Integer userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserResponseDto> modifyUser(Integer userId, UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.modifyUser(userId, userRequestDto));
    }

    //    @CrossOrigin(origins = "http://localhost:4200")
    @Override
    public ResponseEntity<UserResponseDto> postNewUser(UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addNewUser(userRequestDto));
    }

    @Override
    public ResponseEntity<Void> modifyAuthorities(Integer userId, List<String> authorities) {
        userService.modifyAuthorities(userId, authorities);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> disableAccounts(List<Integer> usersIds) {
        userService.disableAccounts(usersIds);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> enableAccounts(List<Integer> usersIds) {
        userService.enableAccounts(usersIds);
        return ResponseEntity.ok().build();
    }
}
