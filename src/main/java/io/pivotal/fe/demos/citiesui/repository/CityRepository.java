package io.pivotal.fe.demos.citiesui.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.fe.demos.citiesui.model.PagedCities;

@Repository
@ConfigurationProperties(prefix="spring")
public class CityRepository {
	private static final Logger logger = LoggerFactory.getLogger(CityRepository.class);
	private String cities_ws_url;
	
	//@Autowired
	private RestTemplate restTemplate;
	
	public CityRepository() {
		restTemplate = restTemplate();
	}
	
	private RestTemplate restTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.addAll(MediaType.parseMediaTypes("application/hal+json"));
		//mediaTypes.addAll(MediaType.parseMediaTypes("application/*.hal+json"));
		converter.setSupportedMediaTypes(mediaTypes);
		converter.setObjectMapper(mapper);
		return new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
	}
	
	public PagedCities findAll(Integer page, Integer size) {
		//logger.info("Calling: " + cities_ws_url);
		ResponseEntity<PagedCities> responseEntity = restTemplate.getForEntity(cities_ws_url + "?page=" + page + "&size=" + size, PagedCities.class);
		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		return responseEntity.getBody();
	}
	
	public PagedCities findByNameContains(String name, Integer page, Integer size) {
		ResponseEntity<PagedCities> responseEntity = restTemplate.getForEntity(cities_ws_url + "/search/nameContains?q=" + name + "&page=" + page + "&size=" + size, PagedCities.class);
		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}

		return responseEntity.getBody();
	}

	public String getCities_ws_url() {
		return cities_ws_url;
	}

	public void setCities_ws_url(String cities_ws_url) {
		this.cities_ws_url = cities_ws_url;
	}
}
