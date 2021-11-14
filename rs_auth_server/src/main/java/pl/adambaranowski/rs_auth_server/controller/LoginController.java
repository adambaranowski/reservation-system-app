package pl.adambaranowski.rs_auth_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import pl.adambaranowski.rs_auth_server.api.LoginApi;
import pl.adambaranowski.rs_auth_server.model.dto.LoginRequestDto;
import pl.adambaranowski.rs_auth_server.model.dto.LoginResponseDto;
import pl.adambaranowski.rs_auth_server.service.JwtService;
import pl.adambaranowski.rs_auth_server.service.LoginService;

@Controller
@RequiredArgsConstructor
public class LoginController implements LoginApi {
    private final LoginService loginService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(loginService.login(loginRequestDto));
    }

}
