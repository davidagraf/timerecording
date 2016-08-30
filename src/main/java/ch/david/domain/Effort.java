package ch.david.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Effort.
 */
@Entity
@Table(name = "effort")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Effort implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "hours", nullable = false)
    private Integer hours;

    @NotNull
    @Column(name = "day", nullable = false)
    private LocalDate day;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHours() {
        return hours;
    }

    public Effort hours(Integer hours) {
        this.hours = hours;
        return this;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public LocalDate getDay() {
        return day;
    }

    public Effort day(LocalDate day) {
        this.day = day;
        return this;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Project getProject() {
        return project;
    }

    public Effort project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public Effort user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Effort effort = (Effort) o;
        if(effort.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, effort.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Effort{" +
            "id=" + id +
            ", hours='" + hours + "'" +
            ", day='" + day + "'" +
            '}';
    }
}
