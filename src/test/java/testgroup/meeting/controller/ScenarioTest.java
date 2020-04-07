package testgroup.meeting.controller;

import org.junit.Test;

public class ScenarioTest {
    private MeetingRestControllerTest test = new MeetingRestControllerTest();
    @Test
    public void testAll(){
        test.testShowingAllMeetingAtStart();
        test.testAddingFirstMeeting();
        test.testAddingFirstParticipants();
        test.testDeletingFirstParticipant();
        test.testDeletingFirstMeeting();
        test.testShowingAllMeetingAtStart();
    }

}
