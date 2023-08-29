package id.ac.ui.cs.advprog.kost.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebsiteConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000/", "https://koko-space.vercel.app/",
                        "https://frontend-muoatrwl2-marcellinuselbert.vercel.app/", "http://localhost:3001/")
            .allowedMethods("GET", "POST", "PATCH", "DELETE", "PUT");
    }
}
