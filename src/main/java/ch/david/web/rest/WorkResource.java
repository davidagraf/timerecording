package ch.david.web.rest;

import ch.david.domain.User;
import ch.david.repository.UserRepository;
import ch.david.security.SecurityUtils;
import com.codahale.metrics.annotation.Timed;
import ch.david.domain.Work;

import ch.david.repository.WorkRepository;
import ch.david.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Work.
 */
@RestController
@RequestMapping("/api")
public class WorkResource {

    private final Logger log = LoggerFactory.getLogger(WorkResource.class);

    @Inject
    private WorkRepository workRepository;

    @Inject
    private UserRepository userRepository;

    private void setCurrentUser(Work work) {
        Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        work.setUser(user.get());
    }

    /**
     * POST  /works : Create a new work.
     *
     * @param work the work to create
     * @return the ResponseEntity with status 201 (Created) and with body the new work, or with status 400 (Bad Request) if the work has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/works",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Work> createWork(@Valid @RequestBody Work work) throws URISyntaxException {
        log.debug("REST request to save Work : {}", work);
        if (work.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("work", "idexists", "A new work cannot already have an ID")).body(null);

        }

        setCurrentUser(work);

        Work result = workRepository.save(work);
        return ResponseEntity.created(new URI("/api/works/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("work", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /works : Updates an existing work.
     *
     * @param work the work to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated work,
     * or with status 400 (Bad Request) if the work is not valid,
     * or with status 500 (Internal Server Error) if the work couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/works",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Work> updateWork(@Valid @RequestBody Work work) throws URISyntaxException {
        log.debug("REST request to update Work : {}", work);
        if (work.getId() == null) {
            return createWork(work);
        }

        setCurrentUser(work);

        Work result = workRepository.save(work);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("work", work.getId().toString()))
            .body(result);
    }

    /**
     * GET  /works : get all the works.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of works in body
     */
    @RequestMapping(value = "/works",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Work> getAllWorks() {
        log.debug("REST request to get all Works");
        List<Work> works = workRepository.findByUserIsCurrentUser();
        return works;
    }

    /**
     * GET  /works/:id : get the "id" work.
     *
     * @param id the id of the work to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the work, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/works/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Work> getWork(@PathVariable Long id) {
        log.debug("REST request to get Work : {}", id);
        Work work = workRepository.findOneAndUserIsCurrentUser(id);
        return Optional.ofNullable(work)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /works/:id : delete the "id" work.
     *
     * @param id the id of the work to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/works/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        log.debug("REST request to delete Work : {}", id);
        workRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("work", id.toString())).build();
    }

}
