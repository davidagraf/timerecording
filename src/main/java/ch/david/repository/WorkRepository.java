package ch.david.repository;

import ch.david.domain.Work;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Work entity.
 */
@SuppressWarnings("unused")
public interface WorkRepository extends JpaRepository<Work,Long> {

    @Query("select work from Work work where work.user.login = ?#{principal.username}")
    List<Work> findByUserIsCurrentUser();

}
