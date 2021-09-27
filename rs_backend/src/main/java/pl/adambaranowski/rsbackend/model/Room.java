package pl.adambaranowski.rsbackend.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    private Integer number;

    private String description;

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @Builder.Default
    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "room")
    private Set<Equipment> equipmentItems = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "room")
    private Set<Reservation> reservations;

    public void addEquipment(Equipment equipment) {
        equipmentItems.add(equipment);
        equipment.setRoom(this);
    }

    public void removeEquipment(Equipment equipment) {
        equipmentItems.remove(equipment);
        equipment.setRoom(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(number, room.number) && Objects.equals(description, room.description) && roomStatus == room.roomStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, description, roomStatus);
    }

    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", description='" + description + '\'' +
                ", roomStatus=" + roomStatus +
                ", equipmentItems=" + equipmentItems.size() +
                '}';
    }
}
