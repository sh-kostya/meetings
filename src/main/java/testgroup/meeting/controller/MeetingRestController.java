package testgroup.meeting.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import testgroup.meeting.model.Meeting;
import testgroup.meeting.model.Participant;
import testgroup.meeting.repository.MeetingRepository;
import testgroup.meeting.service.EmailService;

@RestController
@RequestMapping(value = "/meeting-management", produces = {MediaType.APPLICATION_JSON_VALUE})
public class MeetingRestController {

    private MeetingRepository meetingRepository;
    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setMeetingRepository(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public EmailService getEmailService() {
        return emailService;
    }


    public MeetingRepository getMeetingRepository() {
        return meetingRepository;
    }

    @Transactional
    @RequestMapping(value = "/get_meetings", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    @Transactional
    @RequestMapping(value = "/add_meeting", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Meeting addMeeting(@RequestBody Meeting newMeeting) {
        List<Meeting> meetings = getAllMeetings();
        if (!meetings.contains(newMeeting)) {
            return meetingRepository.save(newMeeting);
        } else return null;
    }

    @Transactional
    @RequestMapping(value = "/delete_meeting", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<Meeting> deleteMeeting(@RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        String name = jsonObj.getString("name");
        Long id = meetingRepository.getByName(name).getId();
        if (meetingRepository.findById(id).isPresent()) {
            Meeting meeting = meetingRepository.getByName(name);
            meetingRepository.delete(meeting);
            return meetingRepository.findAll();
        } else return null;
    }

    @Transactional
    @RequestMapping(value = "/get_meeting", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Meeting getMeetingByName(@RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        String name = jsonObj.getString("name");
        Meeting meeting = meetingRepository.getByName(name);

        if (meetingRepository.findById(meeting.getId()).isPresent()) {
            return meetingRepository.findById(meeting.getId()).get();
        } else {
            return null;
        }
    }

    @Transactional
    @RequestMapping(value = "/add_participant", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Meeting addParticipant(@RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        String name = jsonObj.getString("name");
        jsonObj.remove("name");
        String tmpJson = jsonObj.toString();
        Long id = meetingRepository.getByName(name).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Participant participant = objectMapper.readValue(tmpJson, Participant.class);
            if (meetingRepository.findById(id).isPresent() & !meetingRepository.isContainsParticipant(participant, name)) {
                return meetingRepository.findById(id).map(meeting -> {
                    meeting.addParticipant(participant);
                    return meetingRepository.save(meeting);
                }).orElseGet(() -> null);
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @RequestMapping(value = "/add_participants", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Meeting addParticipants(@RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        String name = jsonObj.getString("name");
        JSONArray participants = jsonObj.getJSONArray("participants");
        Long id = meetingRepository.getByName(name).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (meetingRepository.findById(id).isPresent()) {
                for (Object obj : participants
                ) {
                    Participant participant = objectMapper.readValue(obj.toString(), Participant.class);
                    meetingRepository.findById(id).map(meeting -> {
                        meeting.addParticipant(participant);
                        return meetingRepository.save(meeting);
                    }).orElseGet(() -> null);
                }
                return meetingRepository.findById(id).get();
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @RequestMapping(value = "/delete_participant", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Meeting deleteParticipant(@RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        String name = jsonObj.getString("name");
        Long id = meetingRepository.getByName(name).getId();
        String email = jsonObj.getString("email");
        try {
            if (meetingRepository.findById(id).isPresent()) {
                Meeting meeting = meetingRepository.findById(id).get();
                Set<Participant> participants = meeting.getParticipantSet();
                Set<Participant> participantsNew = new HashSet<>(participants);
                for (Participant participant : participantsNew
                ) {
                    if (participant.getEmail().equals(email)) {
                        participants.remove(participant);
                        break;
                    }
                }
                meeting.setParticipantSet(participants);
                meetingRepository.save(meeting);
                return meeting;
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Transactional
    @RequestMapping(value = "/send_email_to_participants", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Meeting sendEmailToParticipants(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        String name = jsonObject.getString("name");
        Long id = meetingRepository.getByName(name).getId();
        if (meetingRepository.findById(id).isPresent()) {
            Meeting meeting = meetingRepository.findById(id).get();
            Set<Participant> participants = meeting.getParticipantSet();
            for (Participant participant : participants
            ) {
                try {
                    emailService.setTo(participant.getEmail());
                    String message = "Hi, " + participant.getFirstName() + " " + participant.getLastName() + "!";
                    message += " We invite you to a meeting " + meeting.getName() + ". The meeting will begin at " + meeting.getDate() + " at " + meeting.getPlace();
                    emailService.setText(message);
                    emailService.send(emailService.getMailMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return meeting;

        } else return null;

    }

}
