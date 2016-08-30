package ch.david.repository;

import ch.david.domain.Effort;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Effort entity.
 */
@SuppressWarnings("unused")
public interface EffortRepository extends JpaRepository<Effort,Long> {

    @Query("select effort from Effort effort where effort.user.login = ?#{principal.username}")
    List<Effort> findByUserIsCurrentUser();

    /**
     * findOne on Work entities which belong to the current user
     * @param id
     * @return
     */
    @Query("select effort from Effort effort where effort.user.id = :id and effort.user.login = ?#{principal.username}")
    Effort findOneAndUserIsCurrentUser(@Param("id") Long id);
}
