package Pets;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.*;

import com.github.javafaker.Faker;

import Entities.Pets;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PetsTestes {
		
		private static Pets pets;
		public static Faker faker;
		public static RequestSpecification request;
		
		
		@BeforeAll
		public static void setup() {
			RestAssured.baseURI = "https://petstore.swagger.io/v2";
			
			faker = new Faker();
			
			pets = new Pets(faker.idNumber().hashCode(),
					faker.name().name());
					
		}
		
		@BeforeEach
		void setRequest() {
			
			request = given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
					.header("api-key", "special-key")
					.contentType(ContentType.JSON);
		}
		
		@Test
		@Order(1)
		public void CreateNewPet_WithValidData_ReturnOk() {
			
			request
			       .body(pets)
			       .when()
			       .post("/pet")
			       .then()
			       .assertThat().statusCode(200);
		}
		
		@Test
		@Order(2)
		public void GetPet_ValidPet_ReturnOk() {
			
			request
			       .param("id", pets.getId())
			       .param("name", pets.getName())
			       .when()
			       .get("/pet/id")
			       .then()
			       .assertThat()
			       .and().time(lessThan(3000L));
			       
		}
		
		@Test
		@Order(3)
		public void UpdateName_ReturnOk() {
			
			request
			       .when()
			       .get("/pet/" + pets.getId())
			       .then()
			       .assertThat().statusCode(200).and().time(lessThan(3000L));
			
		}
		
		@Test
		@Order(4)
		public void DeletePet_PetExists_ReturnOk() {
			
			request
			       .when()
			       .delete("/pet/" + pets.getId())
			       .then()
			       .assertThat().statusCode(200).and().time(lessThan(3000L))
			       .log();
			
		}
		
		@Test
		public void CreateNewPet_WithInvalidBody_ReturnBadRequest() {
			
			Response response = request
			       .body("teste")
			       .when()
			       .post("/pet")
			       .then()
			       .extract().response();
			
			Assertions.assertNotNull(response);
			Assertions.assertEquals(400, response.statusCode());
			Assertions.assertEquals(true, response.getBody().asPrettyString().contains("unknown"));
			Assertions.assertEquals(3, response.body().jsonPath().getMap("$").size());
		}
		
	}





