package com.studying.backendservice.controllers;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.dto.AuthRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class NavigationController {

  @GetMapping("/login")
  public String loginPage(Model model) {
    model.addAttribute("request", new AuthRequest());
    return "login";
  }

  @PostMapping("/login")
  public String login(@ModelAttribute AuthRequest request, Model model) {
    if (request.getEmail() != null && request.getEmail().contains("@")) {
      TenantContext.setTenantId(resolveTenantId(request.getEmail()));
    } else {
      TenantContext.setTenantId("public");
    }
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    restTemplate.getMessageConverters()
        .add(new MappingJackson2HttpMessageConverter());
    restTemplate.getMessageConverters().forEach(c -> System.out.println(c.getClass()));
    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    h.add("X-Tenant-Id", TenantContext.getTenantId());
    HttpEntity<AuthRequest> entity = new HttpEntity<>(request, h);
    ResponseEntity<String> response = restTemplate.postForEntity(
        "http://localhost:8080/api/auth/login",
        entity,
        String.class);

    System.out.println(response.getHeaders());
    System.out.println(response.getStatusCode());
    System.out.println(response.getBody());

    if (response.getBody() != null) {
      return "redirect:/login";
    } else {
      model.addAttribute("error", "Invalid credentials");
      return "redirect:/login";
    }
  }

  private String resolveTenantId(String login) {
    int at = login.indexOf('@');
    return (at > 0) ? login.substring(at + 1) : "public";
  }
}
