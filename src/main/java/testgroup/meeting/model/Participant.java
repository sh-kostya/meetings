package testgroup.meeting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "participant")
@JsonIgnoreProperties(value = {"meetingSet"})
public class Participant implements Serializable {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;


    public Set<Meeting> getMeetingSet() {
        return meetingSet;
    }

    public void setMeetingSet(Set<Meeting> meetingSet) {
        this.meetingSet = meetingSet;
    }

    public void addMeeting(Meeting meeting) {
        meetingSet.add(meeting);
    }

    @ManyToMany(mappedBy = "participantSet")
    @JsonIgnore
    private Set<Meeting> meetingSet = new HashSet<>();


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "email: " + email + ", firstname: " + firstName + ", lastname: " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        Participant participant = (Participant) o;
        return this.getEmail().equals(participant.getEmail());
    }
}
