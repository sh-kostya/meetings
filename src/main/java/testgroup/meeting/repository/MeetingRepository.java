package testgroup.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import testgroup.meeting.model.Meeting;
import testgroup.meeting.model.Participant;

import java.util.List;
import java.util.Set;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Transactional(readOnly = true)
    default Meeting getByName(String name) {
        Meeting meeting = new Meeting();
        meeting.setName(name);
        List<Meeting> meetings = this.findAll();
        for (Meeting meetingTmp : meetings
        ) {
            if (meetingTmp.equals(meeting)) {
                return meetingTmp;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    default boolean isContainsParticipant(Participant participant, String meetingName) {
        boolean isExist = false;
        Long id = this.getByName(meetingName).getId();
        Set<Participant> participants = this.findById(id).get().getParticipantSet();
        for (Participant participantTmp : participants
        ) {
            if (participantTmp.equals(participant)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

}
