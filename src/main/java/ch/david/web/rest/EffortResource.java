package ch.david.web.rest;

import ch.david.domain.User;
import ch.david.repository.UserRepository;
import ch.david.security.SecurityUtils;
import com.codahale.metrics.annotation.Timed;
import ch.david.domain.Effort;

import ch.david.repository.EffortRepository;
import ch.david.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
 * REST controller for managing Effort.
 */
@RestController
@RequestMapping("/api")
public class EffortResource {

    private final Logger log = LoggerFactory.getLogger(EffortResource.class);

    @Inject
    private EffortRepository effortRepository;

    @Inject
    private UserRepository userRepository;

    private void setCurrentUser(Effort effort) {
        Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        effort.setUser(user.get());
    }

    /**
     * POST  /efforts : Create a new effort.
     *
     * @param effort the effort to create
     * @return the ResponseEntity with status 201 (Created) and with body the new effort, or with status 400 (Bad Request) if the effort has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/efforts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Effort> createEffort(@Valid @RequestBody Effort effort) throws URISyntaxException {
        log.debug("REST request to save Effort : {}", effort);
        if (effort.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("effort", "idexists", "A new effort cannot already have an ID")).body(null);
        }
        setCurrentUser(effort);
        Effort result = effortRepository.save(effort);
        return ResponseEntity.created(new URI("/api/efforts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("effort", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /efforts : Updates an existing effort.
     *
     * @param effort the effort to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated effort,
     * or with status 400 (Bad Request) if the effort is not valid,
     * or with status 500 (Internal Server Error) if the effort couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/efforts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Effort> updateEffort(@Valid @RequestBody Effort effort) throws URISyntaxException {
        log.debug("REST request to update Effort : {}", effort);
        if (effort.getId() == null) {
            return createEffort(effort);
        }
        setCurrentUser(effort);
        Effort result = effortRepository.save(effort);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("effort", effort.getId().toString()))
            .body(result);
    }

    /**
     * GET  /efforts : get all the efforts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of efforts in body
     */
    @RequestMapping(value = "/efforts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Effort> getAllEfforts() {
        log.debug("REST request to get all Efforts");
        List<Effort> efforts = effortRepository.findByUserIsCurrentUser();
        return efforts;
    }

    /**
     * GET  /efforts/:id : get the "id" effort.
     *
     * @param id the id of the effort to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the effort, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/efforts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Effort> getEffort(@PathVariable Long id) {
        log.debug("REST request to get Effort : {}", id);
        Effort effort = effortRepository.findOneAndUserIsCurrentUser(id);
        return Optional.ofNullable(effort)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /efforts/:id : delete the "id" effort.
     *
     * @param id the id of the effort to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/efforts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEffort(@PathVariable Long id) {
        log.debug("REST request to delete Effort : {}", id);
        effortRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("effort", id.toString())).build();
    }

}
