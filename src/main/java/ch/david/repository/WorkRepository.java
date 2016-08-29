package ch.david.repository;

import ch.david.domain.Work;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Work entity.
 */
@SuppressWarnings("unused")
public interface WorkRepository extends JpaRepository<Work,Long> {

    @Query("select work from Work work where work.user.login = ?#{principal.username}")
    List<Work> findByUserIsCurrentUser();

    /**
     * findOne on Work entities which belong to the current user
     * @param id
     * @return
     */
    @Query("select work from Work work where work.user.id = :id and work.user.login = ?#{principal.username}")
    Work findOneAndUserIsCurrentUser(@Param("id") Long id);
}
