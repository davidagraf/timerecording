package ch.david.web.rest;

import ch.david.TimerecordingApp;
import ch.david.domain.Effort;
import ch.david.repository.EffortRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EffortResource REST controller.
 *
 * @see EffortResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimerecordingApp.class)
public class EffortResourceIntTest {

    private static final Integer DEFAULT_HOURS = 1;
    private static final Integer UPDATED_HOURS = 2;

    private static final LocalDate DEFAULT_DAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DAY = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private EffortRepository effortRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restEffortMockMvc;

    private Effort effort;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EffortResource effortResource = new EffortResource();
        ReflectionTestUtils.setField(effortResource, "effortRepository", effortRepository);
        this.restEffortMockMvc = MockMvcBuilders.standaloneSetup(effortResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Effort createEntity(EntityManager em) {
        Effort effort = new Effort();
        effort = new Effort()
                .hours(DEFAULT_HOURS)
                .day(DEFAULT_DAY);
        return effort;
    }

    @Before
    public void initTest() {
        effort = createEntity(em);
    }

    @Test
    @Transactional
    public void createEffort() throws Exception {
        int databaseSizeBeforeCreate = effortRepository.findAll().size();

        // Create the Effort

        restEffortMockMvc.perform(post("/api/efforts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(effort)))
                .andExpect(status().isCreated());

        // Validate the Effort in the database
        List<Effort> efforts = effortRepository.findAll();
        assertThat(efforts).hasSize(databaseSizeBeforeCreate + 1);
        Effort testEffort = efforts.get(efforts.size() - 1);
        assertThat(testEffort.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testEffort.getDay()).isEqualTo(DEFAULT_DAY);
    }

    @Test
    @Transactional
    public void checkHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = effortRepository.findAll().size();
        // set the field null
        effort.setHours(null);

        // Create the Effort, which fails.

        restEffortMockMvc.perform(post("/api/efforts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(effort)))
                .andExpect(status().isBadRequest());

        List<Effort> efforts = effortRepository.findAll();
        assertThat(efforts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDayIsRequired() throws Exception {
        int databaseSizeBeforeTest = effortRepository.findAll().size();
        // set the field null
        effort.setDay(null);

        // Create the Effort, which fails.

        restEffortMockMvc.perform(post("/api/efforts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(effort)))
                .andExpect(status().isBadRequest());

        List<Effort> efforts = effortRepository.findAll();
        assertThat(efforts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEfforts() throws Exception {
        // Initialize the database
        effortRepository.saveAndFlush(effort);

        // Get all the efforts
        restEffortMockMvc.perform(get("/api/efforts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(effort.getId().intValue())))
                .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)))
                .andExpect(jsonPath("$.[*].day").value(hasItem(DEFAULT_DAY.toString())));
    }

    @Test
    @Transactional
    public void getEffort() throws Exception {
        // Initialize the database
        effortRepository.saveAndFlush(effort);

        // Get the effort
        restEffortMockMvc.perform(get("/api/efforts/{id}", effort.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(effort.getId().intValue()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS))
            .andExpect(jsonPath("$.day").value(DEFAULT_DAY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEffort() throws Exception {
        // Get the effort
        restEffortMockMvc.perform(get("/api/efforts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEffort() throws Exception {
        // Initialize the database
        effortRepository.saveAndFlush(effort);
        int databaseSizeBeforeUpdate = effortRepository.findAll().size();

        // Update the effort
        Effort updatedEffort = effortRepository.findOne(effort.getId());
        updatedEffort
                .hours(UPDATED_HOURS)
                .day(UPDATED_DAY);

        restEffortMockMvc.perform(put("/api/efforts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEffort)))
                .andExpect(status().isOk());

        // Validate the Effort in the database
        List<Effort> efforts = effortRepository.findAll();
        assertThat(efforts).hasSize(databaseSizeBeforeUpdate);
        Effort testEffort = efforts.get(efforts.size() - 1);
        assertThat(testEffort.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testEffort.getDay()).isEqualTo(UPDATED_DAY);
    }

    @Test
    @Transactional
    public void deleteEffort() throws Exception {
        // Initialize the database
        effortRepository.saveAndFlush(effort);
        int databaseSizeBeforeDelete = effortRepository.findAll().size();

        // Get the effort
        restEffortMockMvc.perform(delete("/api/efforts/{id}", effort.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Effort> efforts = effortRepository.findAll();
        assertThat(efforts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
