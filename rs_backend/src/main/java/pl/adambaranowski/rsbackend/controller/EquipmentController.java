package pl.adambaranowski.rsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rsbackend.api.EquipmentApi;
import pl.adambaranowski.rsbackend.model.dto.EquipmentRequestDto;
import pl.adambaranowski.rsbackend.model.dto.EquipmentResponseDto;
import pl.adambaranowski.rsbackend.service.EquipmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EquipmentController implements EquipmentApi {
    private final EquipmentService equipmentService;

    @Override
    public ResponseEntity<EquipmentResponseDto> addNewEquipmentItem(EquipmentRequestDto equipmentRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.addNewEquipmentItem(equipmentRequestDto));
    }

    @Override
    public ResponseEntity<EquipmentResponseDto> deleteEquipmentItemById(Integer equipmentId) {
        equipmentService.deleteEquipmentById(equipmentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<List<EquipmentResponseDto>> getAllEquipmentItems() {
        return ResponseEntity.ok(equipmentService.getAll());
    }

    @Override
    public ResponseEntity<EquipmentResponseDto> getEquipmentItemById(Integer equipmentId) {
        return ResponseEntity.ok(equipmentService.getEquipmentItemById(equipmentId));
    }

    @Override
    public ResponseEntity<EquipmentResponseDto> updateEquipmentItemById(Integer equipmentId, EquipmentRequestDto equipmentRequestDto) {
        return ResponseEntity.ok(equipmentService.updateEquipmentItemById(equipmentId, equipmentRequestDto));
    }
}
