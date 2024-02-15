package com.ecommerce.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ecommerce.common.entity.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CountryRepository repo;

	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testListCountries() throws Exception {
		String url = "/countries/list";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();

		Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

		assertThat(countries).hasSizeGreaterThan(0);
	}

	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testCreateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		Country country = new Country("Canada", "can");

		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		System.out.println("Id: " + response);
	}

	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
        Integer countryId = 1;
        Country country = new Country(countryId, "America", "US");
        
		mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(countryId)));

		Country savedCountry = repo.findById(countryId).get();
		assertThat(savedCountry.getName()).isEqualTo(country.getName());
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testDeleteCountry() throws JsonProcessingException, Exception {		
        Integer countryId = 3;
        String url = "/countries/delete/" + countryId;
        
		mockMvc.perform(get(url))
				.andExpect(status().isOk());

		Optional<Country> country = repo.findById(countryId);
		assertThat(country).isNotPresent();
	}

}
