package pl.adambaranowski.rs_auth_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rs_auth_server.api.ModifyApi;
import pl.adambaranowski.rs_auth_server.model.dto.DeleteRequestDto;
import pl.adambaranowski.rs_auth_server.service.UserService;

@RestController
@RequiredArgsConstructor
public class ModifyController implements ModifyApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> deleteUser(DeleteRequestDto deleteRequestDto) {
        userService.deleteUserByEmail(deleteRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
