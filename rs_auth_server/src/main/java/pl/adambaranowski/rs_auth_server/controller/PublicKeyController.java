package pl.adambaranowski.rs_auth_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import pl.adambaranowski.rs_auth_server.api.PublicKeyApi;
import pl.adambaranowski.rs_auth_server.model.dto.PublicKeyResponseDto;
import pl.adambaranowski.rs_auth_server.service.JwtService;

@Controller
@RequiredArgsConstructor
public class PublicKeyController implements PublicKeyApi {
    public final JwtService jwtService;

    @Override
    public ResponseEntity<PublicKeyResponseDto> publicKeyGet() {
        PublicKeyResponseDto responseDto = new PublicKeyResponseDto();
        responseDto.setKey(jwtService.getPublicKey());
        return ResponseEntity.ok(responseDto);
    }
}
