package testgroup.meeting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meeting")
public class Meeting implements Serializable {
    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "place")
    private String place;

    @Column(name = "date")
    private String date;

    //@OneToMany(fetch = FetchType.EAGER)
    @ManyToMany(cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "participant_meeting", joinColumns = @JoinColumn(name = "meeting_id"), inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private volatile Set<Participant> participantSet = new HashSet<>();

    public Set<Participant> getParticipantSet() {
        return participantSet;
    }

    public void setParticipantSet(Set<Participant> participantSet) {
        this.participantSet = participantSet;
    }

    public void addParticipant(Participant participant) {
        participantSet.add(participant);
    }

    public void deleteParticipant(String email) {
        Participant participant = new Participant();
        participant.setEmail(email);
        for (Participant participantTmp : participantSet
        ) {
            if (participantTmp.equals(participant)) {
                participantSet.remove(participantTmp);
            }
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "name: " + name + ", place: " + place + ", date: " + date + ", participantSet: " + participantSet.toString();
    }

    @Override
    public boolean equals(Object o) {
        Meeting meeting = (Meeting) o;
        return this.getName().equals(meeting.getName());
    }

}
