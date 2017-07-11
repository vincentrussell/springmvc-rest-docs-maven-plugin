package com.github.vincentrussell.restdocs.maven.plugin.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Api(value="goodbye controller", description="controller that says goodbye")
@RequestMapping(value = "/goodbye")
public class GoodByeController {

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ApiOperation(value = "print the word goodbye", notes="get goodbye")
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "successful")
	})
	public String printgoodbye() {
		return "goodbye";
	}

	@RequestMapping(value = "/goodbye/{name:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "print the word goodbye with the name passed in", notes="some more notes")
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "successful")
	})
	@ResponseBody
	public Map<String,String> goodbye(@PathVariable("name") String name) {

		return ImmutableMap.<String,String>builder()
				.put("returnValue", "true")
				.put("name", name)
				.build();

	}

}
