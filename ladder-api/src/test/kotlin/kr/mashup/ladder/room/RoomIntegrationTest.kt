package kr.mashup.ladder.room

import io.restassured.RestAssured
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import kr.mashup.ladder.IntegrationTest
import kr.mashup.ladder.domain.room.dto.RoomDto
import kr.mashup.ladder.room.dto.RoomCreateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class RoomIntegrationTest : IntegrationTest() {
    val `방 생성 요청값` = RoomCreateRequest(description = "스타벅스 판교점. 테마는 신나게 🎶")

    @Test
    fun `방을 생성한다`() {
        // given

        // when
        val response = `방 생성 요청`(`방 생성 요청값`)

        // then
        `방 생성됨`(`방 생성 요청값`, response)
    }

    fun `방 생성 요청`(request: RoomCreateRequest): ExtractableResponse<Response> {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .`when`().post("/api/v1/rooms")
            .then().log().all()
            .extract()
    }

    fun `방 생성됨`(request: RoomCreateRequest, response: ExtractableResponse<Response>) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())

        val created = response.jsonPath().getObject("data", RoomDto::class.java)

        assertAll(
            { assertThat(created.roomId).isNotNull() },
            { assertThat(created.description).isEqualTo(request.description) })
    }

    @Test
    fun `방을 조회한다`() {
        // given
        val 방 = `방 생성되어 있음`(`방 생성 요청값`);

        // when
        val response = `방 조회 요청`(`방`.roomId)

        // then
        `방 조회됨`(`방`, response)
    }

    fun `방 생성되어 있음`(request: RoomCreateRequest): RoomDto {
        return `방 생성 요청`(request).jsonPath().getObject("data", RoomDto::class.java)
    }

    fun `방 조회 요청`(roomId: Long): ExtractableResponse<Response> {
        return RestAssured
            .given().log().all()
            .`when`().get("/api/v1/rooms/{roomId}", roomId)
            .then().log().all()
            .extract()
    }

    fun `방 조회됨`(given: RoomDto, response: ExtractableResponse<Response>) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())

        val actual = response.jsonPath().getObject("data", RoomDto::class.java)
        assertThat(actual.roomId).isEqualTo(given.roomId)
    }
}
