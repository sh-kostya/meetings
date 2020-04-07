package testgroup.meeting.controller;

import io.restassured.RestAssured;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import testgroup.meeting.model.Meeting;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MeetingRestControllerTest {
    private String name = "new_meeting";
    private String place = "new place";
    private String date = "04.04.2024 11:31:32";

    private String email = "kostya@gmail.com";
    private String firstName = "Konstantin";
    private String lastName = "Shaimardanov";

    private String email2 = "kostya2@gmail.com";
    private String firstName2 = "Konstantin2";
    private String lastName2 = "Shaimardanov2";

    static {
        RestAssured.baseURI = "http://localhost:8081/api/rest/meeting-management";
    }

    @Test
    public void testShowingAllMeetingAtStart() {
        JSONObject requestParams = new JSONObject();

        given()
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post("get_meetings").then()
                .contentType("application/json")
                .body(is("[]"));
    }

    @Test
    public void testAddingFirstMeeting() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("place", place);
        requestParams.put("name", name);
        requestParams.put("date", date);
        given()
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post("add_meeting").then()
                .contentType("application/json")
                .body("name", is(name), "place", is(place), "date", is(date));
    }

    @Test
    public void testAddingFirstParticipants() {
        JSONObject requestParams = new JSONObject();
        JSONArray participants = new JSONArray();

        Map<String, String> participant = new HashMap<>();
        participant.put("email", email);
        participant.put("firstName", firstName);
        participant.put("lastName", lastName);
        participants.put(participant);

        Map<String, String> participant2 = new HashMap<>();
        participant2.put("email", email2);
        participant2.put("firstName", firstName2);
        participant2.put("lastName", lastName2);
        participants.put(participant2);

        requestParams.put("name", name);
        requestParams.put("participants", participants);

        Meeting meeting = given().contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post("add_participants").then()
                .contentType("application/json")
                .extract()
                .as(Meeting.class);
        assertThat(meeting.getName(), is(name));
        assertThat(meeting.getParticipantSet().size(), is(2));
    }

    @Test
    public void testDeletingFirstParticipant() {
        JSONObject requestParams = new JSONObject();

        requestParams.put("email", email2);
        requestParams.put("name", name);
        given()
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post("delete_participant").then()
                .contentType("application/json")
                .body("name", is(name), "participantSet[0].email", is(email), "participantSet[0].firstName",
                        is(firstName), "participantSet[0].lastName", is(lastName), "participantSet[1]", is(anything()));

    }

    @Test
    public void testDeletingFirstMeeting() {
        JSONObject requestParams = new JSONObject();

        requestParams.put("name", name);

        given()
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post("delete_meeting").then()
                .contentType("application/json")
                .body(is("[]"));
    }

}










