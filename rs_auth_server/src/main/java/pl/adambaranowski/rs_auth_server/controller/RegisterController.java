package pl.adambaranowski.rs_auth_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rs_auth_server.api.RegisterApi;
import pl.adambaranowski.rs_auth_server.model.dto.RegisterRequestDto;
import pl.adambaranowski.rs_auth_server.service.UserService;

@RestController
@RequiredArgsConstructor
public class RegisterController implements RegisterApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> register(RegisterRequestDto registerRequestDto) {
        userService.addNewUser(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
