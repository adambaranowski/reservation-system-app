package pl.adambaranowski.rsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rsbackend.api.RoomsApi;
import pl.adambaranowski.rsbackend.model.dto.RoomRequestDto;
import pl.adambaranowski.rsbackend.model.dto.RoomResponseDto;
import pl.adambaranowski.rsbackend.service.RoomService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController implements RoomsApi {
    private final RoomService roomService;

    @Override
    public ResponseEntity<Void> deleteRoomById(Integer roomNumber) {
        roomService.deleteRoomById(roomNumber);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @GetMapping("/rooms/numbers")
    public ResponseEntity<List<Integer>> getRoomsNumbers(){
        return ResponseEntity.ok(roomService.getRoomsNumbers());
    }

    @Override
    public ResponseEntity<RoomResponseDto> getRoomById(Integer roomNumber) {
        return ResponseEntity.ok(roomService.getRoomById(roomNumber));
    }

    @Override
    public ResponseEntity<RoomResponseDto> postNewRoom(RoomRequestDto roomRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.postNewRoom(roomRequestDto));
    }

    @Override
    public ResponseEntity<RoomResponseDto> updateRoom(RoomRequestDto roomRequestDto) {
        return ResponseEntity.ok(roomService.updateRoom(roomRequestDto));
    }
}
