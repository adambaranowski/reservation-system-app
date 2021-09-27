package pl.adambaranowski.rsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.adambaranowski.rsbackend.model.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
}
