package pl.adambaranowski.rsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rsbackend.api.LoginApi;
import pl.adambaranowski.rsbackend.model.dto.LoginRequestDto;
import pl.adambaranowski.rsbackend.model.dto.LoginResponseDto;
import pl.adambaranowski.rsbackend.model.dto.UserResponseDto;
import pl.adambaranowski.rsbackend.security.LoginService;


@RestController
@RequiredArgsConstructor
public class LoginController implements LoginApi {

    private final LoginService loginService;

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(loginService.login(loginRequestDto));
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserInfo() {
        return ResponseEntity.ok(loginService.getUserInfoFromToken());
    }
}
