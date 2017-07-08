package com.github.vincentrussell.swagger.json.plugin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Api(value="hello controller", description="controller that says hello")
@RequestMapping(value = "/hello")
public class HelloController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ApiOperation(value = "print the word hello", notes="get hello")
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "successful")
	})
	public String printHello() {
		return "hello";
	}

	@RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
	@ApiOperation(value = "print the word hello with the name passed in", notes="some more notes")
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "successful")
	})
	public ModelAndView hello(@PathVariable("name") String name) {

		ModelAndView model = new ModelAndView();
		model.setViewName("hello");
		model.addObject("msg", name);

		return model;

	}

}
