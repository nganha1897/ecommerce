package com.ecommerce.admin.state;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;
import com.ecommerce.common.entity.StateDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	StateRepository repo;
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testListByCountries() throws Exception {
		Integer countryId = 1;
		String url = "/states/list_by_country/" + countryId;
		
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();

		StateDTO[] states = objectMapper.readValue(jsonResponse, StateDTO[].class);

		assertThat(states).hasSizeGreaterThan(0);
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testCreateState() throws JsonProcessingException, Exception {
		String url = "/states/save";
		Country country = new Country(5);
        //State toronto = new State("Toronto", country);
        State vancouver = new State("Vancouver", country);
        
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(vancouver)).with(csrf())).andDo(print())
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		System.out.println("Id: " + response);
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testUpdateState() throws JsonProcessingException, Exception {
		String url = "/states/save";
        Integer stateId = 6;
        Integer countryId = 5;
        State state = new State(stateId, "Ontario", new Country(5));
        
		mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(state)).with(csrf())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(stateId)));

		State savedState = repo.findById(stateId).get();
		assertThat(savedState.getName()).isEqualTo(state.getName());
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "nambeo2023", roles = "ADMIN")
	public void testDeleteState() throws JsonProcessingException, Exception {		
        Integer stateId = 7;
        String url = "/states/delete/" + stateId;
        
		mockMvc.perform(get(url))
				.andExpect(status().isOk());

		Optional<State> state = repo.findById(stateId);
		assertThat(state).isNotPresent();
	}
}
