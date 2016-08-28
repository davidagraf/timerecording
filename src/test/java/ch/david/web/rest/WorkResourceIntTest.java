package ch.david.web.rest;

import ch.david.TimerecordingApp;
import ch.david.domain.Work;
import ch.david.repository.WorkRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WorkResource REST controller.
 *
 * @see WorkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimerecordingApp.class)
public class WorkResourceIntTest {

    private static final Integer DEFAULT_HOURS = 1;
    private static final Integer UPDATED_HOURS = 2;

    @Inject
    private WorkRepository workRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restWorkMockMvc;

    private Work work;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkResource workResource = new WorkResource();
        ReflectionTestUtils.setField(workResource, "workRepository", workRepository);
        this.restWorkMockMvc = MockMvcBuilders.standaloneSetup(workResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Work createEntity(EntityManager em) {
        Work work = new Work();
        work = new Work()
                .hours(DEFAULT_HOURS);
        return work;
    }

    @Before
    public void initTest() {
        work = createEntity(em);
    }

    @Test
    @Transactional
    public void createWork() throws Exception {
        int databaseSizeBeforeCreate = workRepository.findAll().size();

        // Create the Work

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isCreated());

        // Validate the Work in the database
        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeCreate + 1);
        Work testWork = works.get(works.size() - 1);
        assertThat(testWork.getHours()).isEqualTo(DEFAULT_HOURS);
    }

    @Test
    @Transactional
    public void checkHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRepository.findAll().size();
        // set the field null
        work.setHours(null);

        // Create the Work, which fails.

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isBadRequest());

        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorks() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the works
        restWorkMockMvc.perform(get("/api/works?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(work.getId().intValue())))
                .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)));
    }

    @Test
    @Transactional
    public void getWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get the work
        restWorkMockMvc.perform(get("/api/works/{id}", work.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(work.getId().intValue()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS));
    }

    @Test
    @Transactional
    public void getNonExistingWork() throws Exception {
        // Get the work
        restWorkMockMvc.perform(get("/api/works/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);
        int databaseSizeBeforeUpdate = workRepository.findAll().size();

        // Update the work
        Work updatedWork = workRepository.findOne(work.getId());
        updatedWork
                .hours(UPDATED_HOURS);

        restWorkMockMvc.perform(put("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWork)))
                .andExpect(status().isOk());

        // Validate the Work in the database
        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeUpdate);
        Work testWork = works.get(works.size() - 1);
        assertThat(testWork.getHours()).isEqualTo(UPDATED_HOURS);
    }

    @Test
    @Transactional
    public void deleteWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);
        int databaseSizeBeforeDelete = workRepository.findAll().size();

        // Get the work
        restWorkMockMvc.perform(delete("/api/works/{id}", work.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeDelete - 1);
    }
}
