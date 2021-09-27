package pl.adambaranowski.rsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.adambaranowski.rsbackend.model.Reservation;
import pl.adambaranowski.rsbackend.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r WHERE r.room.number = ?1 and " +
            "((r.beginDate >= ?2) or (r.beginDate <= ?2 and r.endDate >= ?3))")
    List<Reservation> findAllForRoomStartingFromDate(Integer roomNumber, LocalDate requestedStartDate, LocalDate requestedEndDate);

    List<Reservation> findByUser(User user);
}
