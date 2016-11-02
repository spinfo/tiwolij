package tiwolij.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tiwolij.service.author.AuthorService;

@Controller
@RequestMapping("/image")
public class Image {

	@Autowired
	private AuthorService authors;

	@GetMapping({ "", "/" })
	public void image(@RequestParam(name = "id") int id, HttpServletResponse response) throws Exception {
		if (authors.getAuthor(id) == null || authors.getAuthor(id).getImage() == null)
			return;

		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		response.getOutputStream().write(authors.getAuthor(id).getImage());
		response.getOutputStream().close();
	}
}
